package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 12/2/15.
 */
public class ModeSelectPane extends MultiModePane implements EditorMode{


	private final InternalChangeListener internalCl = new InternalChangeListener();

	private SelectBox<EditorMode> modeSelectBox;
	protected Cell modeContextCell;


	public ModeSelectPane(String modeName, MedievalWorld world, EditorMode... modes) {
		super(modeName, world, modes);
	}

	@Override
	public void initUi() {
		modeSelectBox = new SelectBox<EditorMode>(world.app.skin);
		modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
		modeSelectBox.addListener(internalCl);
		//ModeSelectPane.ModeSelectItem gameSelectItem = new ModeSelectPane.ModeSelectItem("Game", gameEditorMode);
		//ModeSelectPane.ModeSelectItem terrainSelectItem = new ModeSelectPane.ModeSelectItem("Terrain", terrainEditorMode);
		for (EditorMode mode : modes) {
			mode.initUi();
		}
		modeSelectBox.setItems(modes);


		toolTable = new Table(world.app.skin);
		if(this instanceof View) // TODO: need proper way to determine if this is the root table...
			toolTable.setBackground("default-pane-trans"); // base editor, set up the background
		toolTable.align(Align.left);
		toolTable.row();//.padBottom(Value.percentWidth(0.05f));
		toolTable.add(modeSelectBox);
		modeContextCell = toolTable.add(new Label("no mode", world.app.skin)).fill().expand().align(Align.topLeft);
	}

	@Override
	public void refreshUi() {
		// do not need to call super
		setMode(getMode()); // this calls refreshUI() on all child nodes
	}

	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}

	public EditorMode getMode() {
		if(!enabled) return null;
		return modeSelectBox.getSelected();
	}

	public void setMode(EditorMode editorMode){
		boolean valid = false;
		for (EditorMode mode : modeSelectBox.getItems()) {
			boolean enabled = editorMode == mode;
			mode.setEnabled(enabled);
			mode.refreshUi();
			if(enabled){
				valid = true;
				modeSelectBox.setSelected(mode);
				modeContextCell.setActor(mode.getToolbarActor());
			}

		}

		if (!valid)
			modeContextCell.setActor(null);
	}

	private class InternalChangeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			Actor actor = event.getListenerActor();

			if(actor == modeSelectBox){
				if(event instanceof ChangeListener.ChangeEvent){
					setMode(modeSelectBox.getSelected());
				}
			}


			return false;
		}
	}

}
