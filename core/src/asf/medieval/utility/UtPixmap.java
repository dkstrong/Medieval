package asf.medieval.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;

/**
 * Created by daniel on 11/21/15.
 */
public class UtPixmap {

	private static final Color c00 = new Color();
	private static final Color c10 = new Color();
	private static final Color c01 = new Color();
	private static final Color c11 = new Color();
	private static final Color cStore = new Color();

	public static int getColorStretchNearest(Pixmap srcPix, Pixmap dstPix, int dstX, int dstY)
	{

		final int dstWidth = dstPix.getWidth();
		final int dstHeight = dstPix.getHeight();
		final float dstXFloat = dstX / (float) dstWidth;
		final float dstYFloat = dstY / (float) dstHeight;
		final float srcXFloat = srcPix.getWidth() * dstXFloat;
		final float srcYFloat = srcPix.getHeight() * dstYFloat;
		int srcX = (int) srcXFloat;
		int srcY = (int)srcYFloat;

		return srcPix.getPixel(srcX,srcY);
	}

	public static int getColorStretchNearest(Pixmap srcPix,  final float srcXScale, final float srcYScale, Pixmap dstPix, int dstX, int dstY)
	{

		final int srcWidth = srcPix.getWidth();
		final int srcHeight = srcPix.getHeight();
		final int dstWidth = dstPix.getWidth();
		final int dstHeight = dstPix.getHeight();
		final float dstXFloat = dstX / (float) dstWidth;
		final float dstYFloat = dstY / (float) dstHeight;
		float srcXFloat = srcWidth * dstXFloat * srcXScale;
		float srcYFloat = srcHeight * dstYFloat * srcYScale;

		int srcX = (int) srcXFloat;
		int srcY = (int)srcYFloat;

		return srcPix.getPixel(srcX,srcY);
	}


	public static int getColorStretchLinear(Pixmap srcPix, Pixmap dstPix, int dstX, int dstY)
	{

		final int dWidth = dstPix.getWidth();
		final int dHeight = dstPix.getHeight();
		final int sWidth = srcPix.getWidth();
		final int sHeight = srcPix.getHeight();
		final float dXFloat = dstX / (float) dWidth;
		final float dyFloat = dstY / (float) dHeight;
		final float sxFloat = srcPix.getWidth() * dXFloat;
		final float syFloat = srcPix.getHeight() * dyFloat;
		final int sX0 = (int)sxFloat;
		final int sY0 = (int)syFloat;
		int sX1 = sX0 + 1;
		int sY1 = sY0 + 1;
		if (sX1 >= sWidth) sX1 = sWidth - 1;
		if (sY1 >= sHeight) sY1 = sHeight - 1;

		Color.rgba8888ToColor(c00,srcPix.getPixel(sX0,sY0));
		Color.rgba8888ToColor(c10,srcPix.getPixel(sX1,sY0));
		Color.rgba8888ToColor(c01,srcPix.getPixel(sX0,sY1));
		Color.rgba8888ToColor(c11,srcPix.getPixel(sX1,sY1));
		UtMath.interpolateBilinear(sxFloat - sX0, syFloat - sY0 , c00, c10, c01, c11,cStore);

		return Color.rgba8888(cStore);

	}

	public static int getColorTile(Pixmap srcPix, Pixmap dstPix, int dstX, int dstY)
	{
		final int srcWidth = srcPix.getWidth();
		final int srcHeight = srcPix.getHeight();
		int srcX = dstX;
		int srcY = dstY;
		while(srcX >= srcWidth){
			srcX -= srcWidth;
		}

		while(srcY >= srcHeight){
			srcY -= srcHeight;
		}

		return srcPix.getPixel(srcX,srcY);
	}

	public static int getColorStretchNearestTile(Pixmap srcPix, final float srcXScale, final float srcYScale, Pixmap dstPix, int dstX, int dstY)
	{
		final int srcWidth = srcPix.getWidth();
		final int srcHeight = srcPix.getHeight();
		final int dstWidth = dstPix.getWidth();
		final int dstHeight = dstPix.getHeight();
		final float dstXFloat = dstX / (float) dstWidth;
		final float dstYFloat = dstY / (float) dstHeight;
		float srcXFloat = srcWidth * dstXFloat * srcXScale;
		float srcYFloat = srcHeight * dstYFloat * srcYScale;

		int srcX = (int) srcXFloat;
		int srcY = (int)srcYFloat;

		while(srcX >= srcPix.getWidth()){
			srcX -= srcPix.getWidth();
		}

		while(srcY >= srcPix.getHeight()){
			srcY -= srcPix.getHeight();
		}

		return srcPix.getPixel(srcX,srcY);
	}

	public static int getColorStretchLinearTile(Pixmap srcPix, final float srcXScale, final float srcYScale, Pixmap dstPix, int dstX, int dstY)
	{
		final int srcWidth = srcPix.getWidth();
		final int srcHeight = srcPix.getHeight();
		final int dstWidth = dstPix.getWidth();
		final int dstHeight = dstPix.getHeight();
		final float dstXFloat = dstX / (float) dstWidth;
		final float dstYFloat = dstY / (float) dstHeight;
		float srcXFloat = srcWidth * dstXFloat * srcXScale;
		float srcYFloat = srcHeight * dstYFloat * srcYScale;

		int srcX = (int) srcXFloat;
		int srcY = (int)srcYFloat;


		int sX0 = srcX;
		int sY0 = srcY;
		while(sX0 >= srcWidth) sX0 -= srcWidth;
		while(sY0 >= srcHeight) sY0 -= srcHeight;
		int sX1 = sX0 + 1;
		int sY1 = sY0 + 1;
		if (sX1 >= srcWidth) sX1 -= srcWidth;
		if (sY1 >= srcHeight) sY1 -= srcHeight;




		Color.rgba8888ToColor(c00,srcPix.getPixel(sX0,sY0));
		Color.rgba8888ToColor(c10,srcPix.getPixel(sX1,sY0));
		Color.rgba8888ToColor(c01,srcPix.getPixel(sX0,sY1));
		Color.rgba8888ToColor(c11,srcPix.getPixel(sX1,sY1));
		UtMath.interpolateBilinear(srcXFloat - srcX, srcYFloat - srcY , c00, c10, c01, c11,cStore);

		return Color.rgba8888(cStore);
	}

	public static Pixmap copyPixmap(Pixmap src){
		Pixmap dst = new Pixmap(src.getWidth(), src.getHeight(), src.getFormat());

		for(int x=0; x<src.getWidth(); x++){
			for(int y=0; y<src.getHeight(); y++){
				dst.drawPixel(x,y,src.getPixel(x,y));
			}
		}

		return dst;
	}
}
