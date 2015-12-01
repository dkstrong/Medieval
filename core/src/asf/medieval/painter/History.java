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

	public void resetHistory(PainterDelegate painterDelegate)
	{
		while (history.size > 0) {
			history.removeIndex(0).dispose();
		}

		history.add(painterDelegate.createHistory());

		currentHistory=history.size - 1;

	}

	public void addHistory(PainterDelegate painterDelegate) {

		// if the insertion point is in the past
		// then we delete history that happens in the future
		while(currentHistory < history.size -1){
			HistoryState lostHistoryState = history.removeIndex(currentHistory+1);
			lostHistoryState.dispose();
		}

		// add new state to the history stack
		history.add(painterDelegate.createHistory());

		// delete history too old to store to save memory
		if (history.size > maxHistory) {
			HistoryState lostHistoryState = history.removeIndex(0);
			lostHistoryState.dispose();
		}

		// we are now viewing the latest point in history
		currentHistory=history.size - 1;

	}

	public void addHistory(PainterDelegate painterDelegate, Array<Point> affectedPixels) {

		// if the insertion point is in the past
		// then we delete history that happens in the future
		while(currentHistory < history.size -1){
			HistoryState lostHistoryState = history.removeIndex(currentHistory+1);
			lostHistoryState.dispose();
		}

		// add new state to the history stack
		history.add(painterDelegate.createHistory(history.get(currentHistory), affectedPixels));

		// delete history too old to store to save memory
		if (history.size > maxHistory) {
			HistoryState lostHistoryState = history.removeIndex(0);
			lostHistoryState.dispose();
		}

		// we are now viewing the latest point in history
		currentHistory=history.size - 1;

	}

	public void recallHistory(PainterDelegate painterDelegate, int index)
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

		painterDelegate.recall(h);
		painterDelegate.output();
	}

	public void undoPreview(PainterDelegate painterDelegate, Array<Point> affectedPixels)
	{
		if(affectedPixels.size <=0)  // Nothing has changed, nothing to undo
			return;

		HistoryState h = history.get(currentHistory);

		painterDelegate.recall(h,affectedPixels);
		painterDelegate.output();

	}
}
