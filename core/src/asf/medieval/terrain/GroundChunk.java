
package asf.medieval.terrain;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GroundChunk extends ModelInstance {
	private static final BoundingBox box = new BoundingBox();
	public final Vector3 position = new Vector3();
	public final Vector3 dimensions = new Vector3();
	public float radius;

	public GroundChunk (Model model) {
		super(model);
		calculateTransforms();
		calculateBoundingBox(box);
		box.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
		box.getCenter(position);

		//Lists.groundChunks.add(this);
	}

	public boolean isVisible (PerspectiveCamera cam) {
		return cam.frustum.sphereInFrustum(position, radius);
	}

}
