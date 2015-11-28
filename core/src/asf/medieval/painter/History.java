package asf.medieval.painter;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/27/15.
 */
public class History implements Disposable{

	public int maxHistory = 64;

	public int currentHistory = 0;
	public final Array<HistoryState> history = new Array<HistoryState>(true, maxHistory, HistoryState.class);

	@Override
	public void dispose() {
		while (history.size > 0) {
			history.removeIndex(0).dispose();
		}
	}

	public void resetHistory(Pixmap pixmap)
	{
		while (history.size > 0) {
			history.removeIndex(0).dispose();
		}

		history.add(new HistoryState(pixmap));

		currentHistory=history.size - 1;

	}

	public void addHistory(Pixmap pixmap) {

		// if the insertion point is in the past
		// then we delete history that happens in the future
		while(currentHistory < history.size -1){
			HistoryState lostHistoryState = history.removeIndex(currentHistory+1);
			lostHistoryState.dispose();
		}

		// add new state to the history stack
		history.add(new HistoryState(pixmap));

		// delete history too old to store to save memory
		if (history.size > maxHistory) {
			HistoryState lostHistoryState = history.removeIndex(0);
			lostHistoryState.dispose();
		}

		// we are now viewing the latest point in history
		currentHistory=history.size - 1;

	}

	public void addHistory(Array<Point> affectedPixels) {

		// if the insertion point is in the past
		// then we delete history that happens in the future
		while(currentHistory < history.size -1){
			HistoryState lostHistoryState = history.removeIndex(currentHistory+1);
			lostHistoryState.dispose();
		}

		// add new state to the history stack
		history.add(new HistoryState(history.get(currentHistory), affectedPixels));

		// delete history too old to store to save memory
		if (history.size > maxHistory) {
			HistoryState lostHistoryState = history.removeIndex(0);
			lostHistoryState.dispose();
		}

		// we are now viewing the latest point in history
		currentHistory=history.size - 1;

	}

	public void recallHistory(int index, PixmapPainter store)
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
		HistoryState h = history.get(currentHistory);

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for(int x=0; x<h.pixelData.length; x++){
			for(int y=0; y<h.pixelData[0].length; y++){
				store.pixmap.drawPixel(x, y, h.pixelData[x][y]);
			}
		}

		store.texture.draw(store.pixmap, 0, 0);
	}

	public void undoPreview(Array<Point> affectedPixels, PixmapPainter store)
	{
		HistoryState h = history.get(currentHistory);

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (Point affectedPixel : affectedPixels) {
			store.pixmap.drawPixel(affectedPixel.x, affectedPixel.y, h.pixelData[affectedPixel.x][affectedPixel.y]);
		}

		store.texture.draw(store.pixmap, 0, 0);

	}
}
