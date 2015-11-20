package asf.medieval;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/20/15.
 */
public class LoadingScreen extends AbstractScreen {
	private Label loadingLabel;
	private String loadingText;

	public LoadingScreen(MedievalApp medievalApp) {
		super(medievalApp);
	}

	@Override
	public void show() {
		super.show();
		super.showBackground(false);

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);


		Table table = new Table(app.skin);
		app.stageScreen.addActor(table);
		table.setFillParent(true);

		loadingText = app.i18n.get("loading");

		loadingLabel = new Label(loadingText+" ...", app.skin);
		//loadingLabel.setFontScale(3);
		table.add(loadingLabel).minSize(300,100).align(Align.left);


	}

	@Override
	public void resize(int width, int height) {
		super.resize(width,height);
	}

	private float loadingCount = 0;

	@Override
	public void render(float delta) {

		loadingCount += delta;
		float n = UtMath.scalarLimitsInterpolation(loadingCount, 0f, .75f, 0f, 4f);
		if(n < 1){
			loadingLabel.setText(loadingText+" .");
		}else if(n <2){
			loadingLabel.setText(loadingText+" ..");
		}else if(n<3){
			loadingLabel.setText(loadingText+" ...");
		}else if(n<4){
			loadingLabel.setText(loadingText+" ..");
		}else{
			loadingCount = 0;
		}

		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render(delta);
	}


	@Override
	public boolean killsWorld() {
		return false;
	}

	@Override
	public ScreenId getScreenId() {
		return ScreenId.Loading;
	}
}
