package asf.medieval.utility;

import com.badlogic.gdx.math.Vector3;

/**
 *
 * https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Plane.java
 * Created by Danny on 11/13/2015.
 */
public class JmePlane {


	public static enum Side {
		None,
		Positive,
		Negative
	}

	/**
	 * Vector normal to the plane.
	 */
	public final Vector3 normal = new Vector3();

	/**
	 * Constant of the plane. See formula in class definition.
	 */
	public float constant;


	public Vector3 getClosestPoint(Vector3 point, Vector3 store){
//        float t = constant - normal.dot(point);
//        return store.set(normal).multLocal(t).addLocal(point);
		float t = (constant - normal.dot(point)) / normal.dot(normal);
		return store.set(normal).scl(t).add(point);
	}

	public Vector3 getClosestPoint(Vector3 point){
		return getClosestPoint(point, new Vector3());
	}

	public Vector3 reflect(Vector3 point, Vector3 store){
		if (store == null)
			store = new Vector3();

		float d = pseudoDistance(point);
		store.set(normal).scl(-1f).scl(d * 2f);
		store.add(point);
		return store;
	}

	/**
	 * <code>pseudoDistance</code> calculates the distance from this plane to
	 * a provided point. If the point is on the negative side of the plane the
	 * distance returned is negative, otherwise it is positive. If the point is
	 * on the plane, it is zero.
	 *
	 * @param point
	 *            the point to check.
	 * @return the signed distance from the plane to a point.
	 */
	public float pseudoDistance(Vector3 point) {
		return normal.dot(point) - constant;
	}

	/**
	 * <code>whichSide</code> returns the side at which a point lies on the
	 * plane. The positive values returned are: NEGATIVE_SIDE, POSITIVE_SIDE and
	 * NO_SIDE.
	 *
	 * @param point
	 *            the point to check.
	 * @return the side at which the point lies.
	 */
	public Side whichSide(Vector3 point) {
		float dis = pseudoDistance(point);
		if (dis < 0) {
			return Side.Negative;
		} else if (dis > 0) {
			return Side.Positive;
		} else {
			return Side.None;
		}
	}
	/** A "close to zero" float epsilon value for use*/
	public static final float FLT_EPSILON = 1.1920928955078125E-7f;

	public boolean isOnPlane(Vector3 point){
		float dist = pseudoDistance(point);
		if (dist < FLT_EPSILON && dist > -FLT_EPSILON)
			return true;
		else
			return false;
	}

}
