package asf.medieval.view;

import asf.medieval.model.ScenarioFactory;
import asf.medieval.utility.StretchableImage;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class HudGameObject implements GameObject,InputProcessor {

	private MedievalWorld world;

	private Container labelContainer;
	private Label label;

	private Container bottomLabelContainer;
	private Label bottomLabel;

	private StretchableImage selectionBox;
	private final Decal moveCommandDecal = new Decal();

	private Array<SoldierGameObject> selectedSoldiers = new Array<SoldierGameObject>(true, 16, SoldierGameObject.class);

	public HudGameObject(MedievalWorld world) {
		this.world = world;
		label = new Label("Hello, there!", world.app.skin);
		label.setAlignment(Align.topRight, Align.topRight);
		labelContainer = new Container<Label>(label);
		labelContainer.align(Align.topRight).pad(10);
		world.stage.addActor(labelContainer);


		bottomLabel = new Label("Hello, there!", world.app.skin);
		bottomLabel.setAlignment(Align.bottomLeft, Align.bottomLeft);
		bottomLabelContainer = new Container<Label>(bottomLabel);
		bottomLabelContainer.align(Align.bottomLeft).pad(10);
		world.stage.addActor(bottomLabelContainer);

		selectionBox = new StretchableImage(world.pack.findRegion("Interface/Hud/selection-box"));


		moveCommandDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		moveCommandDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		moveCommandDecal.setDimensions(2, 2);
		moveCommandDecal.setColor(1, 1, 1, 1);
		moveCommandDecal.rotateX(-90);




		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {


		labelContainer.setBounds(0, 0, width, height);
		bottomLabelContainer.setBounds(0, 0, width, height);

	}

	@Override
	public void update(float delta)
	{
		if(mouseLeftDrag)
		{
			if(selectionBox.getParent() == null)
				world.stage.addActor(selectionBox);

			final float x = mouseLeftDragStartCoords.x;
			final float y = Gdx.graphics.getHeight() - mouseLeftDragStartCoords.y;
			final float width = mouseLeftDragEndCoords.x - mouseLeftDragStartCoords.x;
			final float height = mouseLeftDragStartCoords.y - mouseLeftDragEndCoords.y;

			selectionBox.setBounds(x,y,width,height);
		}
		else
		{
			selectionBox.remove();
		}

		forceSpacebarTimer -= delta;


	}


	@Override
	public void render(float delta) {
		// http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16813
		/*
		Runtime rt = Runtime.getRuntime();
		long max = rt.maxMemory();
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		long used = total - free;
		int availableProcessors = rt.availableProcessors();
		*/
		label.setText(

			"FPS: " + Gdx.graphics.getFramesPerSecond() +
				"\nMem: " + (Gdx.app.getJavaHeap() / 1024 / 1024) + " MB");


		if (selectedSoldiers.size < 1){
			bottomLabel.setText("");
		}else{
			bottomLabel.setText("Selected: "+selectedSoldiers.size);
		}


		if(spacebarDown || forceSpacebarTimer>0)
		{
			moveCommandDecal.setPosition(lastMoveCommandLocation);
			moveCommandDecal.translateY(0.05f);
			world.decalBatch.add(moveCommandDecal);
		}

	}


	private final Vector3 vec1 = new Vector3();
	private final Vector3 vec2 = new Vector3();
	private final Vector3 vec3 = new Vector3();
	private final Vector3 vec4 = new Vector3();
	private boolean mouseLeftDown = false;
	private int mouseLeftDragCount = 0;
	private boolean mouseLeftDrag = false;
	private final Vector2 mouseLeftDragStartCoords = new Vector2();
	private final Vector2 mouseLeftDragEndCoords = new Vector2();
	private final Vector3 lastMoveCommandLocation = new Vector3();



	private boolean spacebarDown = false;
	private float forceSpacebarTimer;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
		{
			mouseLeftDragStartCoords.set(screenX, screenY);
			mouseLeftDown = true;
			mouseLeftDragCount = 0;
			mouseLeftDrag = false;

		}
		return false;
	}

	private Ray getWorldCoord(float screenX, float screenY, Vector3 storeWorldCoord)
	{
		Ray ray = world.cameraManager.cam.getPickRay(screenX, screenY);
		final float distance = -ray.origin.y / ray.direction.y;
		storeWorldCoord.set(ray.direction).scl(distance).add(ray.origin);
		return ray;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
		{
			mouseLeftDown = false;
			selectedSoldiers.clear();

			if(!mouseLeftDrag)
			{
				// Direct selection
				Ray ray = getWorldCoord(screenX, screenY, vec1);
				SoldierGameObject closestSgo = null;
				float closestDistance = Float.MAX_VALUE;

				for (GameObject gameObject : world.gameObjects) {
					if(gameObject instanceof SoldierGameObject)
					{
						SoldierGameObject sgo = (SoldierGameObject) gameObject;
						sgo.selected  = false;
						float sgoDistance = sgo.intersects(ray);
						if(sgoDistance > 0 && sgoDistance < closestDistance)
						{
							closestDistance = sgoDistance;
							closestSgo = sgo;
						}
					}
				}

				if(closestSgo!=null){
					closestSgo.selected = true;
					selectedSoldiers.add(closestSgo);
				}
			}
			else
			{
				// Box Selection
				mouseLeftDrag = false;
				mouseLeftDragEndCoords.set(screenX, screenY);

				getWorldCoord(mouseLeftDragStartCoords.x, mouseLeftDragStartCoords.y, vec1);
				getWorldCoord(mouseLeftDragStartCoords.x, mouseLeftDragEndCoords.y, vec2);
				getWorldCoord(mouseLeftDragEndCoords.x, mouseLeftDragEndCoords.y, vec3);
				getWorldCoord(mouseLeftDragEndCoords.x, mouseLeftDragStartCoords.y, vec4);

				//world.addGameObject(new DebugPosGameObject(world, vec1, vec2,vec3,vec4));

				for (GameObject gameObject : world.gameObjects) {
					if(gameObject instanceof SoldierGameObject)
					{
						SoldierGameObject sgo = (SoldierGameObject) gameObject;
						sgo.selected  = UtMath.isPointInQuadrilateral(sgo.translation, vec1, vec2, vec3, vec4);
						if(sgo.selected){
							selectedSoldiers.add(sgo);
						}
					}
				}


			}

		}
		else if(button == Input.Buttons.RIGHT)
		{
			if(selectedSoldiers.size > 0)
			{
				forceSpacebarTimer = 3f;
				getWorldCoord(screenX, screenY, lastMoveCommandLocation);

				for (SoldierGameObject sgo : selectedSoldiers) {
					ScenarioFactory.seekPosition(sgo.token, lastMoveCommandLocation);

				}

			}

			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(mouseLeftDown)
		{
			if(mouseLeftDragCount++ > 2)
			{
				mouseLeftDrag = true;
				mouseLeftDragEndCoords.set(screenX, screenY);
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode)
		{
			case Input.Keys.SPACE:
				spacebarDown = true;
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode)
		{
			case Input.Keys.SPACE:
				spacebarDown = false;
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
