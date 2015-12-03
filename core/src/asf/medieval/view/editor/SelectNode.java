package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 12/2/15.
 */
public class SelectNode extends MultiNode implements EditorNode {


	private final InternalChangeListener internalCl = new InternalChangeListener();

	private SelectBox<EditorNode> modeSelectBox;
	protected Cell modeContextCell;


	public SelectNode(String modeName, MedievalWorld world, EditorNode... modes) {
		super(modeName, world, modes);
	}

	@Override
	public void initUi() {
		super.initUi();
		modeSelectBox = new SelectBox<EditorNode>(world.app.skin);
		modeSelectBox.getSelection().setProgrammaticChangeEvents(false);
		modeSelectBox.addListener(internalCl);
		//ModeSelectPane.ModeSelectItem gameSelectItem = new ModeSelectPane.ModeSelectItem("Game", gameEditorMode);
		//ModeSelectPane.ModeSelectItem terrainSelectItem = new ModeSelectPane.ModeSelectItem("Terrain", editorView);
		modeSelectBox.setItems(modes);


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

	public EditorNode getMode() {
		if(!enabled) return null;
		return modeSelectBox.getSelected();
	}

	public void setMode(EditorNode editorNode){
		boolean valid = false;
		for (EditorNode mode : modeSelectBox.getItems()) {
			boolean enabled = editorNode == mode;
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
