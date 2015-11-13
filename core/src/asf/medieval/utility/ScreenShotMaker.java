package asf.medieval.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class ScreenShotMaker {

	public static void takeScreenshot(String caption){

		FileHandle fh = Gdx.files.external("Documents/ASneakyFox/Medieval/Screenshots");
		if(!fh.exists())  throw new IllegalArgumentException("Provided location for screen shots does not exist");
		if(!fh.isDirectory()) throw new IllegalArgumentException("Provided location for screen shots is not a directory");

		int screenWidth, screenHeight;
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		String ssname = String.format("%s %s %sx%s - %s.png",
			SimpleDateFormat.getDateInstance().format(new Date()),
			caption,
			screenWidth,
			screenHeight,
			"%s");


		FileHandle ssfh;
		int counter = 0;
		do{
			counter++;
			ssfh = fh.child(String.format(ssname,counter));
		}while(ssfh.exists());


		Pixmap pixmap = getScreenshot(0, 0,
			screenWidth,
			screenHeight,
			true);
		PixmapIO.writePNG(ssfh, pixmap);
		pixmap.dispose();

		Gdx.app.log("ScreenShotMaker","Took screenshot: "+ssfh.path());

	}

	private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
		final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

		if (yDown) {
			// Flip the pixmap upside down
			ByteBuffer pixels = pixmap.getPixels();
			int numBytes = w * h * 4;
			byte[] lines = new byte[numBytes];
			int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		}

		return pixmap;
	}


	private ScreenShotMaker() {
	}
}
