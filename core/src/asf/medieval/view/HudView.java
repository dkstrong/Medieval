package asf.medieval.view;

import asf.medieval.model.Command;
import asf.medieval.model.Token;
import asf.medieval.net.NetworkedGameClient;
import asf.medieval.model.Player;
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
public class HudView implements View,InputProcessor {

	private MedievalWorld world;

	private Container topRightLabelContainer;
	private Label topRightLabel;

	private Container bottomLeftLabelContainer;
	private Label bottomLeftLabel;

	private Container topLeftLabelContainer;
	private Label topLeftLabel;

	private StretchableImage selectionBox;
	private final Decal moveCommandDecal = new Decal();

	private Array<SelectableView> selectedViews = new Array<SelectableView>(true, 8, SelectableView.class);

	public HudView(MedievalWorld world) {
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


		forceSpacebarTimer -= delta;

		for (SelectableView selectedView : selectedViews) {
			if(!canSelectToken(selectedView.getToken()))
			{
				selectedView.setSelected(false);
				selectedViews.removeValue(selectedView, true);
			}
		}
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
				"\nTokens: " + world.scenario.tokens.size +
				"\n" +
				(gameServerStatusString != null ? "\n" + gameServerStatusString : "") +
				(gameClientStatusString != null ? "\n" + gameClientStatusString : "")


		);


		if (selectedViews.size < 1){
			bottomLeftLabel.setText("");
		}else{
			bottomLeftLabel.setText("Selected: " + selectedViews.size);
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

	private boolean canSelectToken(Token token){
		if(token.damage!=null && token.damage.health <=0) return false;
		if(token.owner.id!= world.gameClient.player.id) return false;

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
		{
			mouseLeftDown = false;
			selectedViews.clear();

			if(!mouseLeftDrag)
			{
				// Direct selection
				Ray ray = world.cameraManager.cam.getPickRay(screenX, screenY);
				final boolean intersectsTerrain = world.scenario.heightField.intersect(ray, vec1);
				final float groundDst2 = intersectsTerrain ? ray.origin.dst2(vec1) : Float.MAX_VALUE;

				SelectableView closestSgo = null;
				float closestDistance = Float.MAX_VALUE; // this distance value is only useful for comparing shapes intersections

				for (View view : world.gameObjects) {
					if(view instanceof SelectableView)
					{
						SelectableView sgo = (SelectableView) view;
						sgo.setSelected(false);
						if(canSelectToken(sgo.getToken()))
						{
							float sgoDistance = sgo.intersects(ray);
							if(sgoDistance > 0 && sgoDistance < closestDistance && ray.origin.dst2(sgo.getTranslation()) < groundDst2)
							{
								closestDistance = sgoDistance;
								closestSgo = sgo;
							}
						}
					}
				}

				if(closestSgo!=null){
					closestSgo.setSelected(true);
					selectedViews.add(closestSgo);
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

				//world.addGameObject(new DebugShapeView(world, vec1, vec2,vec3,vec4));

				for (View view : world.gameObjects) {
					if(view instanceof SelectableView)
					{
						SelectableView sgo = (SelectableView) view;
						sgo.setSelected(false);
						if(canSelectToken(sgo.getToken())){
							sgo.setSelected(UtMath.isPointInQuadrilateral(sgo.getTranslation(), vec1, vec2, vec3, vec4));
							if(sgo.isSelected()){
								selectedViews.add(sgo);
							}
						}

					}
				}


			}

		}
		else if(button == Input.Buttons.RIGHT)
		{
			if(selectedViews.size > 0)
			{
				forceSpacebarTimer = 3f;
				getWorldCoord(screenX, screenY, lastMoveCommandLocation);

				for (SelectableView sgo : selectedViews) {
					if(sgo instanceof InfantryView)
					{
						Command command = new Command();
						command.tokenId = ((InfantryView) sgo).token.id;
						command.location = UtMath.toVector2(lastMoveCommandLocation, new Vector2());
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
				command.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
				world.gameClient.sendCommand(command);
				break;
			case Input.Keys.J:
				Command jimmyCommand = new Command();
				jimmyCommand.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
				jimmyCommand.jimmy = true;
				world.gameClient.sendCommand(jimmyCommand);
				break;
			case Input.Keys.B:
				Command buildCommand = new Command();
				buildCommand.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
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
