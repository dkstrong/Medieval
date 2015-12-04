package asf.medieval.view;

import asf.medieval.net.NetworkedGameClient;
import asf.medieval.model.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class HudView implements View, InputProcessor {

	private MedievalWorld world;
	public HudBuildView hudBuildView;
	public HudCommandView hudCommandView;
	public HudSelectionView hudSelectionView;


	private Container topRightLabelContainer;
	private Label topRightLabel;

	private Container brLabelContainer;
	private Label brLabel;

	private Container topLeftLabelContainer;
	private Label topLeftLabel;


	public HudView(MedievalWorld world) {
		this.world = world;
		hudBuildView = new HudBuildView(world);
		hudCommandView = new HudCommandView(world);
		hudSelectionView = new HudSelectionView(world);

		topRightLabel = new Label("Hello, there!", world.app.skin);
		topRightLabel.setAlignment(Align.topRight, Align.topRight);
		topRightLabelContainer = new Container<Label>(topRightLabel);
		topRightLabelContainer.align(Align.topRight).pad(10);
		world.stage.addActor(topRightLabelContainer);


		brLabel = new Label("Hello, there!", world.app.skin);
		brLabel.setAlignment(Align.bottomRight, Align.bottomRight);
		brLabelContainer = new Container<Label>(brLabel);
		brLabelContainer.align(Align.bottomRight).pad(10);
		world.stage.addActor(brLabelContainer);

		topLeftLabel = new Label("Hello, there!", world.app.skin);
		topLeftLabel.setAlignment(Align.topLeft, Align.topLeft);
		topLeftLabelContainer = new Container<Label>(topLeftLabel);
		topLeftLabelContainer.align(Align.topLeft).pad(10);
		world.stage.addActor(topLeftLabelContainer);


		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int width, int height) {


		topRightLabelContainer.setBounds(0, 0, width, height);
		brLabelContainer.setBounds(0, 0, width, height);
		topLeftLabelContainer.setBounds(0, 0, width, height);
		if(hudBuildView!=null)
			hudBuildView.resize(width,height);
		if(hudCommandView!=null)
			hudCommandView.resize(width, height);
		if(hudSelectionView!=null)
			hudSelectionView.resize(width, height);
	}

	@Override
	public void update(float delta) {
		if(hudBuildView!=null)
			hudBuildView.update(delta);
		if(hudCommandView!=null)
			hudCommandView.update(delta);
		if(hudSelectionView!=null)
			hudSelectionView.update(delta);
	}


	private float requestPingTimer = 5;

	@Override
	public void render(float delta) {

		if(hudBuildView!=null)
			hudBuildView.render(delta);
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
				(gameServerStatusString != null ? "\n" + gameServerStatusString : "") +
				(gameClientStatusString != null ? "\n" + gameClientStatusString : "")


		);


		if (hudSelectionView==null || hudSelectionView.selectedViews.size < 1) {
			brLabel.setText("");
		} else {
			brLabel.setText("Selected: " + hudSelectionView.selectedViews.size);
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
