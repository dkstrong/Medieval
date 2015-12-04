package asf.medieval.view;

import asf.medieval.model.BarracksController;
import asf.medieval.model.ModelId;
import asf.medieval.model.Player;
import asf.medieval.model.Token;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/20/15.
 */
public class HudBuildView implements View, InputProcessor {
	private final MedievalWorld world;

	private RootButtonMapping currentRootButtonMapping;
	private TableButtonMapping currentTableButtonMapping;
	private ModelButtonMapping currentModelButtonMapping;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public HudBuildView(MedievalWorld world) {
		this.world = world;


		ModelButtonMapping construction_church = new ModelButtonMapping(ModelId.Church.ordinal());
		TableButtonMapping construction = new TableButtonMapping("Construction", construction_church);


		ModelButtonMapping barracks_knight = new ModelButtonMapping(ModelId.Knight.ordinal());
		ModelButtonMapping barracks_skeleton = new ModelButtonMapping(ModelId.Skeleton.ordinal());
		ModelButtonMapping barracks_jimmy = new ModelButtonMapping(ModelId.Jimmy.ordinal());
		TableButtonMapping barracks = new TableButtonMapping("Barracks", barracks_knight, barracks_skeleton, barracks_jimmy);



		currentRootButtonMapping = new RootButtonMapping(construction, barracks);


		world.stage.addActor(currentRootButtonMapping.baseTableContainer);


	}

	private Table makeTable(ButtonMapping parentMapping, int colsPerRow, AbstractButtonMapping... buttonTitles) {
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(0);
		buttonGroup.setUncheckLast(true);

		Table table = new Table(world.app.skin);
		table.setBackground("default-pane-trans");
		table.defaults().fill().align(Align.topRight);

		int rowCount = 0;
		int colCount = 0;
		int count = colsPerRow;
		int currentColCount = 0;
		for (AbstractButtonMapping buttonMapping : buttonTitles) {
			buttonGroup.add(buttonMapping.button);
			if (buttonMapping.isVisible()) {
				if (++count >= colsPerRow) {
					count = 0;
					table.row();
					rowCount++;
					currentColCount = 0;
				}

				table.add(buttonMapping.button);

				if (++currentColCount > colCount) {
					colCount = currentColCount;
				}
			}
		}

		//table.add(new Label("", world.app.skin));

		if (parentMapping instanceof  RootButtonMapping) {
			RootButtonMapping rbm = (RootButtonMapping) parentMapping;
			rbm.baseTable = table;
			rbm.baseTableContainer = new Container<Table>(rbm.baseTable);
			rbm.baseTableContainer.setFillParent(true);
			rbm.baseTableContainer.align(Align.right);
			rbm.subCols = colCount;
			rbm.subRows = rowCount;
			rbm.maxColsPerRow = colsPerRow;
			rbm.baseButtonGroup = buttonGroup;
		} else if(parentMapping instanceof TableButtonMapping){
			TableButtonMapping tbm = (TableButtonMapping) parentMapping;
			tbm.subTable = table;
			tbm.subCols = colCount;
			tbm.subRows = colCount;
			tbm.maxColsPerRow = colsPerRow;
			tbm.subButtonGroup = buttonGroup;
		}
		return table;
	}

	private void refreshTable(TableButtonMapping parentMapping) {
		Table table = parentMapping.subTable;
		ButtonGroup<Button> buttonGroup = parentMapping.subButtonGroup;
		Button checkedButton = buttonGroup.getChecked();
		ButtonMapping checkedButtonMapping = checkedButton == null ? null : (ButtonMapping) checkedButton.getUserObject();
		final int colsPerRow = parentMapping.maxColsPerRow;
		int rowCount = 0;
		int colCount = 0;
		int count = colsPerRow;
		int currentColCount = 0;
		table.clearChildren();;
		for (AbstractButtonMapping buttonMapping : parentMapping.subButtonMappings) {
			if (buttonMapping.isVisible()) {
				if (++count >= colsPerRow) {
					count = 0;
					table.row();
					rowCount++;
					currentColCount = 0;
				}

				table.add(buttonMapping.button);
				if (!buttonMapping.isEnabled()) {
					if (checkedButtonMapping == buttonMapping) {
						buttonGroup.clear();
						checkedButtonMapping = null;
					}

				}

				if (++currentColCount > colCount) {
					colCount = currentColCount;
				}
			}
		}

		parentMapping.subCols = colCount;
		parentMapping.subRows = rowCount;

	}

	public void resize(int width, int height) {

		if (currentTableButtonMapping != null) {
			currentTableButtonMapping.resize();

		}

	}

	private Array<BarracksController> barracks = new Array<BarracksController>(false, 8, BarracksController.class);

