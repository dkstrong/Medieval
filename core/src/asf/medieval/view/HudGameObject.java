package asf.medieval.view;

import asf.medieval.model.Command;
import asf.medieval.net.NetworkedGameClient;
import asf.medieval.net.Player;
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

	private Container topRightLabelContainer;
	private Label topRightLabel;

	private Container bottomLeftLabelContainer;
	private Label bottomLeftLabel;

	private Container topLeftLabelContainer;
	private Label topLeftLabel;

	private StretchableImage selectionBox;
	private final Decal moveCommandDecal = new Decal();

	private Array<SelectableGameObject> selectedSoldiers = new Array<SelectableGameObject>(true, 8, SelectableGameObject.class);

	public HudGameObject(MedievalWorld world) {
		this.world = world;
		topRightLabel = new Label("Hello, there!", world.app.skin);
		topRightLabel.setAlignment(Align.topRight, Align.topRight);
		topRightLabelContainer = new Container<Label>(topRightLabel);
		topRightLabelContainer.align(Align.topRight).pad(10);
		world.stage.addActor(topRightLabelContainer);


		bottomLeftLabel = new Label("Hello, there!", world.app.skin);
		bottomLeftLabel.setAlignment(Align.bottomLeft, Align.bottomLeft);
		bottomLeftLabelContainer = new Container<Label>(bottomLeftLabel);
		bottomLeftLabelContainer.align(Align.bottomLeft).pad(10);
		world.stage.addActor(bottomLeftLabelContainer);

		topLeftLabel = new Label("Hello, there!", world.app.skin);
		topLeftLabel.setAlignment(Align.topLeft, Align.topLeft);
		topLeftLabelContainer = new Container<Label>(topLeftLabel);
		topLeftLabelContainer.align(Align.topLeft).pad(10);
		world.stage.addActor(topLeftLabelContainer);

		selectionBox = new StretchableImage(world.pack.findRegion("Interface/Hud/selection-box"));


		moveCommandDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		moveCommandDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		moveCommandDecal.setDimensions(2, 2);
		moveCommandDecal.setColor(1, 1, 1, 1);
		moveCommandDecal.rotateX(-90);




		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {


		topRightLabelContainer.setBounds(0, 0, width, height);
		bottomLeftLabelContainer.setBounds(0, 0, width, height);
		topLeftLabelContainer.setBounds(0, 0, width, height);

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


	private float requestPingTimer = 5;

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

		String gameServerStatusString = null;
		String gameClientStatusString=null;
		if(world.gameServer!=null){
			gameServerStatusString = world.gameServer.isBound() ? "Server Online" : "Server Offline";
		}

		if(world.gameClient instanceof NetworkedGameClient){
			NetworkedGameClient networkedGameClient = (NetworkedGameClient) world.gameClient;
			if(gameServerStatusString== null)
				gameServerStatusString = networkedGameClient.isConnected() ? "Connected to: "+ networkedGameClient.hostName+":"+ networkedGameClient.tcpPort : "No Server Connection";

			gameClientStatusString="Players:";
			if(networkedGameClient.player.id > 0 && networkedGameClient.players.containsKey(networkedGameClient.player.id)){
				gameClientStatusString+= "\n"+String.valueOf(networkedGameClient.player);
				requestPingTimer-=delta;
				if(requestPingTimer <0){
					requestPingTimer = 15;
					networkedGameClient.client.updateReturnTripTime();
				}
				int returnTripTime = networkedGameClient.client.getReturnTripTime();
				gameClientStatusString+=" ["+returnTripTime+"]";
			}
			for (Player player : networkedGameClient.players.values()) {
				if(player.id != networkedGameClient.player.id){
					gameClientStatusString+= "\n"+String.valueOf(player);
				}
			}
			String topLeftText = String.valueOf(world.gameClient);
			topLeftText+="\nbuffer: "+networkedGameClient.numLockstepFramesInBuffer();
			topLeftLabel.setText(topLeftText);

		}else{
			topLeftLabel.setText("");
		}

		topRightLabel.setText(

			"FPS: " + Gdx.graphics.getFramesPerSecond() +
				"\nMem: " + (Gdx.app.getJavaHeap() / 1024 / 1024) + " MB" +
				"\n" +
				(gameServerStatusString != null ? "\n" + gameServerStatusString : "") +
				(gameClientStatusString != null ? "\n" + gameClientStatusString : "")


		);


		if (selectedSoldiers.size < 1){
			bottomLeftLabel.setText("");
		}else{
			bottomLeftLabel.setText("Selected: " + selectedSoldiers.size);
		}


		if(spacebarDown || forceSpacebarTimer>0)
		{
			moveCommandDecal.setPosition(lastMoveCommandLocation);
			world.scenario.heightField.getWeightedNormalAt(lastMoveCommandLocation, vec1);
			moveCommandDecal.setRotation(vec1, Vector3.Y);
			vec1.scl(0.11f);
			moveCommandDecal.translate(vec1);

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


		// this just gets the intersection point of the ray to the ground plane (y==0)
		//final float distance = -ray.origin.y / ray.direction.y;
		//storeWorldCoord.set(ray.direction).scl(distance).add(ray.origin);


		world.scenario.heightField.intersect(ray, storeWorldCoord);


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
				Ray ray = world.cameraManager.cam.getPickRay(screenX, screenY);
				final boolean intersectsTerrain = world.scenario.heightField.intersect(ray, vec1);
				final float groundDst2 = intersectsTerrain ? ray.origin.dst2(vec1) : Float.MAX_VALUE;

				SelectableGameObject closestSgo = null;
				float closestDistance = Float.MAX_VALUE; // this distance value is only useful for comparing shapes intersections

				for (GameObject gameObject : world.gameObjects) {
					if(gameObject instanceof SelectableGameObject)
					{
						SelectableGameObject sgo = (SelectableGameObject) gameObject;
						sgo.setSelected(false);

						float sgoDistance = sgo.intersects(ray);
						if(sgoDistance > 0 && sgoDistance < closestDistance && ray.origin.dst2(sgo.getTranslation()) < groundDst2)
						{
							closestDistance = sgoDistance;
							closestSgo = sgo;
						}
					}
				}

				if(closestSgo!=null){
					closestSgo.setSelected(true);
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
					if(gameObject instanceof SelectableGameObject)
					{
						SelectableGameObject sgo = (SelectableGameObject) gameObject;
						sgo.setSelected(UtMath.isPointInQuadrilateral(sgo.getTranslation(), vec1, vec2, vec3, vec4));
						if(sgo.isSelected()){
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

				for (SelectableGameObject sgo : selectedSoldiers) {
					if(sgo instanceof SoldierGameObject)
					{
						Command command = new Command();
						command.tokenId = ((SoldierGameObject) sgo).token.id;
						command.location = new Vector3(lastMoveCommandLocation);
						world.gameClient.sendCommand(command);
						//sgo.token.setTarget(lastMoveCommandLocation);
					}

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
			case Input.Keys.I:
				Command command = new Command();
				command.location = new Vector3(world.cameraManager.rtsCamController.center);
				world.gameClient.sendCommand(command);
				break;
			case Input.Keys.B:
				Command buildCommand = new Command();
				buildCommand.location = new Vector3(world.cameraManager.rtsCamController.center);
				buildCommand.structure = true;
				world.gameClient.sendCommand(buildCommand);
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
