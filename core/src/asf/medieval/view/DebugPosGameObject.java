package asf.medieval.view;

import asf.medieval.utility.ModelFactory;
import asf.medieval.utility.UtDebugPrint;
import asf.medieval.view.shape.Box;
import asf.medieval.view.shape.Disc;
import asf.medieval.view.shape.Shape;
import asf.medieval.view.shape.Sphere;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class DebugPosGameObject implements GameObject{

	private final MedievalWorld world;

	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	private float t = 4;

	public DebugPosGameObject(MedievalWorld world, Vector3 pos) {
		this.world = world;

		Model model = ModelFactory.cylinder(2, 10, Color.YELLOW);
		modelInstance = new ModelInstance(model);


		translation.set(pos);

		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
	}

	public DebugPosGameObject(MedievalWorld world, Vector3 pos1, Vector3 pos2) {
		this.world = world;

		Model model = ModelFactory.rect(pos1, pos2, Color.GREEN);
		modelInstance = new ModelInstance(model);

		translation.y += 0.1f;
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
	}

	public DebugPosGameObject(MedievalWorld world, Vector3 pos1, Vector3 pos2, Vector3 pos3, Vector3 pos4) {
		this.world = world;

		//System.out.println("Draw rect:");
		//UtDebugPrint.print(pos1);
		//UtDebugPrint.print(pos2);
		//UtDebugPrint.print(pos3);
		//UtDebugPrint.print(pos4);

		Model model = ModelFactory.rect(pos1, pos2, pos3,pos4,new Color(0,1,0,0.25f));
		modelInstance = new ModelInstance(model);

		translation.y += 0.025f;
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
	}

	public DebugPosGameObject(MedievalWorld world, Vector3 pos, Shape shape) {
		this.world = world;


		if(shape instanceof Box)
		{
			Box box = (Box) shape;
			Vector3 dim = box.getDimensions();
			Model model = ModelFactory.box(dim.x, dim.y,dim.z, Color.BLUE);
			modelInstance = new ModelInstance(model);
		}
		else if(shape instanceof Sphere)
		{
			Sphere sphere = (Sphere) shape;
			Model model = ModelFactory.sphere(sphere.getRadius(), Color.BLUE);
			modelInstance = new ModelInstance(model);

		}
		else if(shape instanceof Disc)
		{
			Disc disc = (Disc) shape;
			Model model = ModelFactory.cylinder(disc.getRadius(), .1f, Color.BLUE);
			modelInstance = new ModelInstance(model);
		}
		else
		{
			throw new UnsupportedOperationException(String.valueOf(shape));
		}

		translation.set(pos).add(shape.getCenter());
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
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
		world.modelBatch.render(modelInstance, world.environment);
	}
}