	public void refreshUi() {
		final Player localPlayer = world.gameClient.player;
		barracks.clear();
		for (Token token : world.scenario.tokens) {
			if (token.owner.id == localPlayer.id) {
				if (token.barracks != null) {
					barracks.add(token.barracks);
				}
			}
		}


		for (TableButtonMapping baseButtonMapping : currentRootButtonMapping.baseButtonMappings) {

			refreshTable(baseButtonMapping);
		}


	}

	private BarracksController getBarracksToBuild(int modelId) {
		for (BarracksController barrack : barracks) {
			if (barrack.canBuild(modelId))
				return barrack;
		}
		return null;
	}

	@Override
	public void update(float delta) {
		if (currentModelButtonMapping != null) {
			world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(), translation);
		}
	}

	@Override
	public void render(float delta) {
		if (currentModelButtonMapping != null) {

			currentModelButtonMapping.modelInstance.transform.set(
				translation.x, translation.y, translation.z,
				rotation.x, rotation.y, rotation.z, rotation.w,
				1, 1, 1
			);
			world.shadowBatch.render(currentModelButtonMapping.modelInstance);
			world.modelBatch.render(currentModelButtonMapping.modelInstance, world.environment);
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (currentModelButtonMapping != null) {
			if (button == Input.Buttons.LEFT) {
				world.hudView.hudCommandView.spawnCommand(currentModelButtonMapping.modelId, translation);
				if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					currentRootButtonMapping.baseButtonGroup.uncheckAll();
				}

			} else if (button == Input.Buttons.RIGHT) {
				currentTableButtonMapping.subButtonGroup.uncheckAll();
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private interface ButtonMapping{
		public boolean isVisible();

		public boolean isEnabled();
	}

	private class RootButtonMapping implements ButtonMapping {

		private Container<Table> baseTableContainer;
		private Table baseTable;
		int subRows, subCols, maxColsPerRow;
		private ButtonGroup baseButtonGroup;
		private TableButtonMapping[] baseButtonMappings;

		public RootButtonMapping(TableButtonMapping... baseButtonMappings) {
			this.baseButtonMappings = baseButtonMappings;

			makeTable(this, 1, baseButtonMappings);

		}

		@Override
		public boolean isVisible() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}

	private abstract class AbstractButtonMapping implements ButtonMapping, EventListener {
		String buttonText;
		TextButton button;

		protected void init(String buttonText) {
			this.buttonText = buttonText;
			button = new TextButton(buttonText, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);
		}

		public abstract boolean isVisible();

		public abstract boolean isEnabled();

	}




	private class TableButtonMapping extends AbstractButtonMapping {
		Table subTable;
		int subRows, subCols, maxColsPerRow;
		ButtonGroup<Button> subButtonGroup;
		AbstractButtonMapping[] subButtonMappings;

		public TableButtonMapping(String buttonText, AbstractButtonMapping... subButtonMappings) {
			this.subButtonMappings = subButtonMappings;
			init(buttonText);
			makeTable(this, 1, subButtonMappings);
		}


		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeListener.ChangeEvent) {
				if (subTable != null) {
					if (button.isChecked()) {
						currentTableButtonMapping = this;
						resize();
						world.stage.addActor(subTable);
					} else {
						subButtonGroup.uncheckAll();
						subTable.remove();
					}
					return true;
				}
			}
			return false;
		}

		public void resize() {
			for (Cell cell : subTable.getCells()) {
				cell.minWidth(button.getWidth());
			}
			float width = button.getWidth() * subCols;
			float height = button.getHeight() * subRows;
			Vector2 stageCoords = button.localToAscendantCoordinates(null, new Vector2());
			float heightOffset = button.getHeight() * (subRows - 1);
			subTable.setBounds(
				stageCoords.x - width,
				stageCoords.y - heightOffset,
				width,
				height);
		}

		@Override
		public boolean isVisible() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}

	private class ModelButtonMapping extends AbstractButtonMapping {
		public final int modelId;
		public final ModelInstance modelInstance;


		public ModelButtonMapping(int modelId) {
			ModelViewInfo mvi = world.models.get(modelId);
			init(mvi.name);

			this.modelId = modelId;
			Model model = world.assetManager.get(mvi.assetLocation[0]);
			modelInstance = new ModelInstance(model);

		}

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeListener.ChangeEvent) {
				if (modelInstance != null) {
					if (button.isChecked()) {
						currentModelButtonMapping = this;
					} else {
						if (currentModelButtonMapping == this)
							currentModelButtonMapping = null;
					}
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isVisible() {
			return modelId == ModelId.Church.ordinal() || getBarracksToBuild(modelId) != null;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}


}
