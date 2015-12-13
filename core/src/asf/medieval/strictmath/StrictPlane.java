package asf.medieval.strictmath;

/**
 * Based of Plane.java from jMonkeyEngine3
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictPlane {
	public strictfp static enum Side {
		None,
		Positive,
		Negative
	}

	/**
	 * Vector normal to the plane.
	 */
	public final StrictVec3 normal = new StrictVec3();

	/**
	 * Constant of the plane. See formula in class definition.
	 */
	public final StrictPoint constant = new StrictPoint();

	public final static StrictPoint TEMP_POINT = new StrictPoint();

	/** A "close to zero" float epsilon value for use*/
	public static final float FLT_EPSILON = 1.1920928955078125E-7f;

	public StrictVec3 getClosestPoint(StrictVec3 point, StrictVec3 store){
//        float t = constant - normal.dot(point);
//        return store.set(normal).multLocal(t).addLocal(point);

		float t = constant.val - normal.dot(point,TEMP_POINT).val;
		t /= normal.dot(normal, TEMP_POINT).val; // is this needed?

		store.set(normal);
		store.x.val*= t;
		store.y.val *=t;
		store.z.val*=t;
		store.add(point);
		return store;
	}

	public StrictVec3 reflect(StrictVec3 point, StrictVec3 store){
		StrictPoint d = pseudoDistance(point,TEMP_POINT);
		float scl = -d.val * 2f;
		store.set(normal);
		store.x.val *= scl;
		store.y.val *=scl;
		store.z.val*=scl;
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
	public StrictPoint pseudoDistance(StrictVec3 point, StrictPoint store) {
		return normal.dot(point, store).sub(constant);
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
	public Side whichSide(StrictVec3 point) {
		StrictPoint dis = pseudoDistance(point, TEMP_POINT);
		if (dis.val < 0) {
			return Side.Negative;
		} else if (dis.val > 0) {
			return Side.Positive;
		} else {
			return Side.None;
		}
	}


	public boolean isOnPlane(StrictVec3 point){
		StrictPoint dist = pseudoDistance(point, TEMP_POINT);
		if (dist.val < FLT_EPSILON && dist.val > -FLT_EPSILON)
			return true;
		else
			return false;
	}
}
