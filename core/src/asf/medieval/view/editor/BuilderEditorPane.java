package asf.medieval.view.editor;

import asf.medieval.model.MilitaryId;
import asf.medieval.model.MilitaryInfo;
import asf.medieval.model.StructureId;
import asf.medieval.model.StructureInfo;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.strictmath.VecHelper;
import asf.medieval.view.MedievalWorld;
import asf.medieval.view.ModelViewInfo;
import asf.medieval.view.StructureViewInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/26/15.
 */
public class BuilderEditorPane implements EditorNode {
	public final MedievalWorld world;
	public final EditorView editorView;
	private boolean enabled;

	private Table toolTable;
	private ButtonGroup<Button> buttonGroup;

	private ModelBuildMapping currentModelBuildNode;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();



	public BuilderEditorPane(EditorView editorView) {
		this.editorView = editorView;
		this.world = editorView.world;

	}

	//////////////////////////////////////////////////////
	/// Begin methods that set up and refresh the ui
	//////////////////////////////////////////////////////
	@Override
	public void initUi() {



		toolTable = new Table(world.app.skin);
		toolTable.align(Align.left);

		toolTable.row();
		toolTable.add(new Label("Resources", world.app.skin));

		ModelBuildMapping tree = new ModelBuildMapping(StructureId.Tree);


		buttonGroup = new ButtonGroup<Button>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(0);
		buttonGroup.setUncheckLast(true);
		buttonGroup.add(tree.button);

		for (Button button : buttonGroup.getButtons()) {
			toolTable.add(button);
		}



	}


	@Override
	public Actor getToolbarActor() {
		return toolTable;
	}


	@Override
	public void refreshUi() {
		if(!enabled){
			buttonGroup.uncheckAll();
		}

	}

	@Override
	public void update(float delta) {
		if (!enabled) return;

		if (currentModelBuildNode != null) {
			world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(), translation);
		}
	}

	@Override
	public void render(float delta) {
		if (!enabled) return;
		if (currentModelBuildNode != null) {

			if (currentModelBuildNode.modelInstance != null) {
				currentModelBuildNode.modelInstance.transform.set(
					translation.x, translation.y, translation.z,
					rotation.x, rotation.y, rotation.z, rotation.w,
					1, 1, 1
				);
				world.shadowBatch.render(currentModelBuildNode.modelInstance);
				world.modelBatch.render(currentModelBuildNode.modelInstance, world.environment);
			}

		}
	}

	@Override
	public void dispose() {
		setEnabled(false);
	}
	////////////////////////////////////////
	/// Begin methods that mutate the UI (ie button actions)
	////////////////////////////////////////

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled && !this.enabled) {
			this.enabled = true;
		} else if (!enabled && this.enabled) {
			this.enabled = false;
		}
	}

	////////////////////////////////////////////////
	/// Begin methods that listen for user input
	////////////////////////////////////////////////

	@Override
	public boolean keyDown(int keycode) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		if (currentModelBuildNode != null) {
			if (button == Input.Buttons.LEFT) {
				StrictVec2 loc = VecHelper.toVec2(translation, new StrictVec2());
				world.scenario.newMineable(loc, currentModelBuildNode.structureId.ordinal());
				if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					buttonGroup.uncheckAll();
				}
				return true;
			} else if (button == Input.Buttons.RIGHT) {
				buttonGroup.uncheckAll();
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled) return false;
		return false;
	}

	@Override
	public String toString(){
		return "Game";
	}


	private class ModelBuildMapping implements EventListener {
		public TextButton button;

		public StructureId structureId;
		public MilitaryId militaryId;
		public ModelInstance modelInstance;

		public ModelBuildMapping(StructureId structureId) {
			this.structureId = structureId;
			StructureViewInfo svi = world.structureViewInfo[structureId.ordinal()];
			StructureInfo si = world.scenario.structureInfo[structureId.ordinal()];
			button = new TextButton(svi.name, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);


			Model model = world.assetManager.get(svi.assetLocation[0]);
			modelInstance = new ModelInstance(model);


		}

		public ModelBuildMapping(MilitaryId militaryId) {
			this.militaryId = militaryId;
			ModelViewInfo mvi = world.modelViewInfo[militaryId.ordinal()];
			MilitaryInfo mi = world.scenario.militaryInfo[militaryId.ordinal()];
			button = new TextButton(mvi.name, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);

			Model model = world.assetManager.get(mvi.assetLocation[0]);
			modelInstance = new ModelInstance(model);


		}

		public void refreshUi() {

		}

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeListener.ChangeEvent) {
				if (button.isChecked()) {
					currentModelBuildNode = this;

				} else {
					if (currentModelBuildNode == this) {
						currentModelBuildNode = null;
						translation.set(Float.NaN,Float.NaN,Float.NaN);
					}
				}
				return true;
			}
			return false;
		}

	}

}
