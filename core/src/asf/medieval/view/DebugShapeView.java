package asf.medieval.view;

import asf.medieval.utility.ModelFactory;
import asf.medieval.shape.Box;
import asf.medieval.shape.Disc;
import asf.medieval.shape.Shape;
import asf.medieval.shape.Sphere;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class DebugShapeView implements View {

	private final MedievalWorld world;

	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public float t = Float.NaN;

	public DebugShapeView(MedievalWorld world) {
		this.world = world;
	}

	public DebugShapeView point(Vector3 pos, float length, Color color) {
		Model model = ModelFactory.coordinate(length, color);
		modelInstance = new ModelInstance(model);
		translation.set(pos);
		return this;
	}

	public DebugShapeView arrow(Vector3 pos1, Vector3 pos2, Color color) {
		Model model = ModelFactory.arrow(pos1, pos2, color);
		modelInstance = new ModelInstance(model);
		translation.y += 0.025f;
		return this;
	}

	public DebugShapeView shape(Vector3 pos, Shape shape){
		if(shape instanceof Box)
		{
			Box box = (Box) shape;
			Vector3 dim = box.dimensions;
			Model model = ModelFactory.box(dim.x, dim.y,dim.z, Color.BLUE);
			modelInstance = new ModelInstance(model);
		}
		else if(shape instanceof Sphere)
		{
			Sphere sphere = (Sphere) shape;
			Model model = ModelFactory.sphere(sphere.radius, Color.BLUE);
			modelInstance = new ModelInstance(model);

		}
		else if(shape instanceof Disc)
		{
			Disc disc = (Disc) shape;
			Model model = ModelFactory.cylinder(disc.radius, .1f, Color.BLUE);
			modelInstance = new ModelInstance(model);
		}
		else
		{
			throw new UnsupportedOperationException(String.valueOf(shape));
		}

		translation.set(pos).add(shape.center);
		return this;
	}

	/**
	 * creates a rect by supplying the two diagonal corners of a rectangle
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public DebugShapeView rect(Vector3 pos1, Vector3 pos2) {
		Model model = ModelFactory.rect(pos1, pos2, Color.GREEN);
		modelInstance = new ModelInstance(model);
		translation.y += 0.1f;
		return this;
	}


	/**
	 * creates a rect by supplying each corner of the rectangle
	 * @param pos1
	 * @param pos2
	 * @param pos3
	 * @param pos4
	 * @return
	 */
	public DebugShapeView rect(Vector3 pos1, Vector3 pos2, Vector3 pos3, Vector3 pos4) {
		//System.out.println("Draw rect:");
		//UtDebugPrint.print(pos1);
		//UtDebugPrint.print(pos2);
		//UtDebugPrint.print(pos3);
		//UtDebugPrint.print(pos4);

		Model model = ModelFactory.rect(pos1, pos2, pos3,pos4,new Color(0,1,0,0.25f));
		modelInstance = new ModelInstance(model);
		translation.y += 0.025f;
		return this;
	}

	@Override
	public void update(float delta) {

		t -= delta;
		if(t<0){
			modelInstance.model.dispose();
			world.removeGameObject(this);
		}



	}

	@Override
	public void render(float delta) {

		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);

		//world.modelBatch.render(modelInstance, world.environment);
		world.modelBatch.render(modelInstance);
	}
}
