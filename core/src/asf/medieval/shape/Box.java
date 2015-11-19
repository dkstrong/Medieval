package asf.medieval.shape;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by danny on 10/20/14.
 */
public class Box extends Shape {

	public Box(final float radius, final float height)
	{
		dimensions.set(radius*2f, height, radius*2f);

		center.set(0,height/2f,0);

		this.radius = radius;
	}

	public Box(final float xExtent, final float yExtent, final float zExtent, final float xCenter, final float yCenter, final float zCenter)
	{
		dimensions.set(xExtent*2f, yExtent*2, zExtent*2);

		center.x = xCenter;
		center.y = yCenter;
		center.z = zCenter;

		radius = xExtent > zExtent ? xExtent : zExtent;
	}

	public Box(Vector3 min, Vector3 max) {
		dimensions.set(max).sub(min);

		center.x = (max.x + min.x) / 2f;
		center.y = (max.y + min.y) / 2f;
		center.z = (max.z + min.z) / 2f;

		final float xExtent = dimensions.x/2f;
		final float zExtent = dimensions.z/2f;

		radius = xExtent > zExtent ? xExtent : zExtent;
	}

	/**
	 * creates the box dimensions automatically from the bounding box of this model instnace
	 * <p/>
	 * be warned that this can create weird results for animated models if their initial pose is weird.
	 *
	 * @param modelInstance
	 */
	public Box(ModelInstance modelInstance) {
		BoundingBox bb = new BoundingBox();
		modelInstance.calculateBoundingBox(bb);
		bb.getCenter(center);
		bb.getDimensions(dimensions);

		final float xExtent = dimensions.x/2f;
		final float zExtent = dimensions.z/2f;

		radius = xExtent > zExtent ? xExtent : zExtent;
	}

	public Box(Box box) {
		dimensions.set(box.dimensions);
		center.set(box.center);
		radius = box.radius;
	}

	@Override
	public boolean isVisible(Matrix4 transform, Camera cam) {
		return cam.frustum.boundsInFrustum(transform.getTranslation(position).add(center), dimensions);
	}

	@Override
	public boolean isVisible(Vector3 translation, Camera cam) {
		return cam.frustum.boundsInFrustum(position.set(translation).add(center), dimensions);
	}

	@Override
	public float intersects(Matrix4 transform, Ray ray) {
		transform.getTranslation(position).add(center);
		if (Intersector.intersectRayBoundsFast(ray, position, dimensions)) {
			final float len = ray.direction.dot(position.x - ray.origin.x, position.y - ray.origin.y, position.z - ray.origin.z);
			return position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
		}
		return -1f;
	}

	public Box cpy() {
		return new Box(this);
	}
}
