package asf.medieval;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by daniel on 11/20/15.
 */
public class PauseScreen extends AbstractScreen implements InputProcessor, EventListener {

	public PauseScreen(MedievalApp medievalApp) {
		super(medievalApp);
	}

	private Window window;
	private Button resumeButton, returnToMainMenuButton;

	@Override
	public void show() {
		if(app.world!=null)
			app.world.setPaused(true);
		super.show();

		Skin skin = app.skin;
		I18NBundle i18n = app.i18n;


		window = new Window(i18n.get("paused"),skin);
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
		resumeButton = new TextButton(i18n.get("resume"), skin);
		resumeButton.addCaptureListener(this);
		window.add(resumeButton).minSize(150,100);

		window.row();
		returnToMainMenuButton = new TextButton(i18n.get("quitGame"),skin);
		returnToMainMenuButton.addCaptureListener(this);
		window.add(returnToMainMenuButton).minSize(150,100).padTop(50);


	}

	@Override
	public void hide() {
		super.hide();
		if(app.world!=null)
			app.world.setPaused(false);
	}

	@Override
	public void resize(int graphicsWidth, int graphicsHeight) {
		super.resize(graphicsWidth, graphicsHeight);
		float width = graphicsWidth * 0.7f;
		float height = graphicsHeight * 0.65f;
		window.setBounds(
			(graphicsWidth - width) * 0.5f,
			(graphicsHeight - height) * 0.5f,
			width,
			height);
	}


	@Override
	public boolean killsWorld() {
		return false;
	}

	@Override
	public ScreenId getScreenId() {
		return ScreenId.Pause;
	}


	@Override
	public boolean handle(Event event) {
		if (event.getTarget() == window) {
			if (event instanceof InputEvent) {
				InputEvent inputEvent = (InputEvent) event;
				if (inputEvent.getType() == InputEvent.Type.touchDown) {
					float clickX = inputEvent.getStageX();
					float clickY = inputEvent.getStageY();
					if (clickX < window.getX() || clickY < window.getY() || clickX > window.getX() + window.getWidth() || clickY > window.getY() + window.getHeight()) {
						app.setScreen(null);
						return true;
					}
				}
			}
			return false;
		}

		if (!(event instanceof ChangeListener.ChangeEvent)) {
			return false;
		}

		if (event.getListenerActor() == resumeButton) {
			app.setScreen(null);
			return true;
		}else if(event.getListenerActor() == returnToMainMenuButton){
			app.setScreen(ScreenId.Main);
		}

		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.ESCAPE){
			app.setScreen(null);
			return true;
		}
		return false;
	}


	@Override
	public boolean keyTyped(char character) {
		return false;
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
	public boolean scrolled(int amount) {
		return false;
	}



}
