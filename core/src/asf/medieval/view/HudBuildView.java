package asf.medieval.view;

import asf.medieval.CursorId;
import asf.medieval.model.MilitaryId;
import asf.medieval.model.MilitaryInfo;
import asf.medieval.model.StructureId;
import asf.medieval.model.StructureInfo;
import asf.medieval.model.Token;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/20/15.
 */
public class HudBuildView implements View, InputProcessor {
	private final MedievalWorld world;

	private RootBuildNode currentRootBuildNode;
	private CategoryBuildNode currentCategoryBuildNode;
	private ModelBuildNode currentModelBuildNode;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public HudBuildView(MedievalWorld world) {
		this.world = world;

		ModelBuildNode construction_church = new ModelBuildNode(StructureId.Church);
		CategoryBuildNode construction = new CategoryBuildNode("Construction", construction_church);

		ModelBuildNode church_knight = new ModelBuildNode(MilitaryId.Knight);
		ModelBuildNode church_skeleton = new ModelBuildNode(MilitaryId.Skeleton);
		ModelBuildNode church_jimmy = new ModelBuildNode(MilitaryId.Jimmy);
		CategoryBuildNode church = new CategoryBuildNode(StructureId.Church, church_knight, church_skeleton, church_jimmy);


		currentRootBuildNode = new RootBuildNode(construction, church);


		world.stage.addActor(currentRootBuildNode.container);

		refreshUi();
	}

	public void resize(int width, int height) {
		if (currentRootBuildNode != null) {
			currentRootBuildNode.resize(width, height);
		}

	}

	public void refreshUi() {
		if (currentRootBuildNode != null) {
			currentRootBuildNode.refreshUi();
		}

	}

	private Token getBarracksToBuild(int modelId) {
		return world.scenario.getBarracksToBuild(world.gameClient.user.id, null, modelId);
	}

	@Override
	public void update(float delta) {
		if (currentModelBuildNode != null) {
			world.hudView.hudCommandView.getWorldCoord(Gdx.input.getX(), Gdx.input.getY(), translation);
		}
	}

