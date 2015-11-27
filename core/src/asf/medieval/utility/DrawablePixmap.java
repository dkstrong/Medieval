package asf.medieval.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/26/15.
 */
public class DrawablePixmap implements InputProcessor,Disposable {

	public enum Tool {
		Fill,
		FloodFill,
		HardBrush,
		SoftBrush,
		OutlineBrush

	}

	public Pixmap pixmap;
	public Texture texture;

	public Pixmap.Blending blending = Pixmap.Blending.None;
	private Tool tool = Tool.SoftBrush;
	private final Color brushColor = new Color();
	private int brushRadius = 1;
	private float brushOpacity = 1f;

	private final Array<Pair> drawedPixels = new Array<Pair>(false, 64, Pair.class);

	public DrawablePixmap(int width, int height, Pixmap.Format format) {
		pixmap = new Pixmap(width, height, format);
		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	public DrawablePixmap(Texture srcTexture) {
		srcTexture.getTextureData().prepare();
		Pixmap srcPixmap = srcTexture.getTextureData().consumePixmap();
		if (srcPixmap.getFormat() != Pixmap.Format.RGBA8888) {
			UtLog.warning("pixmap was not rgba8888, weightmaps should be rgba8888");
		}

		pixmap = new Pixmap(srcPixmap.getWidth(), srcPixmap.getHeight(), srcPixmap.getFormat());

		for (int x = 0; x < srcPixmap.getWidth(); x++) {
			for (int y = 0; y < srcPixmap.getHeight(); y++) {
				pixmap.drawPixel(x, y, srcPixmap.getPixel(x, y));
			}
		}

		srcPixmap.dispose();

		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public Color getBrushColor() {
		return brushColor;
	}

	public void setBrushColor(float r, float g, float b, float a) {
		brushColor.set(r,g,b,a);

	}

	public int getBrushRadius() {
		return brushRadius;
	}

	public void setBrushRadius(int brushRadius) {
		this.brushRadius = brushRadius;
		if (this.brushRadius < 0)
			this.brushRadius = 0;
	}

	public float getBrushOpacity() {
		return brushOpacity;
	}

	public void setBrushOpacity(float brushOpacity) {
		this.brushOpacity = UtMath.clamp(brushOpacity, 0f, 1f);
	}


	private void prepareDraw()
	{
		if(tool == null){
			UtLog.warning("tried to draw but no tool is selected");
			return;
		}

		if(blending == null)
			blending = Pixmap.Blending.None;

		Pixmap.setBlending(blending);
	}

	public void draw(int x, int y) {
		prepareDraw();

		drawTool(x, y);

		texture.draw(pixmap, 0, 0);
	}

	public void draw(int x1, int y1, int x2, int y2) {
		prepareDraw();

		if(x1>=0 && y2 >=0){
			float dist = Vector2.dst(x1,y1,x2,y2);
			float alphaStep = UtMath.largest(brushRadius, .1f) / (8f * dist);

			int count=0;
			for (float a = 0; a < 1f && count < 100; a += alphaStep) {
				int xL = Math.round(UtMath.interpolateLinear(a, x1,x2));
				int yL = Math.round(UtMath.interpolateLinear(a, y1,y2));
				if(!hasDrawnPixel(xL, yL)){
					drawTool(xL, yL);
					drawedPixels.add(new Pair(xL, yL));
				}
				count++;
			}
		}else{
			drawedPixels.clear();
		}

		if(!hasDrawnPixel(x2, y2)){
			drawTool(x2, y2);
			drawedPixels.add(new Pair(x2, y2));
		}


		texture.draw(pixmap, 0, 0);
	}

	private void drawTool(int x, int y){
		switch (tool) {
			case Fill:
				pixmap.setColor(brushColor);
				pixmap.fill();
				break;
			case FloodFill:
				pixmap.setColor(brushColor);
				pixmap.fill(); // TODO: flood fill algorithm
				break;
			case OutlineBrush:
				pixmap.setColor(brushColor);
				pixmap.drawCircle(x, y, brushRadius);
				break;
			case HardBrush:
				pixmap.setColor(brushColor);
				pixmap.fillCircle(x,y,brushRadius);
				break;
			case SoftBrush:
				Color c = new Color();
				Color.rgba8888ToColor(c,pixmap.getPixel(x, y));

				c.r = brushOpacity * brushColor.r + (1-brushOpacity) * c.r;
				c.g = brushOpacity * brushColor.g + (1-brushOpacity) * c.g;
				c.b = brushOpacity * brushColor.b + (1-brushOpacity) * c.b;
				c.a = brushOpacity * brushColor.a + (1-brushOpacity) * c.a;


				pixmap.drawPixel(x, y, Color.rgba8888(c));
				break;
		}
	}

	private boolean hasDrawnPixel(int x, int y){
		for (Pair drawedPixel : drawedPixels) {
			if(drawedPixel.equals(x,y))
				return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		texture.dispose();
		pixmap.dispose();
	}



	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		switch(keycode)
		{
			case Input.Keys.LEFT_BRACKET:
				if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setBrushOpacity(brushOpacity - 0.1f);
				}else{
					setBrushRadius(brushRadius-1);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setBrushOpacity(brushOpacity + 0.1f);
				}else{
					setBrushRadius(brushRadius+1);
				}
				return true;
			case Input.Keys.B:
				if(tool == Tool.HardBrush){
					tool = Tool.SoftBrush;
				}else if(tool == Tool.SoftBrush){
					tool = Tool.OutlineBrush;
				}else if(tool == Tool.OutlineBrush){
					tool = Tool.HardBrush;
				}else{
					tool = Tool.HardBrush;
				}
				return true;
			case Input.Keys.F:
				if(tool == Tool.FloodFill){
					tool = Tool.Fill;
				}else if(tool == Tool.Fill){
					tool = Tool.FloodFill;
				}else{
					tool = Tool.FloodFill;
				}
				return true;

		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
			if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)){
				setBrushRadius(brushRadius-amount);
				return true;
			}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
				setBrushOpacity(brushOpacity-amount * 0.1f);
				return true;
			}

		}
		return false;
	}
}
