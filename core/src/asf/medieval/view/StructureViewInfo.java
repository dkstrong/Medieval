package asf.medieval.view;

import asf.medieval.model.StructureId;
import asf.medieval.shape.Box;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;

/**
 * Created by daniel on 12/16/15.
 */
public class StructureViewInfo {
	public String name;
	public Shape shape;
	public String[] assetLocation;
	public Attribute[] materialAttributes;

	public void setAssetLocation(String... assetLocation) {
		this.assetLocation = assetLocation;
	}

	public static StructureViewInfo[] standardConfiguration()
	{
		StructureViewInfo[] store = new StructureViewInfo[StructureId.values().length];

		StructureViewInfo tree = store[StructureId.Tree.ordinal()] = new StructureViewInfo();
		tree.name = "Tree";
		tree.shape = new Box(1f, 7.5f);
		tree.setAssetLocation("Models/Foliage/Tree.g3db","Models/Mineables/WoodLog.g3db","Models/Mineables/WoodCut.g3db");
		tree.materialAttributes = new Attribute[]{
			new IntAttribute(IntAttribute.CullFace, 0),
			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
			new FloatAttribute(FloatAttribute.AlphaTest, 0.5f)
		};

		StructureViewInfo keep = store[StructureId.Keep.ordinal()] = new StructureViewInfo();
		keep.name = "Keep";
		keep.shape = new Box(10,18);
		keep.setAssetLocation("Models/Structures/Keep/Keep.g3db");

		StructureViewInfo granary = store[StructureId.Granary.ordinal()] = new StructureViewInfo();
		granary.name = "Granary";
		granary.shape = new Box(10,8);
		granary.setAssetLocation("Models/Structures/Granary.g3db");

		StructureViewInfo house = store[StructureId.House.ordinal()] = new StructureViewInfo();
		house.name = "House";
		house.shape = new Box(10,9);
		house.setAssetLocation("Models/Structures/stonehouse.g3db");

		StructureViewInfo farm = store[StructureId.Farm.ordinal()] = new StructureViewInfo();
		farm.name = "Farm";
		farm.shape = new Box(10,14);
		farm.setAssetLocation("Models/Structures/Farm.g3db","Models/Mineables/Basket.g3db");



		StructureViewInfo church = store[StructureId.Church.ordinal()] = new StructureViewInfo();
		church.name = "Church";
		float churchWidth = 9.5f;
		float churchHeight = 10f;
		float churchDepth = 10.2f;
		church.shape = new Box( churchWidth, churchHeight, churchDepth, churchWidth*0.05f, churchHeight, -churchDepth*0.75f);
		church.setAssetLocation("Models/Structures/Church.g3db");
		return store;
	}
}
