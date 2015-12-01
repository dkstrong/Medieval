package asf.medieval.painter;

import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Interpolation;
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



	public interface PixmapCoordProvider{
		public void getPixmapCoord(int screenX, int screenY, int pixmapWidth, int pixmapHeight, Vector2 store);
	}

	public PixmapCoordProvider coordProvider;

	public PainterDelegate painterDelegate;
	public final History history=new History();
	private boolean previewPainting = false;

	private Tool tool = Tool.Brush;
	private final Brush brush = new Brush();
	private final Color brushColor = new Color();
	private float brushOpacity = 1f;

	private boolean previewDrawMode = true;
	private boolean leftDown;
	private int lastX = -1;
	private int lastY = -1;

	private boolean[][] drawedPixels;
	private final Array<Point> affectedPixels = new Array<Point>(false, 64, Point.class);

	public PixmapPainter(PainterDelegate painterDelegate) {
		this.painterDelegate = painterDelegate;
		painterDelegate.setPainter(this);
		history.resetHistory(painterDelegate);
		drawedPixels = new boolean[painterDelegate.getWidth()][painterDelegate.getHeight()];
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

	public void setBrushColor(Color c) {
		brushColor.set(c);
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
			final float dist = Vector2.dst(x1, y1, x2, y2);
			final float alphaStep = UtMath.largest(brush.getRadius(), 1f) / (8f * dist);

			for (float a = 0; a < 1f ; a += alphaStep) {
				int xL = Math.round(Interpolation.linear.apply(x1, x2, a));
				int yL = Math.round(Interpolation.linear.apply(y1, y2, a));
				drawTool(xL, yL);
			}
		} else {
			clearDrawedPixels();
		}

		drawTool(x2, y2);
		painterDelegate.output();

	}

	private void drawTool(int x, int y) {
		switch (tool) {
			case Fill:
				for(int fx=0; fx<painterDelegate.getWidth(); fx++){
					for(int fy=0; fy<painterDelegate.getHeight(); fy++){
						drawPoint(fx,fy,brushOpacity);
					}
				}
				break;
			case Brush:
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
		if(x < 0 || x >= painterDelegate.getWidth() || y < 0 || y>= painterDelegate.getHeight())
			return;
		if (!hasDrawnPixel(x, y)) {
			int colorCode = painterDelegate.drawPoint(brushColor,x,y,opacity);
			drawedPixels[x][y] = true;
			affectedPixels.add(new Point(x,y,colorCode));
		}
	}

	private void erasePoint(int x, int y, float opacity) {
		if(x < 0 || x >= painterDelegate.getWidth() || y < 0 || y>= painterDelegate.getHeight())
			return;
		if (!hasDrawnPixel(x, y)) {
			int colorCode = painterDelegate.erasePoint(brushColor, x, y, opacity);
			drawedPixels[x][y] = true;
			affectedPixels.add(new Point(x,y,colorCode));
		}
	}

	private void clearDrawedPixels(){
		for(int x=0; x<drawedPixels.length; x++){
			for(int y=0; y< drawedPixels.length; y++){
				drawedPixels[x][y] = false;
			}
		}
	}

	private boolean hasDrawnPixel(int x, int y) {
		return drawedPixels[x][y];
	}

	public void output(FileHandle fh){
		history.recallHistory(painterDelegate, history.history.size - 1); // ensure preview is not going to show up int he file..
		painterDelegate.output(fh);
	}

	@Override
	public void dispose() {
		painterDelegate.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {

			case Input.Keys.Z:
				if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
					if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
						history.recallHistory(painterDelegate, history.currentHistory + 1);
					}else{
						history.recallHistory(painterDelegate, history.currentHistory - 1);
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
			touchPaint(screenX,screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			if(leftDown){
				//history.addHistory(pixmap);
				history.addHistory(painterDelegate,affectedPixels);
				affectedPixels.clear();
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
			touchPaint(screenX,screenY);
			return true;
		}
		return false;
	}

	private static final Vector2 tempStore= new Vector2();
	private void touchPaint(int screenX, int screenY)
	{
		if(previewDrawMode){
			history.undoPreview(painterDelegate, affectedPixels);
			affectedPixels.clear();
			previewDrawMode=false;
		}
		if(coordProvider == null){
			throw new IllegalStateException("coord provider is null");
		}
		coordProvider.getPixmapCoord(screenX, screenY, painterDelegate.getWidth(), painterDelegate.getHeight(),tempStore);
		int texX = Math.round(tempStore.x);
		int texY = Math.round(tempStore.y);
		draw(lastX, lastY, texX, texY);
		lastX = texX;
		lastY = texY;
	}


	public void updateInput(float delta){
		if(!leftDown && previewPainting && coordProvider != null){
			previewDrawMode = true;
			history.undoPreview(painterDelegate, affectedPixels);
			affectedPixels.clear();

			coordProvider.getPixmapCoord(Gdx.input.getX(), Gdx.input.getY(), painterDelegate.getWidth(), painterDelegate.getHeight(),tempStore);
			int texX = Math.round(tempStore.x);
			int texY = Math.round(tempStore.y);
			draw(-1,-1,texX, texY);
		}
	}

	public boolean isPreviewPainting() {
		return previewPainting;
	}

	public void setPreviewPainting(boolean previewPainting) {
		this.previewPainting = previewPainting;

		if(!previewPainting && previewDrawMode){
			history.undoPreview(painterDelegate, affectedPixels);
			affectedPixels.clear();
			previewDrawMode=false;
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		return false;
	}

}
