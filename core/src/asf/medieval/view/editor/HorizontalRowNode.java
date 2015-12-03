package asf.medieval.view.editor;

import asf.medieval.view.MedievalWorld;

/**
 * Created by daniel on 12/2/15.
 */
public class HorizontalRowNode extends MultiNode implements EditorNode {

	public HorizontalRowNode(String modeName, MedievalWorld world, EditorNode... modes) {
		super(modeName, world, modes);
	}

	@Override
	public void initUi() {
		super.initUi();

		for (EditorNode mode : modes) {
			toolTable.add(mode.getToolbarActor());
		}
	}
}
