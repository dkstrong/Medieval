package asf.medieval.utility;

import com.badlogic.gdx.math.Interpolation;

/**
 * Based off of libgdx Vector2, but modified to use fixed point math instead.
 *
 * Note that all use of integers in arguments and return types are not regular integers, they are fixed point integers.
 *
 * Created by daniel on 12/9/15.
 */
public class FPVec2  {
	public final static FPVec2 X = new FPVec2("1", "0");
	public final static FPVec2 Y = new FPVec2("0", "1");
	public final static FPVec2 Zero = new FPVec2("0", "0");

	public int x;
	public int y;

	public FPVec2 () {
	}

	public FPVec2 (int fpX, int fpY) {
		this.x = fpX;
		this.y = fpY;
	}

	public FPVec2 (String x, String y) {
		this.x = FPMath.toFP(x);
		this.y = FPMath.toFP(y);
	}

	public FPVec2 (FPVec2 copy) {
		set(copy);
	}

	public FPVec2 cpy () {
		return new FPVec2(this);
	}

	public FPVec2 set (FPVec2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public FPVec2 set (int fpX, int fpY) {
		this.x = fpX;
		this.y = fpY;
		return this;
	}

	public static int len (int x, int y) {
		//return (float)Math.sqrt(x * x + y * y);
		return FPMath.sqrt(FPMath.mul(x,x) + FPMath.mul(y,y));
	}

	public int len () {
		return FPMath.sqrt(FPMath.mul(x,x) + FPMath.mul(y,y));
	}

	public static int len2 (int x, int y) {
		//return x * x + y * y;
		return FPMath.mul(x,x) + FPMath.mul(y,y);
	}

	public int len2 () {
		return FPMath.mul(x,x) + FPMath.mul(y,y);
	}

	public FPVec2 sub (FPVec2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public FPVec2 sub (int x, int y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public FPVec2 nor () {
		/*
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		*/
		int len = len();
		if (len != FPMath.ZERO) {
			x = FPMath.div(x,len);
			x = FPMath.div(y,len);
		}

		return this;
	}

	public FPVec2 add (FPVec2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public FPVec2 add (int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static int dot (int x1, int y1, int x2, int y2) {
		//return x1 * x2 + y1 * y2;
		return FPMath.mul(x1,x2) + FPMath.mul(y1,y2);
	}

	public int dot (FPVec2 v) {
		//return x * v.x + y * v.y;
		return FPMath.mul(x,v.x) + FPMath.mul(y,v.y);
	}

	public int dot (int ox, int oy) {
		//return x * ox + y * oy;
		return FPMath.mul(x,ox) + FPMath.mul(y,oy);
	}

	public FPVec2 scl (int scalar) {
//		x *= scalar;
//		y *= scalar;
		x = FPMath.mul(x,scalar);
		y = FPMath.mul(y,scalar);
		return this;
	}

	public FPVec2 scl (int scalarX, int scalarY) {
//		this.x *= x;
//		this.y *= y;
		x = FPMath.mul(x,scalarX);
		y = FPMath.mul(y,scalarY);
		return this;
	}

	public FPVec2 scl (FPVec2 v) {
//		this.x *= v.x;
//		this.y *= v.y;
		x = FPMath.mul(x,v.x);
		y = FPMath.mul(y,v.y);
		return this;
	}

	public FPVec2 mulAdd (FPVec2 vec, int scalar) {
//		this.x += vec.x * scalar;
//		this.y += vec.y * scalar;
		x += FPMath.mul(vec.x,scalar);
		y += FPMath.mul(vec.y,scalar);
		return this;
	}

	public FPVec2 mulAdd (FPVec2 vec, FPVec2 mulVec) {
//		this.x += vec.x * mulVec.x;
//		this.y += vec.y * mulVec.y;
		x += FPMath.mul(vec.x,mulVec.x);
		y += FPMath.mul(vec.y,mulVec.y);
		return this;
	}

	public static int dst (int x1, int y1, int x2, int y2) {
//		final float x_d = x2 - x1;
//		final float y_d = y2 - y1;
//		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
		final int x_d = x2 - x1;
		final int y_d = y2 - y1;
		return FPMath.sqrt(FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d));
	}

	public int dst (FPVec2 v) {
//		final float x_d = v.x - x;
//		final float y_d = v.y - y;
//		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
		final int x_d = v.x - x;
		final int y_d = v.y - y;
		return FPMath.sqrt(FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d));
	}

	public int dst (int x, int y) {
		final int x_d = x - this.x;
		final int y_d = y - this.y;
		return FPMath.sqrt(FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d));
	}

	public static int dst2 (int x1, int y1, int x2, int y2) {
		final int x_d = x2 - x1;
		final int y_d = y2 - y1;
		return FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d);
	}

	public int dst2 (FPVec2 v) {
		final int x_d = v.x - x;
		final int y_d = v.y - y;
		return FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d);
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the squared distance between this and the other vector */
	public int dst2 (int x, int y) {
		final int x_d = x - this.x;
		final int y_d = y - this.y;
		return FPMath.mul(x_d,x_d)+FPMath.mul(y_d,y_d);
	}

	public FPVec2 limit (int limit) {
		return limit2(FPMath.mul(limit,limit));
	}

	public FPVec2 limit2 (int limit2) {
		int len2 = len2();
		if (len2 > limit2) {
			//return scl((float)Math.sqrt(limit2 / len2));
			return scl(FPMath.sqrt(limit2/len2));
		}
		return this;
	}

	public FPVec2 clamp (int min, int max) {
		final int len2 = len2();
		if (len2 == FPMath.ZERO) return this;
		int max2 = FPMath.mul(max,max);
		if (len2 > max2) return scl(FPMath.sqrt(max2/len2));
		int min2 = FPMath.mul(min,min);
		if (len2 < min2) return scl(FPMath.sqrt(min2/len2));
		return this;
	}

	public FPVec2 setLength (int len) {
		return setLength2(FPMath.mul(len,len));
	}

	public FPVec2 setLength2 (int len2) {
		int oldLen2 = len2();
		return (oldLen2 == FPMath.ZERO || oldLen2 == len2) ? this : scl(FPMath.sqrt(len2/oldLen2));
	}



	/** Calculates the 2D cross product between this and the given vector.
	 * @param v the other vector
	 * @return the cross product */
	public int crs (FPVec2 v) {
		//return this.x * v.y - this.y * v.x;
		return FPMath.mul(x,v.y) - FPMath.mul(y,v.x);
	}

	/** Calculates the 2D cross product between this and the given vector.
	 * @param x the x-coordinate of the other vector
	 * @param y the y-coordinate of the other vector
	 * @return the cross product */
	public int crs (int x, int y) {
		//return this.x * y - this.y * x;
		return FPMath.mul(this.x,y) - FPMath.mul(this.y,x);
	}

	/** @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis (typically
	 *         counter-clockwise) and between 0 and 360. */
	public int angle () {
//		float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
//		if (angle < 0) angle += 360;
//		return angle;

		int angle = FPMath.radDeg(FPMath.atan2(y,x));
		if (angle < FPMath.ZERO) angle += FPMath.toFP(360);
		return angle;
	}

	/** @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
	 *         (typically counter-clockwise.) between -180 and +180 */
	public int angle (FPVec2 reference) {
		//return (float)Math.atan2(crs(reference), dot(reference)) * MathUtils.radiansToDegrees;
		return FPMath.radDeg(FPMath.atan2(crs(reference), dot(reference)));
	}

	/** @return the angle in radians of this vector (point) relative to the x-axis. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise) */
	public int angleRad () {
		return FPMath.atan2(y, x);
	}

	/** @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise.) */
	public int angleRad (FPVec2 reference) {
		//return (float)Math.atan2(crs(reference), dot(reference));
		return FPMath.atan2(crs(reference), dot(reference));
	}

	/** Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param degrees The angle in degrees to set. */
	public FPVec2 setAngle (int degrees) {
		return setAngleRad(FPMath.degRad(degrees));
	}

	/** Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param radians The angle in radians to set. */
	public FPVec2 setAngleRad (int radians) {
		this.set(len(), FPMath.ZERO);
		this.rotateRad(radians);

		return this;
	}

	/** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param degrees the angle in degrees */
	public FPVec2 rotate (int degrees) {
		return rotateRad(FPMath.degRad(degrees));
	}

	/** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param radians the angle in radians */
	public FPVec2 rotateRad (int radians) {
//		float cos = (float)Math.cos(radians);
//		float sin = (float)Math.sin(radians);
//
//		float newX = this.x * cos - this.y * sin;
//		float newY = this.x * sin + this.y * cos;
//
//		this.x = newX;
//		this.y = newY;

		int cos = FPMath.cos(radians);
		int sin = FPMath.sin(radians);

		int newX = FPMath.max(x,cos) - FPMath.max(y,sin);
		int newY = FPMath.max(x,sin) + FPMath.max(y,cos);

		this.x = newX;
		this.y = newY;

		return this;
	}

	/** Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
	public FPVec2 rotate90 (int dir) {
		int x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	public FPVec2 lerp (FPVec2 target, int alpha) {
		final int invAlpha = FPMath.ONE - alpha;
//		this.x = (x * invAlpha) + (target.x * alpha);
//		this.y = (y * invAlpha) + (target.y * alpha);
		this.x = FPMath.mul(x,invAlpha) + FPMath.mul(target.x,alpha);
		this.x = FPMath.mul(y,invAlpha) + FPMath.mul(target.y,alpha);
		return this;
	}

	public FPVec2 interpolate (FPVec2 target, int alpha, Interpolation interpolation) {
		// TODO: cant use libgdx interpolation because it uses floats, but
		// it would be cool to make a FP version of it..
		//return lerp(target, interpolation.apply(alpha));
		return lerp(target, alpha);
	}

	public boolean isUnit () {
		return isUnit(FPMath.EPSILON);
	}

	public boolean isUnit (final int margin) {
		return Math.abs(len2() - FPMath.ONE) < margin;
	}

	public boolean isZero () {
		return x == FPMath.ZERO && y == FPMath.ZERO;
	}

	public boolean isZero (final int margin) {
		return len2() < margin;
	}

	public boolean isOnLine (FPVec2 other) {
		//return MathUtils.isZero(x * other.y - y * other.x);
		return FPMath.isZero(FPMath.mul(x, other.y) - FPMath.mul(y, other.x));
	}

	public boolean isOnLine (FPVec2 other, int epsilon) {
		//return MathUtils.isZero(x * other.y - y * other.x, epsilon);
		return FPMath.isZero(FPMath.mul(x, other.y) - FPMath.mul(y, other.x), epsilon);
	}

	public boolean isCollinear (FPVec2 other, int epsilon) {
		return isOnLine(other, epsilon) && dot(other) > FPMath.ZERO;
	}

	public boolean isCollinear (FPVec2 other) {
		return isOnLine(other) && dot(other) > FPMath.ZERO;
	}

	public boolean isCollinearOpposite (FPVec2 other, int epsilon) {
		return isOnLine(other, epsilon) && dot(other) <= FPMath.ZERO;
	}

	public boolean isCollinearOpposite (FPVec2 other) {
		return isOnLine(other) && dot(other) <= FPMath.ZERO;
	}

	public boolean isPerpendicular (FPVec2 vector) {
		return FPMath.isZero(dot(vector));
	}

	public boolean isPerpendicular (FPVec2 vector, int epsilon) {
		return FPMath.abs(dot(vector)) <= epsilon;
	}

	public boolean hasSameDirection (FPVec2 vector) {
		return dot(vector) > FPMath.ZERO;
	}

	public boolean hasOppositeDirection (FPVec2 vector) {
		return dot(vector) < FPMath.ZERO;
	}

	public FPVec2 setZero () {
		this.x = FPMath.ZERO;
		this.y = FPMath.ZERO;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FPVec2 fpVec2 = (FPVec2) o;

		if (x != fpVec2.x) return false;
		if (y != fpVec2.y) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	@Override
	public String toString () {

		return "(" + FPMath.toString(x) + "," + FPMath.toString(y) + ")";
	}

	public FPVec2 fromString (String v) {
		int s = v.indexOf(',', 1);
		if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
			try {
				int fpX = FPMath.toFP(v.substring(1, s));
				int fpY = FPMath.toFP(v.substring(s + 1, v.length() - 1));
				return this.set(fpX, fpY);
			} catch (NumberFormatException ex) {
				throw new RuntimeException("Malformed FPVec2: " + v, ex);
			}
		}else{
			throw new RuntimeException("Malformed FPVec2: " + v);
		}

	}
}
