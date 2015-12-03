package asf.medieval.painter;

import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/30/15.
 */
public class PixmapPainterDelegate implements PainterDelegate {

	public Painter painter;
	public Pixmap pixmap;
	public Texture texture;

	public PixmapPainterDelegate(int width, int height, Pixmap.Format format) {
		pixmap = new Pixmap(width, height, format);
	}

	public PixmapPainterDelegate(Texture srcTexture) {
		this(srcTexture.getWidth(), srcTexture.getHeight(), srcTexture);
	}

	public PixmapPainterDelegate(int width, int height, Texture srcTexture) {
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
	}

	@Override
	public void setPainter(Painter painter) {
		this.painter = painter;
		texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	@Override
	public HistoryState createHistory() {
		return new HistoryState(pixmap);
	}

	@Override
	public HistoryState createHistory(HistoryState currentState, Array<Point> affectedPixels) {
		return new HistoryState(currentState, affectedPixels);
	}

	@Override
	public void recall(HistoryState historyState) {

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for(int x=0; x<historyState.pixelData.length; x++){
			for(int y=0; y<historyState.pixelData[0].length; y++){
				pixmap.drawPixel(x, y, historyState.pixelData[x][y]);
			}
		}
	}

	@Override
	public void recall(HistoryState historyState, Array<Point> affectedPixels) {
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (Point affectedPixel : affectedPixels) {
			pixmap.drawPixel(affectedPixel.x, affectedPixel.y, historyState.pixelData[affectedPixel.x][affectedPixel.y]);
		}
	}

	@Override
	public int drawPoint(int currentColor, Color brushColor, int x, int y, float opacity) {
		Color c;
		if (opacity < 1f) {
			c = new Color();
			//int currentColor = pixmap.getPixel(x, y);
			Color.rgba8888ToColor(c, currentColor );

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
		return colorCode;
	}

	@Override
	public int erasePoint(int currentColor,Color brushColor, int x, int y, float opacity) {
		Color c;
		c = new Color();
		Color.rgba8888ToColor(c, currentColor);

		c.r = UtMath.largest((1 - opacity) * c.r - opacity * brushColor.r, 0f);
		c.g = UtMath.largest((1 - opacity) * c.g - opacity * brushColor.g, 0f);
		c.b = UtMath.largest((1 - opacity) * c.b - opacity * brushColor.b, 0f);
		c.a = UtMath.largest((1 - opacity) * c.a - opacity * brushColor.a, 0f);

		int colorCode = Color.rgba8888(c);

		pixmap.drawPixel(x, y, colorCode);
		return colorCode;
	}

	@Override
	public void output()
	{
		texture.draw(pixmap,0,0);
	}

	@Override
	public void output(FileHandle fh)
	{
		PixmapIO.writePNG(fh, pixmap);
	}

	@Override
	public int getWidth() {
		return pixmap.getWidth();
	}

	@Override
	public int getHeight() {
		return pixmap.getHeight();
	}

	@Override
	public void dispose(){
		texture.dispose();
		pixmap.dispose();
	}

}
