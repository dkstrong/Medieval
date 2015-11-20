package asf.medieval;

import asf.medieval.view.MedievalWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/20/15.
 */
public abstract class AbstractScreen {

	final MedievalApp app;
	protected Label titleLabel;
	private ImageButton soundButton, automatchButton, quitButton;

	private boolean mainTitle;
	protected float titleHeight;
	protected float margin;
	protected float contentStartY;

	private final InternalCL internalCl = new InternalCL();

	public AbstractScreen(MedievalApp app) {
		this.app = app;
	}

	public void show() {
		if (this instanceof InputProcessor) {
			Gdx.input.setInputProcessor(new InputMultiplexer(app.stage, internalCl, (InputProcessor) this));
		} else {
			Gdx.input.setInputProcessor(new InputMultiplexer(app.stage, internalCl));
		}

	}

	protected void showBackground(boolean withButtons) {
		app.stageScreen.addActor(app.backgroundImage);

		if (withButtons) {
			soundButton = new ImageButton(app.skin, "sound");
			app.stageScreen.addActor(soundButton);
			soundButton.addCaptureListener(internalCl);
			soundButton.setChecked(!app.prefs.getBoolean("musicEnabled", true));

			/*
			if (app.client.automatching) {
				automatchButton = new ImageButton(app.skin, "automatch");
				app.stageScreen.addActor(automatchButton);
				automatchButton.addCaptureListener(internalCl);
			}*/

			quitButton = new ImageButton(app.skin, mainTitle ? "exit" : "back");
			app.stageScreen.addActor(quitButton);
			quitButton.addCaptureListener(internalCl);
		}
	}

	protected void showMainBackground(boolean withButtons) {
		app.stageScreen.addActor(app.backgroundImage);

		if (withButtons) {
			soundButton = new ImageButton(app.skin, "sound");
			app.stageScreen.addActor(soundButton);
			soundButton.addCaptureListener(internalCl);
			soundButton.setChecked(!app.prefs.getBoolean("musicEnabled", true));
/*
			if (app.client.automatching) {
				automatchButton = new ImageButton(app.skin, "automatch");
				app.stageScreen.addActor(automatchButton);
				automatchButton.addCaptureListener(internalCl);
			}*/

			quitButton = new ImageButton(app.skin, "exit");
			app.stageScreen.addActor(quitButton);
			quitButton.addCaptureListener(internalCl);
		}
	}

	protected void showBasic(String title, boolean mainTitle) {

		this.mainTitle = mainTitle;

		titleLabel = new Label(title, app.skin, mainTitle ? "title" : "title-small");
		Label.LabelStyle ls;
		titleLabel.setAlignment(Align.center);

		if (!mainTitle) {
			titleLabel.setWrap(true);
		}
		app.stageScreen.addActor(titleLabel);

	}

	public void resize(int graphicsWidth, int graphicsHeight) {
		if (app.backgroundImage != null){
			app.backgroundImage.setBounds(0, 0, graphicsWidth, graphicsHeight);

		}


		titleHeight = mainTitle ? 75 : 125;
		if (titleLabel != null)
			titleLabel.setBounds(0, graphicsHeight - titleHeight, graphicsWidth, titleHeight);

		float buttonSize = 70;
		margin = 5;
		contentStartY = buttonSize + margin;

		if (soundButton != null)
			soundButton.setBounds(
				margin,
				margin,
				buttonSize, buttonSize);

		if (automatchButton != null)
			automatchButton.setBounds(
				margin + buttonSize + margin,
				margin,
				buttonSize, buttonSize);

		if (quitButton != null)
			quitButton.setBounds(
				graphicsWidth - buttonSize - margin,
				margin,
				buttonSize, buttonSize);
	}

	public void render(float delta) {

	}

	public void pause() {

	}

	public void resume() {

	}

	public void hide() {

	}

	protected void onBackClicked() {

	}

	public abstract boolean killsWorld();

	public abstract ScreenId getScreenId();

	private class InternalCL extends ChangeListener implements InputProcessor {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			if (actor == soundButton) {
				//app.prefs.putBoolean("musicEnabled", !soundButton.isChecked());
				//app.prefs.flush();
			} else if (actor == quitButton) {
				onBackClicked();
			}
		}

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Input.Keys.ESCAPE) {
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					app.exitApp();
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			if (quitButton != null && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
				onBackClicked();
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

}
