package asf.medieval.painter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/28/15.
 */
public class HistoryState implements Disposable {
	public int[][] pixelData;

	public HistoryState(Pixmap pixmap) {
		pixelData = new int[pixmap.getWidth()][pixmap.getHeight()];
		for(int x=0; x<pixelData.length; x++){
			for(int y=0; y<pixelData[0].length; y++){
				pixelData[x][y] = pixmap.getPixel(x,y);
			}
		}
	}

	public HistoryState(float[] fieldData, int fieldWidth, int fieldHeight) {
		pixelData = new int[fieldWidth][fieldHeight];
		for(int x=0; x<pixelData.length; x++){
			for(int y=0; y<pixelData[0].length; y++){
				pixelData[x][y] =  Color.alpha(fieldData[y*fieldWidth+x]);
			}
		}
	}

	public HistoryState(HistoryState copy, Array<Point> affectedPoints) {
		int width = copy.pixelData.length;
		int height = copy.pixelData[0].length;
		pixelData = new int[width][];
		for(int i = 0; i < width; i++)
		{
			pixelData[i] = new int[height];
			System.arraycopy(copy.pixelData[i], 0, pixelData[i], 0, height);
		}

		for (Point affectedPoint : affectedPoints) {
			pixelData[affectedPoint.x][affectedPoint.y] = affectedPoint.color;
		}

	}

	@Override
	public void dispose() {

	}
}
