package asf.medieval.view;

import asf.medieval.model.ModelId;
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

/**
 * Created by daniel on 11/20/15.
 */
public class HudBuildView implements View, InputProcessor {
	private final MedievalWorld world;

	private Container<Table> baseTableContainer;
	private Table baseTable;
	private ButtonGroup baseButtonGroup;


	private TableButtonMapping currentTableButtonMapping;
	private ModelButtonMapping currentModelButtonMapping;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public HudBuildView(MedievalWorld world) {
		this.world = world;

		TableButtonMapping construction = new TableButtonMapping("Construction");
		ModelButtonMapping construction_church = new ModelButtonMapping(ModelId.Church,"Models/Church/Church.g3db");
		makeTable(construction, 4, construction_church);

		TableButtonMapping barracks = new TableButtonMapping("Barracks");
		ModelButtonMapping barracks_knight = new ModelButtonMapping(ModelId.Knight,"Models/Characters/knight_01.g3db");
		ModelButtonMapping barracks_skeleton = new ModelButtonMapping(ModelId.Skeleton,"Models/Characters/Skeleton.g3db");
		ModelButtonMapping barracks_jimmy = new ModelButtonMapping(ModelId.Jimmy,"Models/Jimmy/Jimmy_r1.g3db");
		makeTable(barracks, 1, barracks_knight, barracks_skeleton, barracks_jimmy);



		makeTable(null, 1, construction, barracks);
		world.stage.addActor(baseTableContainer);


	}

	private Table makeTable(TableButtonMapping parentMapping, int colsPerRow, ButtonMapping... buttonTitles) {
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(0);
		buttonGroup.setUncheckLast(true);

		Table table = new Table(world.app.skin);
		table.setBackground("default-pane-trans");
		table.defaults().fill().align(Align.topRight);

		int rowCount=0;
		int colCount=0;
		int count = colsPerRow;
		int currentColCount=0;
		for (ButtonMapping buttonMapping : buttonTitles) {
			if (++count >= colsPerRow) {
				count = 0;
				table.row();
				rowCount++;
				currentColCount=0;
			}

			table.add(buttonMapping.button);
			buttonGroup.add(buttonMapping.button);
			if(++currentColCount>colCount){
				colCount = currentColCount;
			}
		}

		//table.add(new Label("", world.app.skin));

		if(parentMapping == null){
			baseTable = table;
			baseTableContainer = new Container<Table>(baseTable);
			baseTableContainer.setFillParent(true);
			baseTableContainer.align(Align.right);
			baseButtonGroup = buttonGroup;
		} else {
			parentMapping.subTable = table;
			parentMapping.subCols = colCount;
			parentMapping.subRows = rowCount;
			parentMapping.subButtonGroup = buttonGroup;
		}
		return table;
	}

	public void resize(int width, int height) {

		if(currentTableButtonMapping !=null){
			currentTableButtonMapping.resize();

		}

	}

	@Override
	public void update(float delta) {
		if(currentModelButtonMapping!=null){
			world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(),translation);
		}
	}

	@Override
	public void render(float delta) {
		if(currentModelButtonMapping!=null){

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
		if(currentModelButtonMapping!=null){
			if(button == Input.Buttons.LEFT){
				world.hudView.hudCommandView.spawnCommand(currentModelButtonMapping.modelId,translation);
				if(!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					baseButtonGroup.uncheckAll();
				}

			}else if(button == Input.Buttons.RIGHT){
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

	private abstract class ButtonMapping implements EventListener {
		String buttonText;
		TextButton button;

		public ButtonMapping(String buttonText) {
			this.buttonText = buttonText;
			button = new TextButton(buttonText, world.app.skin,"toggle");
			button.setUserObject(this);
			button.addListener(this);
		}


	}

	private class TableButtonMapping extends ButtonMapping{
		Table subTable;
		int subRows, subCols;
		ButtonGroup<Button> subButtonGroup;

		public TableButtonMapping(String buttonText) {
			super(buttonText);
		}


		@Override
		public boolean handle(Event event) {
			if(event instanceof ChangeListener.ChangeEvent){
				if (subTable != null) {
					if(button.isChecked()){
						currentTableButtonMapping = this;
						resize();
						world.stage.addActor(subTable);
					}else{
						subButtonGroup.uncheckAll();
						subTable.remove();
					}
					return true;
				}
			}
			return false;
		}

		public void resize(){
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
	}

	private class ModelButtonMapping extends ButtonMapping{
		public final ModelId modelId;
		public final ModelInstance modelInstance;


		public ModelButtonMapping(ModelId modelId, String assetLocation) {
			super(modelId.name());
			this.modelId = modelId;
			Model model = world.assetManager.get(assetLocation);
			modelInstance = new ModelInstance(model);

		}

		@Override
		public boolean handle(Event event) {
			if(event instanceof ChangeListener.ChangeEvent){
				if (modelInstance != null) {
					if(button.isChecked()){
						currentModelButtonMapping = this;
					}else{
						if(currentModelButtonMapping == this)
							currentModelButtonMapping = null;
					}
					return true;
				}
			}
			return false;
		}
	}


}
