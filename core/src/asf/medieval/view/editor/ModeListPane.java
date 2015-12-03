package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;
import asf.medieval.view.View;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 12/2/15.
 */
public class ModeListPane extends MultiModePane implements EditorMode{

	public ModeListPane(String modeName, MedievalWorld world, EditorMode... modes) {
		super(modeName, world, modes);
	}

	@Override
	public void initUi() {
		super.initUi();

		for (EditorMode mode : modes) {
			toolTable.add(mode.getToolbarActor());
		}
	}
}
