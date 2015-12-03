package asf.medieval.view.editor;

import asf.medieval.view.View;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 12/2/15.
 */
public interface EditorNode extends Disposable, InputProcessor {

	public void initUi();

	public void refreshUi();

	public Actor getToolbarActor();

	public void setEnabled(boolean enabled);

	public void update(float delta);


	public void render(float delta);

}
