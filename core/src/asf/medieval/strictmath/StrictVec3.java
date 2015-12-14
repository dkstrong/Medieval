package asf.medieval.strictmath;


/**
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictVec3 {
	public final static StrictVec3 X = new StrictVec3("1", "0", "0");
	public final static StrictVec3 Y = new StrictVec3("0", "1", "0");
	public final static StrictVec3 Z = new StrictVec3("0", "0", "1");
	public final static StrictVec3 Zero = new StrictVec3("0", "0", "0");

	private final static StrictPoint TEMP_POINT = new StrictPoint();

	public final StrictPoint x;
	public final StrictPoint y;
	public final StrictPoint z;

	public StrictVec3() {
		x = new StrictPoint();
		y = new StrictPoint();
		z = new StrictPoint();
	}

	public StrictVec3(StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x = new StrictPoint(x);
		this.y = new StrictPoint(y);
		this.z = new StrictPoint(z);
	}

	public StrictVec3(String x, String y, String z) {
		this.x = new StrictPoint(x);
		this.y = new StrictPoint(y);
		this.z = new StrictPoint(z);
	}

	public StrictVec3(StrictVec3 v) {
		this.x = new StrictPoint(v.x);
		this.y = new StrictPoint(v.y);
		this.z = new StrictPoint(v.z);
	}

	public StrictVec3 cpy () {
		return new StrictVec3(this);
	}

	public StrictVec3 set (StrictVec3 v) {
		x.val = v.x.val;
		y.val = v.y.val;
		z.val = v.z.val;
		return this;
	}

	public StrictVec3 set (String x, String y, String z) {
		this.x.set(x);
		this.y.set(y);
		this.z.set(z);
		return this;
	}

	public StrictVec3 set (StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x.set(x);
		this.y.set(y);
		this.z.set(z);
		return this;
	}

	public StrictVec3 add (StrictVec3 v) {
		x.val += v.x.val;
		y.val += v.y.val;
		z.val += v.z.val;
		return this;
	}

	public StrictVec3 add (StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x.val += x.val;
		this.y.val += y.val;
		this.z.val += z.val;
		return this;
	}

	public StrictVec3 sub (StrictVec3 v) {
		x.val -= v.x.val;
		y.val -= v.y.val;
		z.val -= v.z.val;
		return this;
	}

	public StrictVec3 sub (StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x.val -= x.val;
		this.y.val -= y.val;
		this.z.val -= z.val;
		return this;
	}

	public StrictVec3 scl (StrictPoint scalar) {
		x.val *= scalar.val;
		y.val *= scalar.val;
		z.val *= scalar.val;
		return this;
	}

	public StrictVec3 scl (StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x.val *= x.val;
		this.y.val *= y.val;
		this.z.val *= z.val;
		return this;
	}

	public StrictVec3 scl (StrictVec3 v) {
		this.x.val *= v.x.val;
		this.y.val *= v.y.val;
		this.z.val *= v.z.val;
		return this;
	}

	public StrictVec3 div (StrictPoint scalar) {
		x.val /= scalar.val;
		y.val /= scalar.val;
		z.val /= scalar.val;
		return this;
	}

	public StrictVec3 div (StrictPoint x, StrictPoint y, StrictPoint z) {
		this.x.val /= x.val;
		this.y.val /= y.val;
		this.z.val /= z.val;
		return this;
	}

	public StrictVec3 div (StrictVec3 v) {
		this.x.val /= v.x.val;
		this.y.val /= v.y.val;
		this.z.val /= v.z.val;
		return this;
	}


	public StrictVec3 nor () {
		StrictPoint len2 = len(TEMP_POINT);
		if(len2.val==0f || len2.val == 1f) return this;

		float len = (float)StrictMath.sqrt(len2.val);

		x.val /= len;
		y.val /= len;
		z.val /= len;

		return this;
	}

	public StrictVec3 mulAdd (StrictVec3 vec, StrictPoint scalar) {
		this.x.val += vec.x.val * scalar.val;
		this.y.val += vec.y.val * scalar.val;
		this.z.val += vec.z.val * scalar.val;
		return this;
	}

	public StrictVec3 mulAdd (StrictVec3 vec, StrictVec3 mulVec) {
		this.x.val += vec.x.val * mulVec.x.val;
		this.y.val += vec.y.val * mulVec.y.val;
		this.z.val += vec.z.val * mulVec.z.val;
		return this;
	}


	public StrictVec3 limit (StrictPoint limit) {
		final float limit2 = limit.val * limit.val;
		StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val> limit2) {
			float sqrt = (float)StrictMath.sqrt(limit2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			z.val *= sqrt;
		}
		return this;
	}

	public StrictVec3 limit2 (StrictPoint limit2) {
		StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val> limit2.val) {
			float sqrt = (float)StrictMath.sqrt(limit2.val / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			z.val *= sqrt;
		}
		return this;
	}

	public StrictVec3 clamp (StrictPoint min, StrictPoint max) {
		final StrictPoint len2 = len2(TEMP_POINT);
		if (len2.val == 0f) return this;
		float max2 = max.val * max.val;
		if (len2.val > max2){
			final float sqrt = (float)StrictMath.sqrt(max2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			z.val *= sqrt;
			return this;
		}
		float min2 = min.val * min.val;
		if (len2.val < min2){
			final float sqrt = (float)StrictMath.sqrt(min2 / len2.val);
			x.val *= sqrt;
			y.val *= sqrt;
			z.val *= sqrt;
			return this;
		}
		return this;
	}

	public StrictVec3 setLength (StrictPoint len) {
		final float len2 = len.val*len.val;

		StrictPoint oldLen2 = len2(TEMP_POINT);
		if(oldLen2.val == 0 || oldLen2.val == len2){
			return this;
		}else{
			float sqrt = (float)StrictMath.sqrt(len2 / oldLen2.val);
			x.val *= sqrt;
			y.val*=sqrt;
			z.val *= sqrt;
			return this;
		}
	}

	public StrictVec3 setLength2 (StrictPoint len2) {
		StrictPoint oldLen2 = len2(TEMP_POINT);
		if(oldLen2.val == 0 || oldLen2.val == len2.val){
			return this;
		}else{
			float sqrt = (float)StrictMath.sqrt(len2.val / oldLen2.val);
			x.val *= sqrt;
			y.val*=sqrt;
			z.val *= sqrt;
			return this;
		}
	}

	public StrictPoint dot (StrictVec3 v, StrictPoint store) {
		store.val = x.val * v.x.val + y.val * v.y.val+ z.val * v.z.val;
		return store;
	}

	public StrictPoint dot (StrictPoint ox, StrictPoint oy,StrictPoint oz, StrictPoint store) {
		store.val = x.val * ox.val + y.val * oy.val + z.val * oz.val;
		return store;
	}

	public StrictPoint len (StrictPoint store) {
		store.val = (float) StrictMath.sqrt(x.val * x.val + y.val *y.val+ z.val *z.val);
		return store;
	}

	public static StrictPoint len2 (StrictPoint x,StrictPoint y,StrictPoint z,StrictPoint store) {
		store.val = x.val * x.val + y.val *y.val+ z.val *z.val;
		return store;
	}

	public StrictPoint len2 (StrictPoint store) {
		store.val = x.val * x.val + y.val *y.val+ z.val *z.val;
		return store;
	}

	public StrictPoint dst (StrictVec3 v,StrictPoint store) {
		final float x_d = v.x.val - x.val;
		final float y_d = v.y.val - y.val;
		final float z_d = v.z.val - z.val;
		store.val = (float)StrictMath.sqrt(x_d * x_d + y_d * y_d+ z_d * z_d);
		return store;
	}

	public StrictPoint dst (StrictPoint x, StrictPoint y, StrictPoint store) {
		final float x_d = x.val - this.x.val;
		final float y_d = y.val - this.y.val;
		final float z_d = z.val - this.z.val;
		store.val = (float)StrictMath.sqrt(x_d * x_d + y_d * y_d + z_d * z_d);
		return store;
	}

	public StrictPoint dst2 (StrictVec3 v, StrictPoint store) {
		final float x_d = v.x.val - x.val;
		final float y_d = v.y.val - y.val;
		final float z_d = v.z.val - z.val;
		store.val = x_d * x_d + y_d * y_d + z_d * z_d;
		return store;
	}

	public StrictPoint dst2 (StrictPoint x, StrictPoint y, StrictPoint store) {
		final float x_d = x.val - this.x.val;
		final float y_d = y.val - this.y.val;
		final float z_d = z.val - this.z.val;
		store.val = x_d * x_d + y_d * y_d+ z_d * z_d;
		return store;
	}

	@Override
	public String toString () {
		final String format ="%.2f";
		return "(" + String.format(format, x.val) + ", " + String.format(format, y.val) + ", "+String.format(format, z.val)+")";
	}

	public StrictVec3 crs (StrictVec3 v) {
		final float tempX = this.y.val * v.z.val - this.z.val * v.y.val;
		final float tempY = this.z.val * v.x.val - this.x.val * v.z.val;
		final float tempZ = this.x.val * v.y.val - this.y.val * v.x.val;
		this.x.val = tempX;
		this.y.val = tempY;
		this.z.val = tempZ;
		return this;
	}

	public StrictVec3 crs (StrictPoint vx, StrictPoint vy, StrictPoint vz) {
		final float tempX = this.y.val * vz.val - this.z.val * vy.val;
		final float tempY = this.z.val * vx.val - this.x.val * vz.val;
		final float tempZ = this.x.val * vy.val - this.y.val * vx.val;
		this.x.val = tempX;
		this.y.val = tempY;
		this.z.val = tempZ;
		return this;
	}

	/*
	public StrictVec3 lerp (StrictVec3 target, StrictPoint alpha) {
		final float invAlpha = 1.0f - alpha.val;
		this.x.val = (x.val * invAlpha) + (target.x.val * alpha.val);
		this.y.val = (y.val * invAlpha) + (target.y.val * alpha.val);
		this.z.val = (z.val * invAlpha) + (target.z.val * alpha.val);
		return this;
	}
	 */
	public StrictVec3 lerp (StrictVec3 target, StrictPoint alpha) {
		x.val += alpha.val * (target.x.val - x.val);
		y.val += alpha.val * (target.y.val - y.val);
		z.val += alpha.val * (target.z.val - z.val);
		return this;
	}


	public boolean epsilonEquals (StrictVec3 other, StrictPoint epsilon) {
		if (other == null) return false;
		if (StrictMath.abs(other.x.val - x.val) > epsilon.val) return false;
		if (StrictMath.abs(other.y.val - y.val) > epsilon.val) return false;
		if (StrictMath.abs(other.z.val - z.val) > epsilon.val) return false;
		return true;
	}

	/** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
	 * @return whether the vectors are the same. */
	public boolean epsilonEquals (StrictPoint x, StrictPoint y,StrictPoint z, StrictPoint epsilon) {
		if (StrictMath.abs(x.val - this.x.val) > epsilon.val) return false;
		if (StrictMath.abs(y.val - this.y.val) > epsilon.val) return false;
		if (StrictMath.abs(z.val - this.z.val) > epsilon.val) return false;
		return true;
	}

	public boolean isUnit () {
		return isUnit(StrictPoint.MIN_ROUNDING_ERROR);
	}

	public boolean isUnit (final StrictPoint margin) {
		return StrictMath.abs(len2(TEMP_POINT).val - 1f) < margin.val;
	}

	public boolean isZero () {
		return x.val == 0 && y.val == 0 && z.val == 0;
	}

	public boolean isZero (final StrictPoint margin) {
		return len2(TEMP_POINT).val < margin.val;
	}

	public boolean isOnLine (StrictVec3 other) {
		final float tempX = this.y.val * other.z.val - this.z.val * other.y.val;
		final float tempY = this.z.val * other.x.val - this.x.val * other.z.val;
		final float tempZ = this.x.val * other.y.val - this.y.val * other.x.val;
		final float len2 = tempX * tempX + tempY *tempY+ tempZ *tempZ;
		return len2 <= StrictPoint.MIN_ROUNDING_ERROR.val;
	}

	public boolean isOnLine (StrictVec3 other, StrictPoint epsilon) {
		final float tempX = this.y.val * other.z.val - this.z.val * other.y.val;
		final float tempY = this.z.val * other.x.val - this.x.val * other.z.val;
		final float tempZ = this.x.val * other.y.val - this.y.val * other.x.val;
		final float len2 = tempX * tempX + tempY *tempY+ tempZ *tempZ;
		return len2 <= epsilon.val;
	}

	public boolean isCollinear (StrictVec3 other) {
		return isOnLine(other) && dot(other,TEMP_POINT).val > 0f;
	}

	public boolean isCollinear (StrictVec3 other, StrictPoint epsilon) {
		return isOnLine(other, epsilon) && dot(other, TEMP_POINT).val > 0f;
	}

	public boolean isCollinearOpposite (StrictVec3 other) {
		return isOnLine(other) && dot(other,TEMP_POINT).val < 0f;
	}

	public boolean isCollinearOpposite (StrictVec3 other, StrictPoint epsilon) {
		return isOnLine(other, epsilon) && dot(other,TEMP_POINT).val < 0f;
	}

	public boolean isPerpendicular (StrictVec3 vector) {
		return dot(vector,TEMP_POINT).isZero();
	}

	public boolean isPerpendicular (StrictVec3 vector, StrictPoint epsilon) {
		return dot(vector,TEMP_POINT).isZero(epsilon);
	}

	public boolean hasSameDirection (StrictVec3 vector) {
		return dot(vector, TEMP_POINT).val > 0;
	}

	public boolean hasOppositeDirection (StrictVec3 vector) {
		return dot(vector,TEMP_POINT).val < 0;
	}

	public StrictVec3 setZero () {
		this.x.val = 0;
		this.y.val = 0;
		this.z.val = 0;
		return this;
	}
}
