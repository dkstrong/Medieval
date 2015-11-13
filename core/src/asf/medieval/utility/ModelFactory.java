package asf.medieval.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class ModelFactory {
	public static Model box(float x, float y, float z, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(color));
		Model model = modelBuilder.createBox(x, y, z,mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;

	}

	public static Model cylinder(float radius, float height, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(color));
		Model model = modelBuilder.createCylinder(radius, height, radius, 16, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;
	}

	public static Model sphere(float radius, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(color));
		Model model = modelBuilder.createSphere(radius*2f, radius*2f, radius*2f, 16, 16, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;
	}

	public static Model arrow(Vector3 from, Vector3 to, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(color));
		Model model = modelBuilder.createArrow(from, to, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;

	}

	public static Model rect(Vector3 pos1, Vector3 pos2, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		Material mat = new Material(ColorAttribute.createDiffuse(color));

		final float minX = Math.min(pos1.x, pos2.x);
		final float minY = Math.min(pos1.y, pos2.y);
		final float minZ = Math.min(pos1.z, pos2.z);
		final float maxX = Math.max(pos1.x, pos2.x);
		final float maxY = Math.max(pos1.y, pos2.y);
		final float maxZ = Math.max(pos1.z, pos2.z);

		Model model = modelBuilder.createRect(
			maxX, minY, minZ,
			minX, minY, minZ,
			minX, minY, maxZ,
			maxX, minY, maxZ,
			0,1,0,mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		//Model model = modelBuilder.createArrow(pos1, pos2, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;

	}


	public static Model rect(Vector3 pos1, Vector3 pos2, Vector3 pos3, Vector3 pos4, Color color){
		ModelBuilder modelBuilder = new ModelBuilder();
		BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Material mat = new Material(ColorAttribute.createDiffuse(color), blendingAttribute);


		Model model = modelBuilder.createRect(
			pos1.x, pos1.y, pos1.z,
			pos2.x, pos2.y, pos2.z,
			pos3.x, pos3.y, pos3.z,
			pos4.x, pos4.y, pos4.z,
			0,1,0,mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		//Model model = modelBuilder.createArrow(pos1, pos2, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		return model;

	}
}
