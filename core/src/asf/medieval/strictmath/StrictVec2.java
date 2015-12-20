package asf.medieval.strictmath;


/**
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictVec2 {
	public final static StrictVec2 X = new StrictVec2("1", "0");
	public final static StrictVec2 Y = new StrictVec2("0", "1");
	public final static StrictVec2 Zero = new StrictVec2("0", "0");

	private final static StrictPoint TEMP_POINT = new StrictPoint();

	public final StrictPoint x;
	public final StrictPoint y;

	/**
	 * Constructs a new vector at (0,0)
	 */
	public StrictVec2() {
		x = new StrictPoint();
		y = new StrictPoint();
	}

	/**
	 * Constructs a vector with the given components
	 *
	 * @param x The x-component
	 * @param y The y-component
	 */
	public StrictVec2(StrictPoint x, StrictPoint y) {
		this.x = new StrictPoint(x);
		this.y = new StrictPoint(y);
	}

	public StrictVec2(String x, String y) {
		this.x = new StrictPoint(x);
		this.y = new StrictPoint(y);
	}

	public StrictVec2(StrictVec2 v) {
		this.x = new StrictPoint(v.x);
		this.y = new StrictPoint(v.y);
	}

	public StrictVec2 cpy() {
		return new StrictVec2(this);
	}

	public StrictVec2 set(StrictVec2 v) {
		x.val = v.x.val;
		y.val = v.y.val;
		return this;
	}

	public StrictVec2 set(String x, String y) {
		this.x.set(x);
		this.y.set(y);
		return this;
	}

	public StrictVec2 add(StrictVec2 v) {
		x.val += v.x.val;
		y.val += v.y.val;
		return this;
	}

	public StrictVec2 add(StrictPoint x, StrictPoint y) {
		this.x.val += x.val;
		this.y.val += y.val;
		return this;
	}

	public StrictVec2 sub(StrictVec2 v) {
		x.val -= v.x.val;
		y.val -= v.y.val;
		return this;
	}

	public StrictVec2 sub(StrictPoint x, StrictPoint y) {
		this.x.val -= x.val;
		this.y.val -= y.val;
		return this;
	}

	public StrictVec2 scl(StrictPoint scalar) {
		x.val *= scalar.val;
		y.val *= scalar.val;
		return this;
	}

	public StrictVec2 scl(String scalar) {
		TEMP_POINT.set(scalar);
		x.val *= TEMP_POINT.val;
		y.val *= TEMP_POINT.val;
		return this;
	}

	public StrictVec2 scl(StrictPoint x, StrictPoint y) {
		this.x.val *= x.val;
		this.y.val *= y.val;
		return this;
	}

	public StrictVec2 scl(String x, String y) {
		this.x.val *= TEMP_POINT.set(x).val;
		this.y.val *= TEMP_POINT.set(y).val;
		return this;
	}

	public StrictVec2 scl(StrictVec2 v) {
		this.x.val *= v.x.val;
		this.y.val *= v.y.val;
		return this;
	}

	public StrictVec2 div(StrictPoint scalar) {
		x.val /= scalar.val;
		y.val /= scalar.val;
		return this;
	}

	public StrictVec2 div(StrictPoint x, StrictPoint y) {
		this.x.val /= x.val;
		this.y.val /= y.val;
		return this;
	}

	public StrictVec2 div(StrictVec2 v) {
		this.x.val /= v.x.val;
		this.y.val /= v.y.val;
		return this;
	}


	public StrictVec2 nor() {
		/*
		StrictPoint len = len(TEMP_POINT);

		if (len.val != 0) {
			x.val /= len.val;
			y.val /= len.val;
		}
		*/

		final float len2 = this.len2(TEMP_POINT).val;
		if (len2 == 0f || len2 == 1f) return this;
		TEMP_POINT.val = 1f / (float) StrictMath.sqrt(len2);

		scl(TEMP_POINT);


		return this;
	}

	public StrictVec2 mulAdd(StrictVec2 vec, StrictPoint scalar) {
		this.x.val += vec.x.val * scalar.val;
		this.y.val += vec.y.val * scalar.val;
		return this;
	}

	public StrictVec2 mulAdd(StrictVec2 vec, StrictVec2 mulVec) {
		this.x.val += vec.x.val * mulVec.x.val;
		this.y.val += vec.y.val * mulVec.y.val;
		return this;
	}

	public StrictVec2 negate() {
		this.x.val *= -1f;
		this.y.val *= -1f;
		return this;
	}


	public StrictVec2 limit(StrictPoint limit) {
		final float limit2 = limit.val * limit.val;
		StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val > limit2) {
			float sqrt = (float) StrictMath.sqrt(limit2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
		}
		return this;
	}

	public StrictVec2 limit2(StrictPoint limit2) {
		StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val > limit2.val) {
			float sqrt = (float) StrictMath.sqrt(limit2.val / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
		}
		return this;
	}

	public StrictVec2 truncate(StrictPoint limit) {
		StrictPoint len2 = len2(TEMP_POINT);
		StrictPoint limit2 = new StrictPoint(limit).sqr();

		if (len2.val > limit2.val) {
			nor().scl(limit);
		}

		return this;
	}

	public StrictVec2 clamp(StrictPoint min, StrictPoint max) {
		final StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val == 0f) return this;
		float max2 = max.val * max.val;
		if (len2.val > max2) {
			final float sqrt = (float) StrictMath.sqrt(max2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			return this;
		}
		float min2 = min.val * min.val;
		if (len2.val < min2) {
			final float sqrt = (float) StrictMath.sqrt(min2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			return this;
		}
		return this;
	}

	public StrictVec2 setLength(StrictPoint len) {
		final float len2 = len.val * len.val;

		StrictPoint oldLen2 = len2(TEMP_POINT);
		if (oldLen2.val == 0 || oldLen2.val == len2) {
			return this;
		} else {
			float sqrt = (float) StrictMath.sqrt(len2 / oldLen2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			return this;
		}
	}

	public StrictVec2 setLength2(StrictPoint len2) {
		StrictPoint oldLen2 = len2(TEMP_POINT);
		if (oldLen2.val == 0 || oldLen2.val == len2.val) {
			return this;
		} else {
			float sqrt = (float) StrictMath.sqrt(len2.val / oldLen2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			return this;
		}
	}

	public StrictPoint dot(StrictVec2 v, StrictPoint store) {
		store.val = x.val * v.x.val + y.val * v.y.val;
		return store;
	}

	public StrictPoint dot(StrictPoint ox, StrictPoint oy, StrictPoint store) {
		store.val = x.val * ox.val + y.val * oy.val;
		return store;
	}

	public StrictPoint len(StrictPoint store) {
		store.val = (float) StrictMath.sqrt(x.val * x.val + y.val * y.val);
		return store;
	}

	public StrictPoint len2(StrictPoint store) {
		store.val = x.val * x.val + y.val * y.val;
		return store;
	}

	public StrictPoint dst(StrictVec2 v, StrictPoint store) {
		final float x_d = v.x.val - x.val;
		final float y_d = v.y.val - y.val;
		store.val = (float) StrictMath.sqrt(x_d * x_d + y_d * y_d);
		return store;
	}

	public StrictPoint dst(StrictPoint x, StrictPoint y, StrictPoint store) {
		final float x_d = x.val - this.x.val;
		final float y_d = y.val - this.y.val;
		store.val = (float) StrictMath.sqrt(x_d * x_d + y_d * y_d);
		return store;
	}

	public StrictPoint dst2(StrictVec2 v, StrictPoint store) {
		final float x_d = v.x.val - x.val;
		final float y_d = v.y.val - y.val;
		store.val = x_d * x_d + y_d * y_d;
		return store;
	}

	public StrictPoint dst2(StrictPoint x, StrictPoint y, StrictPoint store) {
		final float x_d = x.val - this.x.val;
		final float y_d = y.val - this.y.val;
		store.val = x_d * x_d + y_d * y_d;
		return store;
	}

	@Override
	public String toString() {
		final String format = "%.2f";
		return "(" + String.format(format, x.val) + ", " + String.format(format, y.val) + ")";
	}

	public StrictPoint crs(StrictVec2 v, StrictPoint store) {
		store.val = this.x.val * v.y.val - this.y.val * v.x.val;
		return store;
	}

	public StrictPoint crs(StrictPoint x, StrictPoint y, StrictPoint store) {
		store.val = this.x.val * y.val - this.y.val * x.val;
		return store;
	}

	/**
	 * @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis (typically
	 * counter-clockwise) and between 0 and 360.
	 */
	public StrictPoint angle(StrictPoint store) {
		store.set(y).atan2(x).radDeg();
		if (store.val < 0) store.val += 360;
		return store;
	}

	/**
	 * @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
	 * (typically counter-clockwise.) between -180 and +180
	 */
	public StrictPoint angle(StrictVec2 reference, StrictPoint store) {
		StrictPoint crs = crs(reference, store);
		StrictPoint dot = dot(reference, TEMP_POINT);
		crs.atan2(dot).radDeg();
		return store;
	}

	/**
	 * @return the angle in radians of this vector (point) relative to the x-axis. Angles are towards the positive y-axis.
	 * (typically counter-clockwise)
	 */
	public StrictPoint angleRad(StrictPoint store) {
		store.set(y).atan2(x);
		return store;
	}

	/**
	 * @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
	 * (typically counter-clockwise.)
	 */
	public StrictPoint angleRad(StrictVec2 reference, StrictPoint store) {
		StrictPoint crs = crs(reference, store);
		StrictPoint dot = dot(reference, TEMP_POINT);
		crs.atan2(dot);
		return store;
	}

	/**
	 * Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 *
	 * @param degrees The angle in degrees to set.
	 */
	public StrictVec2 setAngle(StrictPoint degrees) {
		x.val = len(TEMP_POINT).val;
		y.val = 0;
		TEMP_POINT.set(degrees).radDeg();
		return rotateRad(TEMP_POINT);
	}

	public StrictVec2 setAngleRad(StrictPoint radians, StrictPoint len){
		x.val = (float)StrictMath.cos(radians.val) * len.val;
		y.val = (float)StrictMath.sin(radians.val) * len.val;
		return this;
	}

	public StrictVec2 setAngleRad(StrictPoint radians, String len){
		float lenFloat = TEMP_POINT.set(len).val;
		x.val = (float)StrictMath.cos(radians.val) * lenFloat;
		y.val = (float)StrictMath.sin(radians.val) * lenFloat;
		return this;
	}

	/**
	 * Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 *
	 * @param radians The angle in radians to set.
	 */
	public StrictVec2 setAngleRad(StrictPoint radians) {
		float len = len(TEMP_POINT).val;

		x.val = (float)StrictMath.cos(radians.val) * len;
		y.val = (float)StrictMath.sin(radians.val) * len;

		return this;
	}

	/**
	 * Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 *
	 * @param degrees the angle in degrees
	 */
	public StrictVec2 rotate(StrictPoint degrees) {
		TEMP_POINT.set(degrees).radDeg();
		return rotateRad(TEMP_POINT);
	}

	/**
	 * Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 *
	 * @param radians the angle in radians
	 */
	public StrictVec2 rotateRad(StrictPoint radians) {
		float cos = (float) StrictMath.cos(radians.val);
		float sin = (float) StrictMath.sin(radians.val);

		float newX = this.x.val * cos - this.y.val * sin;
		float newY = this.x.val * sin + this.y.val * cos;

		this.x.val = newX;
		this.y.val = newY;

		return this;
	}

	/**
	 * Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise.
	 */
	public StrictVec2 rotate90(int dir) {
		float x = this.x.val;
		if (dir >= 0) {
			this.x.val = -y.val;
			y.val = x;
		} else {
			this.x.val = y.val;
			y.val = -x;
		}
		return this;
	}

	public StrictVec2 lerp(StrictVec2 target, StrictPoint alpha) {
		final float invAlpha = 1.0f - alpha.val;
		this.x.val = (x.val * invAlpha) + (target.x.val * alpha.val);
		this.y.val = (y.val * invAlpha) + (target.y.val * alpha.val);
		return this;
	}


	public boolean epsilonEquals(StrictVec2 other, StrictPoint epsilon) {
		if (other == null) return false;
		if (StrictMath.abs(other.x.val - x.val) > epsilon.val) return false;
		if (StrictMath.abs(other.y.val - y.val) > epsilon.val) return false;
		return true;
	}

	/**
	 * Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
	 *
	 * @return whether the vectors are the same.
	 */
	public boolean epsilonEquals(StrictPoint x, StrictPoint y, StrictPoint epsilon) {
		if (StrictMath.abs(x.val - this.x.val) > epsilon.val) return false;
		if (StrictMath.abs(y.val - this.y.val) > epsilon.val) return false;
		return true;
	}

	public boolean isUnit() {
		return isUnit(StrictPoint.MIN_ROUNDING_ERROR);
	}

	public boolean isUnit(final StrictPoint margin) {
		return StrictMath.abs(len2(TEMP_POINT).val - 1f) < margin.val;
	}

	public boolean isZero() {
		return x.val == 0 && y.val == 0;
	}

	public boolean isZero(final StrictPoint margin) {
		return len2(TEMP_POINT).val < margin.val;
	}

	public boolean isOnLine(StrictVec2 other) {
		TEMP_POINT.val = x.val * other.y.val - y.val * other.x.val;
		return TEMP_POINT.isZero();
	}

	public boolean isOnLine(StrictVec2 other, StrictPoint epsilon) {
		TEMP_POINT.val = x.val * other.y.val - y.val * other.x.val;
		return TEMP_POINT.isZero(epsilon);
	}

	public boolean isCollinear(StrictVec2 other) {
		return isOnLine(other) && dot(other, TEMP_POINT).val > 0f;
	}

	public boolean isCollinear(StrictVec2 other, StrictPoint epsilon) {
		return isOnLine(other, epsilon) && dot(other, TEMP_POINT).val > 0f;
	}

	public boolean isCollinearOpposite(StrictVec2 other) {
		return isOnLine(other) && dot(other, TEMP_POINT).val < 0f;
	}

	public boolean isCollinearOpposite(StrictVec2 other, StrictPoint epsilon) {
		return isOnLine(other, epsilon) && dot(other, TEMP_POINT).val < 0f;
	}

	public boolean isPerpendicular(StrictVec2 vector) {
		return dot(vector, TEMP_POINT).isZero();
	}

	public boolean isPerpendicular(StrictVec2 vector, StrictPoint epsilon) {
		return dot(vector, TEMP_POINT).isZero(epsilon);
	}

	public boolean hasSameDirection(StrictVec2 vector) {
		return dot(vector, TEMP_POINT).val > 0;
	}

	public boolean hasOppositeDirection(StrictVec2 vector) {
		return dot(vector, TEMP_POINT).val < 0;
	}

	public StrictVec2 setZero() {
		this.x.val = 0;
		this.y.val = 0;
		return this;
	}
}
