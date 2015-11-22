package asf.medieval.view;

import asf.medieval.model.Command;
import asf.medieval.net.NetworkedGameClient;
import asf.medieval.model.Player;
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

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class HudView implements View, InputProcessor {

	private MedievalWorld world;
	public HudCommandView hudCommandView;
	public HudSelectionView hudSelectionView;

	private Container topRightLabelContainer;
	private Label topRightLabel;

	private Container bottomLeftLabelContainer;
	private Label bottomLeftLabel;

	private Container topLeftLabelContainer;
	private Label topLeftLabel;


	public HudView(MedievalWorld world) {
		this.world = world;
		hudCommandView = new HudCommandView(world);
		hudSelectionView = new HudSelectionView(world);
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


		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {


		topRightLabelContainer.setBounds(0, 0, width, height);
		bottomLeftLabelContainer.setBounds(0, 0, width, height);
		topLeftLabelContainer.setBounds(0, 0, width, height);
		if(hudCommandView!=null)
			hudCommandView.resize(width, height);
		if(hudSelectionView!=null)
			hudSelectionView.resize(width, height);
	}

	@Override
	public void update(float delta) {
		if(hudCommandView!=null)
			hudCommandView.update(delta);
		if(hudSelectionView!=null)
			hudSelectionView.update(delta);
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

		if(hudCommandView!=null)
			hudCommandView.render(delta);
		if(hudSelectionView!=null)
			hudSelectionView.render(delta);


		String gameServerStatusString = null;
		String gameClientStatusString = null;
		if (world.gameServer != null) {
			gameServerStatusString = world.gameServer.isBound() ? "Server Online" : "Server Offline";
		}

		if (world.gameClient instanceof NetworkedGameClient) {
			NetworkedGameClient networkedGameClient = (NetworkedGameClient) world.gameClient;
			if (gameServerStatusString == null)
				gameServerStatusString = networkedGameClient.isConnected() ? "Connected to: " + networkedGameClient.hostName + ":" + networkedGameClient.tcpPort : "No Server Connection";

			gameClientStatusString = "Players:";
			if (networkedGameClient.player.id > 0 && networkedGameClient.players.containsKey(networkedGameClient.player.id)) {
				gameClientStatusString += "\n" + String.valueOf(networkedGameClient.player);
				requestPingTimer -= delta;
				if (requestPingTimer < 0) {
					requestPingTimer = 15;
					networkedGameClient.client.updateReturnTripTime();
				}
				int returnTripTime = networkedGameClient.client.getReturnTripTime();
				gameClientStatusString += " [" + returnTripTime + "]";
			}
			for (Player player : networkedGameClient.players.values()) {
				if (player.id != networkedGameClient.player.id) {
					gameClientStatusString += "\n" + String.valueOf(player);
				}
			}
			String topLeftText = String.valueOf(world.gameClient);
			topLeftText += "\nbuffer: " + networkedGameClient.numLockstepFramesInBuffer();
			topLeftLabel.setText(topLeftText);

		} else {
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

		if (hudSelectionView==null || hudSelectionView.selectedViews.size < 1) {
			bottomLeftLabel.setText("");
		} else {
			bottomLeftLabel.setText("Selected: " + hudSelectionView.selectedViews.size);
		}

	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
