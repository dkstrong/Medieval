package asf.medieval.model.math;

import asf.medieval.utility.UtStrictMath;


/**
 *
 * Based off the libgdx vector2 class, this one enforces
 * strict floating point math so that the game scenario
 * can be determnisitic across computers
 *
 * TODO: StrictVec2 might not serialize well as the serializer probably wont be strictfp
 *
 * TODO: I will probably want to use BigDecimal instead..
 *
 *
 * Created by daniel on 12/5/15.
 */
public strictfp class StrictVec2 {

	public final static StrictVec2 X = new StrictVec2(1, 0);
	public final static StrictVec2 Y = new StrictVec2(0, 1);
	public final static StrictVec2 Zero = new StrictVec2(0, 0);

	/** the x-component of this vector **/
	public float x;
	/** the y-component of this vector **/
	public float y;

	/** Constructs a new vector at (0,0) */
	public StrictVec2() {
	}

	/** Constructs a vector with the given components
	 * @param x The x-component
	 * @param y The y-component */
	public StrictVec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Constructs a vector from the given vector
	 * @param v The vector */
	public StrictVec2(StrictVec2 v) {
		set(v);
	}

	public StrictVec2 cpy () {
		return new StrictVec2(this);
	}

	public static float len (float x, float y) {
		return (float)StrictMath.sqrt(x * x + y * y);
	}

	public float len () {
		return (float)StrictMath.sqrt(x * x + y * y);
	}

	public static float len2 (float x, float y) {
		return x * x + y * y;
	}

	public float len2 () {
		return x * x + y * y;
	}

	public StrictVec2 set (StrictVec2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public StrictVec2 set (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public StrictVec2 sub (StrictVec2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public StrictVec2 sub (float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public StrictVec2 nor () {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	public StrictVec2 add (StrictVec2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/** Adds the given components to this vector
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining */
	public StrictVec2 add (float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static float dot (float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	public float dot (StrictVec2 v) {
		return x * v.x + y * v.y;
	}

	public float dot (float ox, float oy) {
		return x * ox + y * oy;
	}

	public StrictVec2 scl (float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/** Multiplies this vector by a scalar
	 * @return This vector for chaining */
	public StrictVec2 scl (float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	public StrictVec2 scl (StrictVec2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	public StrictVec2 mulAdd (StrictVec2 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		return this;
	}

	public StrictVec2 mulAdd (StrictVec2 vec, StrictVec2 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		return this;
	}

	public static float dst (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float dst (StrictVec2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector */
	public float dst (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst2 (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	public float dst2 (StrictVec2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the squared distance between this and the other vector */
	public float dst2 (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	public StrictVec2 limit (float limit) {
		return limit2(limit * limit);
	}

	public StrictVec2 limit2 (float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			return scl((float)Math.sqrt(limit2 / len2));
		}
		return this;
	}

	public StrictVec2 clamp (float min, float max) {
		final float len2 = len2();
		if (len2 == 0f) return this;
		float max2 = max * max;
		if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
		return this;
	}

	public StrictVec2 setLength (float len) {
		return setLength2(len * len);
	}

	public StrictVec2 setLength2 (float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
	}

	public String toString () {
		return "(" + x + "," + y + ")";
	}

	/** Calculates the 2D cross product between this and the given vector.
	 * @param v the other vector
	 * @return the cross product */
	public float crs (StrictVec2 v) {
		return this.x * v.y - this.y * v.x;
	}

	/** Calculates the 2D cross product between this and the given vector.
	 * @param x the x-coordinate of the other vector
	 * @param y the y-coordinate of the other vector
	 * @return the cross product */
	public float crs (float x, float y) {
		return this.x * y - this.y * x;
	}

	/** @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis (typically
	 *         counter-clockwise) and between 0 and 360. */
	public float angle () {
		float angle = (float)Math.atan2(y, x) * UtStrictMath.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
	 *         (typically counter-clockwise.) between -180 and +180 */
	public float angle (StrictVec2 reference) {
		return (float)Math.atan2(crs(reference), dot(reference)) * UtStrictMath.radiansToDegrees;
	}

	/** @return the angle in radians of this vector (point) relative to the x-axis. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise) */
	public float angleRad () {
		return (float)Math.atan2(y, x);
	}

	/** @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise.) */
	public float angleRad (StrictVec2 reference) {
		return (float)Math.atan2(crs(reference), dot(reference));
	}

	/** Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param degrees The angle in degrees to set. */
	public StrictVec2 setAngle (float degrees) {
		return setAngleRad(degrees * UtStrictMath.degreesToRadians);
	}

	/** Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param radians The angle in radians to set. */
	public StrictVec2 setAngleRad (float radians) {
		this.set(len(), 0f);
		this.rotateRad(radians);

		return this;
	}

	/** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param degrees the angle in degrees */
	public StrictVec2 rotate (float degrees) {
		return rotateRad(degrees * UtStrictMath.degreesToRadians);
	}

	/** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param radians the angle in radians */
	public StrictVec2 rotateRad (float radians) {
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	/** Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
	public StrictVec2 rotate90 (int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	public StrictVec2 lerp (StrictVec2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StrictVec2 that = (StrictVec2) o;

		if (Float.compare(that.x, x) != 0) return false;
		if (Float.compare(that.y, y) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		return result;
	}


	//	@Override
//	public int hashCode () {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + NumberUtils.floatToIntBits(x);
//		result = prime * result + NumberUtils.floatToIntBits(y);
//		return result;
//	}
//
//	public boolean equals (Object obj) {
//		if (this == obj) return true;
//		if (obj == null) return false;
//		if (getClass() != obj.getClass()) return false;
//		StrictVec2 other = (StrictVec2)obj;
//		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
//		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
//		return true;
//	}

	public boolean epsilonEquals (StrictVec2 other, float epsilon) {
		if (other == null) return false;
		if (Math.abs(other.x - x) > epsilon) return false;
		if (Math.abs(other.y - y) > epsilon) return false;
		return true;
	}

	/** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
	 * @return whether the vectors are the same. */
	public boolean epsilonEquals (float x, float y, float epsilon) {
		if (Math.abs(x - this.x) > epsilon) return false;
		if (Math.abs(y - this.y) > epsilon) return false;
		return true;
	}

	public boolean isUnit () {
		return isUnit(0.000000001f);
	}

	public boolean isUnit (final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	public boolean isZero () {
		return x == 0 && y == 0;
	}

	public boolean isZero (final float margin) {
		return len2() < margin;
	}

	public boolean isOnLine (StrictVec2 other) {
		return UtStrictMath.isZero(x * other.y - y * other.x);
	}

	public boolean isOnLine (StrictVec2 other, float epsilon) {
		return UtStrictMath.isZero(x * other.y - y * other.x, epsilon);
	}

	public boolean isCollinear (StrictVec2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	public boolean isCollinear (StrictVec2 other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	public boolean isCollinearOpposite (StrictVec2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	public boolean isCollinearOpposite (StrictVec2 other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	public boolean isPerpendicular (StrictVec2 vector) {
		return UtStrictMath.isZero(dot(vector));
	}

	public boolean isPerpendicular (StrictVec2 vector, float epsilon) {
		return UtStrictMath.isZero(dot(vector), epsilon);
	}

	public boolean hasSameDirection (StrictVec2 vector) {
		return dot(vector) > 0;
	}

	public boolean hasOppositeDirection (StrictVec2 vector) {
		return dot(vector) < 0;
	}

	public StrictVec2 setZero () {
		this.x = 0;
		this.y = 0;
		return this;
	}
}
