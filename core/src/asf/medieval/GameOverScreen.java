package asf.medieval;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/20/15.
 */
public class GameOverScreen extends AbstractScreen implements EventListener {

	public GameOverScreen(MedievalApp medievalApp) {
		super(medievalApp);
	}

	private Window window;
	private Button leaveGameButton;

	@Override
	public void show() {
		super.show();
		//settings = app.worldSettings;

		String titleText;
		titleText = app.i18n.get("gameOver");


		window = new Window(titleText, app.skin);
		window.getTitleLabel().setAlignment(Align.center);
		app.stageScreen.addActor(window);

		//window.setFillParent(true);

		window.center();
		window.setMovable(false);
		window.setModal(true);
		window.addCaptureListener(this);
		// TODO: used to remove button table here, but new libgdx api doesnt have getButtonTable()
		//window.removeActor(window.getButtonTable());

		window.row();
		leaveGameButton = new TextButton(app.i18n.get("leaveGame"),app.skin);
		leaveGameButton.addCaptureListener(this);
		window.add(leaveGameButton).minSize(150, 100).padTop(50);

	}

	@Override
	public void resize(int graphicsWidth, int graphicsHeight) {
		super.resize(graphicsWidth, graphicsHeight);
		float width = graphicsWidth * .75f;
		float height = graphicsHeight * .75f;
		window.setBounds(
			(graphicsWidth - width) / 2f,
			(graphicsHeight - height) / 2f,
			width, height);

	}

	@Override
	public boolean killsWorld() {
		return false;
	}

	@Override
	public ScreenId getScreenId() {
		return ScreenId.GameOverScreen;
	}

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ChangeListener.ChangeEvent)) {
			return false;
		}

		if (event.getListenerActor() == leaveGameButton) {
			app.setScreen(ScreenId.Main);

		}

		return false;
	}
}
