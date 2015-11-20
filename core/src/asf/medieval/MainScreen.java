package asf.medieval;

import asf.medieval.net.GameServerConfig;
import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Random;

/**
 * Created by daniel on 11/20/15.
 */
public class MainScreen extends AbstractScreen{

	public MainScreen(MedievalApp app) {
		super(app);
	}

	private Button hostGameButton, joinGameButton, offlineGameButton;
	private Table table;
	private Table buttonsTable;

	private TextField hostNameTextfield;
	private TextField portTextfield;


	@Override
	public void show() {
		super.show();
		super.showMainBackground(true);
		super.showBasic(app.i18n.get("title"), true);

		InternalListener internalListener = new InternalListener();

		Label hostNameLabel = new Label(app.i18n.get("hostName")+":",app.skin);
		hostNameLabel.setAlignment(Align.right,Align.right);
		Label portLabel = new Label(app.i18n.get("port")+":",app.skin);
		portLabel.setAlignment(Align.right,Align.right);

		hostNameTextfield = new TextField("fox.dkstrong.com",app.skin);
		portTextfield = new TextField("27677",app.skin);

		buttonsTable = new Table(app.skin);
		hostGameButton = new TextButton(app.i18n.get("hostGame")+"", app.skin, "default");
		joinGameButton = new TextButton(app.i18n.get("joinGame"), app.skin, "default");
		offlineGameButton = new TextButton(app.i18n.get("offlineGame"), app.skin, "default");

		table = new Table(app.skin);
		//table.setBackground("black");
		app.stageScreen.addActor(table);

		table.row();
		table.add(hostNameLabel).align(Align.right);
		table.add(hostNameTextfield).colspan(2);

		table.row();
		table.add(portLabel).align(Align.right);
		table.add(portTextfield).colspan(2);

		table.row();
		table.add(buttonsTable).colspan(3).fill();

		buttonsTable.row();
		buttonsTable.add(hostGameButton);
		buttonsTable.add(joinGameButton);
		buttonsTable.add(offlineGameButton);

		hostGameButton.addCaptureListener(internalListener);
		joinGameButton.addCaptureListener(internalListener);
		offlineGameButton.addCaptureListener(internalListener);

	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void resize(int graphicsWidth, int graphicsHeight) {
		super.resize(graphicsWidth, graphicsHeight);

		float tableWidth = graphicsWidth * 0.75f;
		float tableHeight = graphicsHeight * 0.75f;
		table.setBounds(
			(graphicsWidth - tableWidth) / 2f,
			(graphicsHeight - tableHeight) / 2f,
			tableWidth,
			tableHeight
		);


		for (Cell cell : table.getCells()) {
			if(cell.getActor() instanceof Label){
				cell.pad(5,20,5,20);
			}else if(cell.getActor() instanceof  TextField){
				cell.pad(5,20,5,20).expandX().fill();
			}
		}

		for (Cell cell : buttonsTable.getCells()) {
			if(cell.getActor() instanceof TextButton){
				cell.pad(10,10,10,10).expandX().fill().minHeight(70);

			}
		}

	}

	@Override
	public ScreenId getScreenId() {
		return ScreenId.Main;
	}

	@Override
	public boolean killsWorld() {
		return true;
	}

	@Override
	protected void onBackClicked() {
		app.exitApp();
	}

	private class InternalListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			String name = System.getProperty("user.name");

			if (actor == hostGameButton) {
				MedievalWorld.Settings settings = new MedievalWorld.Settings();
				settings.random = new Random(1);
				settings.server = true;
				settings.gameServerConfig = new GameServerConfig();
				settings.gameServerConfig.tcpPort = Integer.parseInt(portTextfield.getText().trim());
				settings.gameServerConfig.udpPort = settings.gameServerConfig.tcpPort;
				app.loadWorld(settings);
			} else if (actor == joinGameButton) {
				MedievalWorld.Settings settings = new MedievalWorld.Settings();
				settings.random = new Random(1);
				settings.client = true;
				settings.gameServerConfig = new GameServerConfig();
				settings.hostName = hostNameTextfield.getText().trim();
				settings.gameServerConfig.tcpPort = Integer.parseInt(portTextfield.getText().trim());
				settings.gameServerConfig.udpPort = settings.gameServerConfig.tcpPort;

				app.loadWorld(settings);
			} else if (actor == offlineGameButton) {
				MedievalWorld.Settings settings = new MedievalWorld.Settings();

				settings.random = new Random(1);

				app.loadWorld(settings);

			}
		}
	}
}
