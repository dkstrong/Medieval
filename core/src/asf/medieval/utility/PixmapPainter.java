package asf.medieval.utility;

import asf.medieval.model.steer.behavior.Blend;
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
public class PixmapPainter implements InputProcessor,Disposable {

	public enum Tool {
		Fill,
		Brush,
		Point,
		Eraser;
	}

	public Pixmap pixmap;
	public Texture texture;

	private Tool tool = Tool.Brush;
	private final Brush brush = new Brush();
	private final Color brushColor = new Color();
	private float brushOpacity = 1f;

	private final Array<Pair> drawedPixels = new Array<Pair>(false, 64, Pair.class);

	public PixmapPainter(int width, int height, Pixmap.Format format) {
		pixmap = new Pixmap(width, height, format);
		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	public PixmapPainter(Texture srcTexture) {
		this(srcTexture.getWidth(), srcTexture.getHeight(),srcTexture);
	}

	public PixmapPainter(int width, int height, Texture srcTexture) {
		srcTexture.getTextureData().prepare();
		Pixmap srcPixmap = srcTexture.getTextureData().consumePixmap();
		if (srcPixmap.getFormat() != Pixmap.Format.RGBA8888) {
			UtLog.warning("pixmap was not rgba8888, weightmaps should be rgba8888");
		}

		pixmap = new Pixmap(width, height, srcPixmap.getFormat());

		if(width == srcPixmap.getWidth() && height == srcPixmap.getHeight())
		{
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					pixmap.drawPixel(x, y, srcPixmap.getPixel(x,y));
				}
			}
		}
		else
		{
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					pixmap.drawPixel(x, y, UtPixmap.getColorStretchLinear(srcPixmap, pixmap, x, y));
				}
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

	public Brush getBrush() {
		return brush;
	}

	public Color getBrushColor() {
		return brushColor;
	}

	public void setBrushColor(float r, float g, float b, float a) {
		brushColor.set(r,g,b,a);
	}

	public float getBrushOpacity() {
		return brushOpacity;
	}

	public void setBrushOpacity(float brushOpacity) {
		this.brushOpacity = UtMath.clamp(brushOpacity, 0f, 1f);
	}

	public void draw(int x, int y) {
		draw(-1,-1,x,y);
	}

	public void draw(int x1, int y1, int x2, int y2) {
		if(tool == null){
			UtLog.warning("tried to draw but no tool is selected");
			return;
		}

		if(Pixmap.getBlending() != Pixmap.Blending.None){
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		if(x1>=0 && y2 >=0){
			float dist = Vector2.dst(x1,y1,x2,y2);
			float alphaStep = UtMath.largest(brush.getRadius(), .1f) / (8f * dist);

			int count=0;
			for (float a = 0; a < 1f && count < 100; a += alphaStep) {
				int xL = Math.round(UtMath.interpolateLinear(a, x1,x2));
				int yL = Math.round(UtMath.interpolateLinear(a, y1,y2));
				drawTool(xL, yL);
				count++;
			}
		}else{
			drawedPixels.clear();
		}

		drawTool(x2, y2);
		texture.draw(pixmap, 0, 0);
	}

	private void drawTool(int x, int y){
		switch (tool) {
			case Fill:
				pixmap.setColor(brushColor);
				pixmap.fill();
				break;
			case Brush:
				//pixmap.setColor(brushColor);
				//pixmap.fillCircle(x,y+brushRadius*2+brushRadius,brushRadius);
				for (Pair pair : brush.pairs) {

					drawPoint(x+pair.x, y+pair.y,brushOpacity);
				}
				break;
			case Point:
				drawPoint(x,y,brushOpacity);
				break;
			case Eraser:
				for (Pair pair : brush.pairs) {
					erasePoint(x + pair.x, y + pair.y, brushOpacity);
				}
				break;
		}
	}

	private void drawPoint(int x, int y, float opacity){
		if(!hasDrawnPixel(x, y,opacity)){
			Color c;
			if(opacity < 1f)
			{
				c = new Color();
				Color.rgba8888ToColor(c,pixmap.getPixel(x, y));

				c.r = opacity * brushColor.r + (1-opacity) * c.r;
				c.g = opacity * brushColor.g + (1-opacity) * c.g;
				c.b = opacity * brushColor.b + (1-opacity) * c.b;
				c.a = opacity * brushColor.a + (1-opacity) * c.a;
				//UtDebugPrint.print(c);
			}else{
				c = brushColor;
			}

			pixmap.drawPixel(x, y, Color.rgba8888(c));

			drawedPixels.add(new Pair(x, y,opacity));
		}
	}

	private void erasePoint(int x, int y, float opacity){
		if(!hasDrawnPixel(x, y,opacity)){
			Color c;
			c = new Color();
			Color.rgba8888ToColor(c,pixmap.getPixel(x, y));

			c.r = UtMath.largest((1-opacity) * c.r - opacity * brushColor.r ,0f);
			c.g = UtMath.largest((1-opacity) * c.g - opacity * brushColor.g ,0f);
			c.b = UtMath.largest((1-opacity) * c.b - opacity * brushColor.b ,0f);
			c.a = UtMath.largest((1-opacity) * c.a - opacity * brushColor.a ,0f);

			pixmap.drawPixel(x, y, Color.rgba8888(c));
			drawedPixels.add(new Pair(x, y,opacity));
		}
	}

	private boolean hasDrawnPixel(int x, int y, float opacity){
		for (Pair drawedPixel : drawedPixels) {
			if(drawedPixel.equals(x,y,opacity))
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
				if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
					brush.setRadius(brush.getRadius()-1);
				}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setBrushOpacity(brushOpacity - 0.1f);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
					brush.setRadius(brush.getRadius()+1);
				}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					setBrushOpacity(brushOpacity + 0.1f);
				}
				return true;
			case Input.Keys.B:
				tool = Tool.Brush;
				return true;
			case Input.Keys.E:
				tool = Tool.Eraser;
				return true;
			case Input.Keys.F:
				tool = Tool.Fill;
				return true;
			case Input.Keys.P:
				tool = Tool.Point;
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
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
			brush.setRadius(brush.getRadius()-amount);
			return true;
		}else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
			setBrushOpacity(brushOpacity-amount * 0.05f);
			return true;
		}
		return false;
	}

}
