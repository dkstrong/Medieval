package asf.medieval.painter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/30/15.
 */
public interface PainterDelegate extends Disposable{

	public void setPainter(PixmapPainter painter);

	public HistoryState createHistory();

	public HistoryState createHistory(HistoryState currentState, Array<Point> affectedPixels);

	public void recall(HistoryState historyState);

	public void recall(HistoryState historyState, Array<Point> affectedPixels);

	public int drawPoint(Color brushColor, int x, int y, float opacity);

	public int erasePoint(Color brushColor, int x, int y, float opacity);

	public void output();

	public void output(FileHandle fh);

	public int getWidth();

	public int getHeight();

}
