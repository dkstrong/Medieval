package asf.medieval.painter;

import asf.medieval.utility.UtLog;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 11/30/15.
 */
public class PixmapPainterModel implements PainterModel {

	private PixmapPainter painter;
	public Pixmap pixmap;
	public Texture texture;

	public PixmapPainterModel(int width, int height, Pixmap.Format format) {
		pixmap = new Pixmap(width, height, format);
	}

	public PixmapPainterModel(Texture srcTexture) {
		this(srcTexture.getWidth(), srcTexture.getHeight(), srcTexture);
	}

	public PixmapPainterModel(int width, int height, Texture srcTexture) {
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
	public void setPainter(PixmapPainter painter) {
		this.painter = painter;
		painter.pixmap = pixmap;
		texture = new Texture(new PixmapTextureData(painter.pixmap, painter.pixmap.getFormat(), false, false));
	}

	public void output()
	{
		texture.draw(painter.pixmap,0,0);
	}

	@Override
	public int getWidth() {
		return pixmap.getWidth();
	}

	@Override
	public int getHeight() {
		return pixmap.getHeight();
	}

	public void dispose(){
		texture.dispose();
		pixmap.dispose();
	}

}
