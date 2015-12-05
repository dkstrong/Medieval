package asf.medieval.view;

import asf.medieval.model.Player;
import asf.medieval.model.ResourceId;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 12/5/15.
 */
public class HudResourceView implements View {
	private final MedievalWorld world;

	private Container<Table> rootContainer;
	private Table rootTable;
	private Label[] resourceLabels;
	private Label popLabel;

	private Player player;

	public HudResourceView(MedievalWorld world) {
		this.world = world;

		player = world.scenario.getPlayer(world.gameClient.user.id);
		if(player == null)
			System.out.println("damn");

		rootTable = new Table(world.app.skin);
		rootTable.align(Align.topRight);
		rootContainer = new Container<Table>(rootTable);
		rootContainer.setFillParent(true);
		rootContainer.align(Align.topRight).padTop(5).padRight(10);

		world.stage.addActor(rootContainer);

		rootTable.row().fill();
		resourceLabels = new Label[world.resourceViewInfo.length];
		for (ResourceId resourceId : ResourceId.values()) {
			ResourceViewInfo rvi = world.resourceViewInfo[resourceId.ordinal()];
			Label label = new Label("", world.app.skin);
			resourceLabels[resourceId.ordinal()] = label;
			rootTable.add(label);
		}

		Label label = new Label("", world.app.skin);
		popLabel = label;
		rootTable.add(label);


	}


	@Override
	public void update(float delta) {
		for (int i = 0; i < world.resourceViewInfo.length; i++) {
			ResourceViewInfo rvi = world.resourceViewInfo[i];
			int amount = player.resources[i];
			resourceLabels[i].setText(rvi.name+": "+amount);
		}

		popLabel.setText("Pop: "+player.pop+" / "+player.popcap);
	}

	@Override
	public void render(float delta) {


	}
}