	@Override
	public void render(float delta) {
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (currentModelBuildNode != null) {
			if (button == Input.Buttons.LEFT) {
				if(currentModelBuildNode.structureId != null) {
					world.hudView.hudCommandView.spawnCommand(currentModelBuildNode.structureId, translation);
				}else {
					world.hudView.hudCommandView.spawnCommand(currentModelBuildNode.militaryId, translation);
				}
				if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					//currentRootBuildNode.buttonGroup.uncheckAll();
					currentCategoryBuildNode.buttonGroup.uncheckAll();
				}
				return true;
			} else if (button == Input.Buttons.RIGHT) {
				currentCategoryBuildNode.buttonGroup.uncheckAll();
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
		switch(keycode)
		{
			case Input.Keys.TAB:
				currentRootBuildNode.buttonGroup.uncheckAll();
				return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private interface BuildNode {

		public void refreshUi();

		public void resize(int graphicsWidth, int graphicsHeight);

		public boolean isVisible();

		public boolean isEnabled();
	}

	private class RootBuildNode implements BuildNode, EventListener {

		public Container<Table> container;
		public Table table;
		public HorizontalGroup hg;
		public Cell cell;
		public ButtonGroup<Button> buttonGroup;
		public CategoryBuildNode[] childNodes;

		public RootBuildNode(CategoryBuildNode... childNodes) {
			this.childNodes = childNodes;
			table = new Table(world.app.skin);
			container = new Container<Table>(table);
			container.setFillParent(true);
			container.align(Align.bottomLeft);
			hg = new HorizontalGroup();

			table.row();
			table.add(hg).fill().align(Align.left);
			table.row();
			cell = table.add().expand().fill().align(Align.topLeft);

			buttonGroup = new ButtonGroup<Button>();
			buttonGroup.setMaxCheckCount(1);
			buttonGroup.setMinCheckCount(0);
			buttonGroup.setUncheckLast(true);
			for (CategoryBuildNode childNode : childNodes) {
				childNode.parent = this;
				buttonGroup.add(childNode.button);
			}
		}

		@Override
		public void refreshUi() {
			Button checkedButton = buttonGroup.getChecked();
			CategoryBuildNode checkedCategory = checkedButton == null ? null : (CategoryBuildNode) checkedButton.getUserObject();
			hg.clearChildren();
			for (CategoryBuildNode childNode : childNodes) {
				childNode.refreshUi();
				if (childNode.isVisible()) {
					hg.addActor(childNode.button);
					if (childNode == checkedCategory && !childNode.isEnabled()) {
						buttonGroup.uncheckAll();
						checkedCategory = null;
					}
				} else if (childNode == checkedCategory) {
					buttonGroup.uncheckAll();
					checkedCategory = null;
				}
			}
		}

		@Override
		public void resize(int graphicsWidth, int graphicsHeight) {

		}

		@Override
		public boolean handle(Event event) {
			return false;
		}

		public void setCell(Actor actor) {

			if (actor == null) {
				cell.setActor(null);
				container.minSize(400, 0);
				//System.out.println("off");
			} else {
				cell.setActor(actor);
				container.minSize(400, 0);
				//System.out.println("on");
			}


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

	private class CategoryBuildNode implements BuildNode, EventListener {
		public StructureId categoryStructureId;
		public TextButton button;
		public Table table;
		public ButtonGroup<Button> buttonGroup;
		public RootBuildNode parent;
		public ModelBuildNode[] childNodes;

		public CategoryBuildNode(String categoryName, ModelBuildNode... childNodes) {
			commonInit(categoryName, childNodes);
		}

		public CategoryBuildNode(StructureId categoryStructureId, ModelBuildNode... childNodes) {
			this.categoryStructureId = categoryStructureId;
			StructureViewInfo mvi = world.structureViewInfo[categoryStructureId.ordinal()];
			commonInit(mvi.name, childNodes);
		}

		private void commonInit(String categoryName, ModelBuildNode[] childNodes) {
			button = new TextButton(categoryName, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);
			this.childNodes = childNodes;

			table = new Table(world.app.skin);
			table.setBackground("default-pane-trans");
			table.defaults().fill().align(Align.topLeft);
			table.align(Align.topLeft).pad(10);

			buttonGroup = new ButtonGroup<Button>();
			buttonGroup.setMaxCheckCount(1);
			buttonGroup.setMinCheckCount(0);
			buttonGroup.setUncheckLast(true);
			for (ModelBuildNode childNode : childNodes) {
				childNode.parent = this;
				buttonGroup.add(childNode.button);
			}
		}

		@Override
		public void refreshUi() {


			Button checkedButton = buttonGroup.getChecked();
			ModelBuildNode checkedModel = checkedButton == null ? null : (ModelBuildNode) checkedButton.getUserObject();
			table.clearChildren();
			final int colsPerRow = 5;
			int count = colsPerRow;
			int countEnabled = 0;
			for (ModelBuildNode childNode : childNodes) {
				childNode.refreshUi();
				if (childNode.isVisible()) {
					if (++count >= colsPerRow) {
						count = 0;
						table.row();
					}
					table.add(childNode.button);
					if (childNode.isEnabled()) {
						countEnabled++;
					} else if (childNode == checkedModel) {
						buttonGroup.uncheckAll();
						checkedModel = null;
					}
				} else if (childNode == checkedModel) {
					buttonGroup.uncheckAll();
					checkedModel = null;
				}
			}

			button.setDisabled(countEnabled == 0);
		}

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeListener.ChangeEvent) {
				if (button.isChecked()) {
					currentCategoryBuildNode = this;
					parent.setCell(table);
				} else {
					if (currentCategoryBuildNode == this) {
						currentCategoryBuildNode = null;
						parent.setCell(null);
					}
					buttonGroup.uncheckAll();
				}
				return true;
			}
			return false;
		}

		@Override
		public void resize(int graphicsWidth, int graphicsHeight) {

		}

		@Override
		public boolean isVisible() {
			return !button.isDisabled();
		}

		@Override
		public boolean isEnabled() {
			return !button.isDisabled();
		}


	}


	private class ModelBuildNode implements BuildNode, EventListener {
		public TextButton button;
		public CategoryBuildNode parent;

		public final StructureId structureId;
		public final MilitaryId militaryId;


		public ModelInstance modelInstance;

		public int cursor;

		public ModelBuildNode(StructureId structureId) {
			this.structureId = structureId;
			this.militaryId = null;
			StructureViewInfo svi = world.structureViewInfo[structureId.ordinal()];
			StructureInfo si = world.scenario.structureInfo[structureId.ordinal()];

			button = new TextButton(svi.name, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);

			Model model = world.assetManager.get(svi.assetLocation[0]);
			modelInstance = new ModelInstance(model);
			cursor = -1;


		}

		public ModelBuildNode(MilitaryId militaryId) {
			this.structureId = null;
			this.militaryId = militaryId;

			ModelViewInfo mvi = world.modelViewInfo[militaryId.ordinal()];
			MilitaryInfo mi = world.scenario.militaryInfo[militaryId.ordinal()];

			button = new TextButton(mvi.name, world.app.skin, "toggle");
			button.setUserObject(this);
			button.addListener(this);


			cursor = CursorId.RECRUIT_SOLDIER;

		}

		@Override
		public void refreshUi() {
			button.setDisabled(militaryId!=null && getBarracksToBuild(militaryId.ordinal())==null  );
		}

		@Override
		public boolean handle(Event event) {
			if (event instanceof ChangeListener.ChangeEvent) {
				if (button.isChecked()) {
					currentModelBuildNode = this;
					world.app.setCusor(cursor);
				} else {
					if (currentModelBuildNode == this) {
						currentModelBuildNode = null;
						world.app.setCusor(CursorId.DEFAULT);
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public void resize(int graphicsWidth, int graphicsHeight) {

		}

		@Override
		public boolean isVisible() {
			return !button.isDisabled();
		}

		@Override
		public boolean isEnabled() {
			return !button.isDisabled();
		}
	}


}
