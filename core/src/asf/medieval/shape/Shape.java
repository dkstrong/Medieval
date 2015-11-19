package asf.medieval.shape;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by danny on 10/20/14.
 */
public interface Shape {
	/**
	 * @param transform the transform matrix of the object that this shape represents
	 * @param cam
	 * @return
	 */
	public boolean isVisible(Matrix4 transform, Camera cam);

	/**
	 * @return -1 on no intersection, or when there is an intersection:
	 * the squared distance between the center of this
	 * object and the point on the ray closest to this object when there is intersection.
	 */
	public float intersects(Matrix4 transform, Ray ray);

	/**
	 * @param translation the vec of the object that this shape represents
	 * @param cam
	 * @return
	 */
	public boolean isVisible(Vector3 translation, Camera cam);


	public Vector3 getCenter();

	public Vector3 getDimensions();
}
