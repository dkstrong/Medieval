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
public class PixmapPainter implements InputProcessor, Disposable {

	public enum Tool {
		Fill, Brush, Eraser
	}

	public static class History{
		private final Array<Point> affectedPixels = new Array<Point>(false, 64, Point.class);
	}

	public interface PixmapCoordProvider{
		public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store);
	}

	public PixmapCoordProvider coordProvider;

	public Pixmap pixmap;
	public Texture texture;
	public int maxHistory = 64;
	public boolean previewPainting = true;

	private Tool tool = Tool.Brush;
	private final Brush brush = new Brush();
	private final Color brushColor = new Color();
	private float brushOpacity = 1f;

	private boolean previewDrawMode = true;
	private boolean leftDown;
	private int lastX = -1;
	private int lastY = -1;
	private int currentHistory = 0;
	private final Array<Pixmap> history = new Array<Pixmap>(true, maxHistory, Pixmap.class);

	private final Array<Point> drawedPixels = new Array<Point>(false, 64, Point.class);
	private final Array<Point> affectedPixels = new Array<Point>(false, 64, Point.class);

	public PixmapPainter(int width, int height, Pixmap.Format format) {
		pixmap = new Pixmap(width, height, format);
		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
		addHistory();
	}

	public PixmapPainter(Texture srcTexture) {
		this(srcTexture.getWidth(), srcTexture.getHeight(), srcTexture);
	}

	public PixmapPainter(int width, int height, Texture srcTexture) {
		srcTexture.getTextureData().prepare();
		Pixmap srcPixmap = srcTexture.getTextureData().consumePixmap();
		if (srcPixmap.getFormat() != Pixmap.Format.RGBA8888) {
			UtLog.warning("pixmap was not rgba8888, weightmaps should be rgba8888");
		}

		pixmap = new Pixmap(width, height, srcPixmap.getFormat());
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}
		if (width == srcPixmap.getWidth() && height == srcPixmap.getHeight()) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					pixmap.drawPixel(x, y, srcPixmap.getPixel(x, y));
				}
			}
		} else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					pixmap.drawPixel(x, y, UtPixmap.getColorStretchLinear(srcPixmap, pixmap, x, y));
				}
			}
		}

		srcPixmap.dispose();

		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));

		addHistory();

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
		brushColor.set(r, g, b, a);
	}

	public float getBrushOpacity() {
		return brushOpacity;
	}

	public void setBrushOpacity(float brushOpacity) {
		this.brushOpacity = UtMath.clamp(brushOpacity, 0f, 1f);
	}

	public void draw(int x, int y) {
		draw(-1, -1, x, y);
	}

	public void draw(int x1, int y1, int x2, int y2) {
		if (tool == null) {
			UtLog.warning("tried to draw but no tool is selected");
			return;
		}

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		if (x1 >= 0 && y2 >= 0) {
			float dist = Vector2.dst(x1, y1, x2, y2);
			float alphaStep = UtMath.largest(brush.getRadius(), .1f) / (8f * dist);

			int count = 0;
			for (float a = 0; a < 1f && count < 100; a += alphaStep) {
				int xL = Math.round(UtMath.interpolateLinear(a, x1, x2));
				int yL = Math.round(UtMath.interpolateLinear(a, y1, y2));
				drawTool(xL, yL);
				count++;
			}
		} else {
			drawedPixels.clear();
			affectedPixels.clear();
		}

		drawTool(x2, y2);
		texture.draw(pixmap, 0, 0);
	}

	private void drawTool(int x, int y) {
		switch (tool) {
			case Fill:
				pixmap.setColor(brushColor);
				pixmap.fill();
				break;
			case Brush:
				//pixmap.setColor(brushColor);
				//pixmap.fillCircle(x,y+brushRadius*2+brushRadius,brushRadius);
				for (Point point : brush.pairs) {
					drawPoint(x + point.x, y + point.y, brushOpacity);
				}
				break;
			case Eraser:
				for (Point point : brush.pairs) {
					erasePoint(x + point.x, y + point.y, brushOpacity);
				}
				break;
		}
	}

	private void drawPoint(int x, int y, float opacity) {
		if (!hasDrawnPixel(x, y)) {
			Color c;
			if (opacity < 1f) {
				c = new Color();
				Color.rgba8888ToColor(c, pixmap.getPixel(x, y));

				c.r = opacity * brushColor.r + (1 - opacity) * c.r;
				c.g = opacity * brushColor.g + (1 - opacity) * c.g;
				c.b = opacity * brushColor.b + (1 - opacity) * c.b;
				c.a = opacity * brushColor.a + (1 - opacity) * c.a;
				//UtDebugPrint.print(c);
			} else {
				c = brushColor;
			}

			int colorCode = Color.rgba8888(c);
			pixmap.drawPixel(x, y, colorCode);
			drawedPixels.add(new Point(x, y, colorCode));
			affectedPixels.add(new Point(x,y,colorCode));
		}
	}

	private void erasePoint(int x, int y, float opacity) {
		if (!hasDrawnPixel(x, y)) {
			Color c;
			c = new Color();
			Color.rgba8888ToColor(c, pixmap.getPixel(x, y));

			c.r = UtMath.largest((1 - opacity) * c.r - opacity * brushColor.r, 0f);
			c.g = UtMath.largest((1 - opacity) * c.g - opacity * brushColor.g, 0f);
			c.b = UtMath.largest((1 - opacity) * c.b - opacity * brushColor.b, 0f);
			c.a = UtMath.largest((1 - opacity) * c.a - opacity * brushColor.a, 0f);

			int colorCode = Color.rgba8888(c);

			pixmap.drawPixel(x, y, colorCode);
			drawedPixels.add(new Point(x, y, colorCode));
			affectedPixels.add(new Point(x,y,colorCode));
		}
	}

	private boolean hasDrawnPixel(int x, int y) {
		for (Point drawedPixel : drawedPixels) {
			if(drawedPixel.x == x && drawedPixel.y == y)
				return true;
		}
		return false;
	}


	public void addHistory() {
		// create duplicate the current pixmap
		// to be stored in history
		Pixmap pixmapHistory = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}
		for (int x = 0; x < pixmapHistory.getWidth(); x++) {
			for (int y = 0; y < pixmapHistory.getHeight(); y++) {
				pixmapHistory.drawPixel(x, y, pixmap.getPixel(x, y));
			}
		}

		// if the insertion point is in the past
		// then we delete history that happens in the future
		while(currentHistory < history.size -1){
			Pixmap lostHistory = history.removeIndex(currentHistory+1);
			lostHistory.dispose();
		}

		// add new state to the history stack
		history.add(pixmapHistory);

		// delete history too old to store to save memory
		if (history.size > maxHistory) {
			Pixmap lostHistory = history.removeIndex(0);
			lostHistory.dispose();
		}

		// we are now viewing the latest point in history
		currentHistory=history.size - 1;

	}

	public void recallHistoryFast(int index)
	{
		if(history.size <=0){
			System.out.println("history is empty");
			return;
		}
		if (index < 0) {
			index =0;
			System.out.println("there is no history at: " + index + ", using index=" + index);
		} else if (index >= history.size) {
			index = history.size-1;
			System.out.println("there is no history at: " + index + ", using index=" + index);
		}
		currentHistory = index;
		Pixmap pixmapHistory = history.get(currentHistory);

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (Point drawedPixel : affectedPixels) {
			pixmap.drawPixel(drawedPixel.x, drawedPixel.y, pixmapHistory.getPixel(drawedPixel.x,drawedPixel.y));
		}


		texture.draw(pixmap, 0, 0);
	}

	public void recallHistory(int index) {
		if(history.size <=0){
			System.out.println("history is empty");
			return;
		}
		if (index < 0) {
			index =0;
			System.out.println("there is no history at: " + index + ", using index=" + index);
		} else if (index >= history.size) {
			index = history.size-1;
			System.out.println("there is no history at: " + index + ", using index=" + index);
		}
		currentHistory = index;
		Pixmap pixmapHistory = history.get(currentHistory);

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (int x = 0; x < pixmapHistory.getWidth(); x++) {
			for (int y = 0; y < pixmapHistory.getHeight(); y++) {
				pixmap.drawPixel(x, y, pixmapHistory.getPixel(x, y));
			}
		}

		texture.draw(pixmap, 0, 0);

	}


	@Override
	public void dispose() {
		texture.dispose();
		pixmap.dispose();

		while (history.size > 0) {
			history.removeIndex(0).dispose();
		}
	}

	private static final Vector2 tempStore= new Vector2();
	public void updateInput(float delta){
		if(leftDown){
			if(previewDrawMode){
				recallHistoryFast(currentHistory);
				previewDrawMode=false;
			}
			if(coordProvider == null){
				throw new IllegalStateException("coord provider is null");
			}
			coordProvider.getPixmapCoord(Gdx.input.getX(), Gdx.input.getY(), pixmap.getWidth(), pixmap.getHeight(),tempStore);
			int texX = Math.round(tempStore.x);
			int texY = Math.round(tempStore.y);
			draw(lastX, lastY, texX, texY);
			lastX = texX;
			lastY = texY;
		}else if(previewPainting){
			previewDrawMode = tool == Tool.Brush || tool == Tool.Eraser || previewDrawMode;
			if(previewDrawMode){
				recallHistoryFast(currentHistory);

				if(coordProvider == null){
					throw new IllegalStateException("coord provider is null");
				}

				coordProvider.getPixmapCoord(Gdx.input.getX(), Gdx.input.getY(), pixmap.getWidth(), pixmap.getHeight(),tempStore);
				int texX = Math.round(tempStore.x);
				int texY = Math.round(tempStore.y);
				draw(texX, texY);
			}
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					brush.setRadius(brush.getRadius() - 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					setBrushOpacity(brushOpacity - 0.1f);
				}
				return true;
			case Input.Keys.RIGHT_BRACKET:
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					brush.setRadius(brush.getRadius() + 1);
				} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
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
			case Input.Keys.Z:
				if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
					if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
						recallHistory(currentHistory+1);
					}else{
						recallHistory(currentHistory-1);
					}
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			leftDown = true;
			lastX = -1;
			lastY = -1;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			if(leftDown){
				addHistory();
				leftDown = false;
				lastX = -1;
				lastY = -1;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(leftDown)
		{
			return true;
		}
		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			brush.setRadius(brush.getRadius() - amount);
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			setBrushOpacity(brushOpacity - amount * 0.05f);
			return true;
		}
		return false;
	}

}
