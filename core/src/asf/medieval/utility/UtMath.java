package asf.medieval.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.ScreenUtils;

import java.math.BigDecimal;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class UtMath {

	public static final float PI = (float)Math.PI;
	public static final float HALF_PI = (float)Math.PI / 2f;
	public static final float QUARTER_PI = (float)Math.PI / 4f;
	public static final float ONE_THIRD = 1f / 3f;


	public static Vector3 quaternionToEuler(Quaternion quaternion, Vector3 store) {
		float sqw = quaternion.w * quaternion.w;
		float sqx = quaternion.x * quaternion.x;
		float sqy = quaternion.y * quaternion.y;
		float sqz = quaternion.z * quaternion.z;
		store.z = (MathUtils.atan2(2.0f * (quaternion.x * quaternion.y + quaternion.z * quaternion.w), (sqx - sqy - sqz + sqw)) * (MathUtils.radDeg));
		store.x = (MathUtils.atan2(2.0f * (quaternion.y * quaternion.z + quaternion.x * quaternion.w), (-sqx - sqy + sqz + sqw)) * (MathUtils.radDeg));
		store.y = (UtMath.asin(-2.0f * (quaternion.x * quaternion.z - quaternion.y * quaternion.w)) * MathUtils.radDeg);
		return store;

	}

	/**
	 * Returns the arc sine of an angle given in radians.<br>
	 * Special cases:
	 * <ul><li>If fValue is smaller than -1, then the result is -HALF_PI.
	 * <li>If the argument is greater than 1, then the result is HALF_PI.</ul>
	 * @param fValue The angle, in radians.
	 * @return fValue's asin
	 * @see Math#asin(double)
	 */
	public static float asin(float fValue) {
		if (-1.0f < fValue) {
			if (fValue < 1.0f) {
				return (float) Math.asin(fValue);
			}

			return MathUtils.PI/2f;
		}

		return -MathUtils.PI/2f;
	}

	public static int pow(int base, int exp) {
		int result = 1;
		while (exp != 0)
		{
			if ((exp & 1) == 1)
				result *= base;
			exp >>= 1;
			base *= base;
		}

		return result;
	}

	/**
	 *
	 * @param input
	 * @param match
	 * @return input * -1f if the sign of input and match are not the same, otherwise just returns input.
	 */
	public static float matchSign(float input, float match) {
		if (match < 0 && input > 0) {
			return -input;
		} else if (match > 0 && input < 0) {
			return -input;
		}
		return input;
	}

	public static boolean isBetween(float input, float min, float max) {
		return input >= min && input <= max;
	}

	public static boolean isEven(int n) {
		return n % 2 == 0;
	}

	public static float clamp(float input, float min, float max) {
		return (input < min) ? min : (input > max) ? max : input;
	}

	public static int clamp(int input, int min, int max) {
		return (input < min) ? min : (input > max) ? max : input;
	}

	/**
	 * Linear interpolation from startValue to endValue by the given percent.
	 * Basically: ((1 - percent) * startValue) + (percent * endValue)
	 *
	 * @param scale
	 *            scale value to use. if 1, use endValue, if 0, use startValue.
	 * @param startValue
	 *            Begining value. 0% of f
	 * @param endValue
	 *            ending value. 100% of f
	 * @return The interpolated value between startValue and endValue.
	 */
	public static float interpolateLinear(float scale, float startValue, float endValue) {
		if (startValue == endValue) {
			return startValue;
		}
		if (scale <= 0f) {
			return startValue;
		}
		if (scale >= 1f) {
			return endValue;
		}
		return ((1f - scale) * startValue) + (scale * endValue);
	}

	public static void interpolateLinear(float scale, Vector3 startValue, Vector3 endValue, Vector3 store) {
		store.x = interpolateLinear(scale, startValue.x, endValue.x);
		store.y = interpolateLinear(scale, startValue.y, endValue.y);
		store.z = interpolateLinear(scale, startValue.z, endValue.z);
	}

	public static void interpolateLinear(float scale, Quaternion q1, Quaternion q2, Quaternion store) {
		// Create a local quaternion to store the interpolated quaternion
		if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
			store.set(q1);
			return;
		}

		float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);

		if (result < 0.0f) {
			// Negate the second quaternion and the result of the dot product
			q2.x = -q2.x;
			q2.y = -q2.y;
			q2.z = -q2.z;
			q2.w = -q2.w;
			result = -result;
		}

		// Set the first and second scale for the interpolation
		float scale0 = 1 - scale;
		float scale1 = scale;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final double angle = Math.acos(result);
			final double invSinTheta = 1f / Math.sin(angle);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = (float)(Math.sin((1 - scale) * angle) * invSinTheta);
			scale1 = (float)(Math.sin((scale * angle)) * invSinTheta);
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special
		// form of linear interpolation for quaternions.
		store.x = (scale0 * q1.x) + (scale1 * q2.x);
		store.y = (scale0 * q1.y) + (scale1 * q2.y);
		store.z = (scale0 * q1.z) + (scale1 * q2.z);
		store.w = (scale0 * q1.w) + (scale1 * q2.w);

	}

	public static void interpolate(Interpolation interpolation,
				       float scale,
				       Vector3 startValue, Vector3 endValue, Vector3 store){
		store.x =interpolation.apply(startValue.x, endValue.x, scale);
		store.y =interpolation.apply(startValue.y, endValue.y, scale);
		store.z =interpolation.apply(startValue.z, endValue.z, scale);
	}

	public static void interpolate(Interpolation interpolation,
				       float scale,
				       Color startValue, Color endValue, Color store){
		store.r =interpolation.apply(startValue.r, endValue.r, scale);
		store.g =interpolation.apply(startValue.g, endValue.g, scale);
		store.b =interpolation.apply(startValue.b, endValue.b, scale);
		store.a =interpolation.apply(startValue.b, endValue.b, scale);
	}


	public static float sqr(float fValue) {
		return fValue * fValue;
	}

	/**
	 * ensures the length of source is not greater than limit
	 * @param source
	 * @param limit
	 */
	public static void truncate(Vector3 source, float limit) {

		if (source.len2() <= UtMath.sqr(limit)) {
			//return source;
		} else {
			source.nor().scl(limit);
			//UtMath.scaleAdd(source.nor(), limit, Vector3.Zero);
			//source.nor().scaleAdd(limit, Vector3.Zero);
		}
	}

	/**
	 * ensures the length of source is not greater than limit
	 * might be faster than regular truncate?
	 * @param vector
	 * @param max
	 */
	public static void truncateAlt(Vector3 vector, float max)
	{
		float i = max / vector.len();
		i = i < 1.0f ? i : 1.0f;
		vector.scl(i);
	}

	/**
	 * ensures the length of source is not greater than limit
	 * @param source
	 * @param limit
	 */
	public static void truncate(Vector2 source, float limit) {

		if (source.len2() <= UtMath.sqr(limit)) {
			//return source;
		} else {
			source.nor().scl(limit);
			//UtMath.scaleAdd(source.nor(), limit, Vector3.Zero);
			//source.nor().scaleAdd(limit, Vector3.Zero);
		}
	}

	/**
	 * ensures the absolute value of source is not greater than limit
	 * @param source
	 * @param limit
	 * @return
	 */
	public static float truncate(float source, float limit){
		float sign = source < 0 ? -1f : 1f;
		source *= sign; // ensure source is positive
		source = source < limit ? source : limit;
		return source * sign;
	}

	public static void lookAt(Vector3 direction, Vector3 up, Quaternion store) {

		Vector3 vect3 = new Vector3();
		Vector3 vect1 = new Vector3();
		Vector3 vect2 = new Vector3();

		vect3.set(direction).nor();
		vect1.set(up).crs(direction).nor();
		vect2.set(direction).crs(vect1).nor();

		fromAxes(vect1, vect2, vect3, store);

	}

	public static void fromAxes(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Quaternion store) {
		Matrix3 m3 = new Matrix3(new float[]{xAxis.x, yAxis.x, zAxis.x, xAxis.y, yAxis.y, zAxis.y, xAxis.z, yAxis.z, zAxis.z});
		store.setFromMatrix(m3);
	}


	/**
	 * use this value as the "scale" value in interpolation to use hermite acceleration (easily start up, easily slow down at the end)
	 *
	 * @param t
	 * @return
	 */
	public static float hermiteT(float t) {
		return (3.0f * t * t) - (2.0f * t * t * t);
	}

	public static float smallest(float val1, float val2) {
		return (val1 < val2 ? val1 : val2);
	}

	public static float smallest(float val1, float val2, float... moreVals) {
		float smallest = smallest(val1, val2);

		for (float m : moreVals) {
			smallest = smallest(m, smallest);
		}

		return smallest;
	}

	public static int smallest(int val1, int val2) {
		return (val1 < val2 ? val1 : val2);
	}

	public static float largest(float val1, float val2) {
		return (val1 > val2 ? val1 : val2);
	}

	public static float largest(float val1, float val2, float... moreVals) {
		float largest = largest(val1, val2);

		for (float m : moreVals) {
			largest = largest(m, largest);
		}

		return largest;
	}

	public static int largest(int val1, int val2) {
		return (val1 > val2 ? val1 : val2);
	}

	public static String round(Quaternion quaternion, int decimalPlace) {
		if (quaternion == null) {
			return "null";
		}
		return "(" + round(quaternion.x, decimalPlace) + ", " + round(quaternion.y, decimalPlace) + ", " + round(quaternion.z, decimalPlace) + ", " + round(quaternion.w, decimalPlace) + ")";
	}

	public static String round(Vector3 vec, int decimalPlace) {
		if (vec == null) {
			return "null";
		}
		return "(" + round(vec.x, decimalPlace) + ", " + round(vec.y, decimalPlace) + ", " + round(vec.z, decimalPlace) + ")";
	}

	public static String round(Vector2 vec, int decimalPlace) {
		if (vec == null) {
			return "null";
		}
		return "(" + round(vec.x, decimalPlace) + ", " + round(vec.y, decimalPlace) + ")";
	}

	public static BigDecimal round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd;
	}

	public static BigDecimal round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd;
	}

	public static float firstQuartile(float val1, float val2) {
		return midpoint(val1, midpoint(val1, val2));
	}

	public static float thirdQuartile(float val1, float val2) {
		return midpoint(val2, midpoint(val1, val2));
	}

	public static float midpoint(float val1, float val2) {
		return (val1 + val2) / 2f;
	}

	public static int midpoint(int val1, int val2) {
		return ((val1 + val2) / 2);
	}

	/**
	 * Absolute value of the difference of two values
	 *
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float range(float val1, float val2) {return UtMath.abs(val1 - val2);}

	public static int range(int val1, int val2) {return Math.abs(val1 - val2);}

	public static float range(Vector3 val1, Vector3 val2){
		float x = val2.x - val1.x;
		float y = val2.y - val1.y;
		float z = val2.z - val1.z;
		float len = (float)Math.sqrt(x * x + y * y + z * z);
		return len;

	}

	public static float abs(float value){
		if(value < 0){
			return -value;
		}
		return value;
	}

	public static float scalarLimitsInterpolation(float input, float oldMin, float oldMax, float newMin, float newMax) {
		float percent = UtMath.percentOfScalar(UtMath.clamp(input, oldMin, oldMax), oldMin, oldMax);
		return UtMath.scalarOfPercent(percent, newMin, newMax);
	}

	public static float scalarLimitsExtrapolation(float input, float oldMin, float oldMax, float newMin, float newMax) {
		float percent = UtMath.percentOfScalar(input, oldMin, oldMax);
		return UtMath.scalarOfPercent(percent, newMin, newMax);
	}

	public static Vector2 scalarLimitsInterpolation(float input, float oldMin, float oldMax, Vector2 newMin, Vector2 newMax, Vector2 store)
	{
		float percent = UtMath.percentOfScalar(UtMath.clamp(input, oldMin, oldMax), oldMin, oldMax);
		store.x = UtMath.scalarOfPercent(percent, newMin.x, newMax.x);
		store.y = UtMath.scalarOfPercent(percent, newMin.y, newMax.y);
		return store;
	}

	public static Vector2 scalarLimitsExtrapolation(float input, float oldMin, float oldMax, Vector2 newMin, Vector2 newMax, Vector2 store)
	{
		float percent = UtMath.percentOfScalar(input, oldMin, oldMax);
		store.x = UtMath.scalarOfPercent(percent, newMin.x, newMax.x);
		store.y = UtMath.scalarOfPercent(percent, newMin.y, newMax.y);
		return store;
	}

	/**
	 * Takes a float with a min and max value and makes it between 0 and 1 with the same percentage value. reverse of scalarOfPercent()
	 *
	 *
	 * @param input val between oldMin and oldMax
	 * @param oldMin
	 * @param oldMax
	 * @return value between 0 and 1
	 */
	public static float percentOfScalar(float input, float oldMin, float oldMax) {
		return (input - oldMin) / (oldMax - oldMin);
	}

	/**
	 * Take a float between 0 and 1 and scale it to be between newMin and newMax, reverse of percentOfScalar()
	 *
	 *
	 * @param input val between 0 and 1
	 * @param newMin
	 * @param newMax
	 * @return value between newMin and newMax
	 */
	public static float scalarOfPercent(float input, float newMin, float newMax) {
		return (newMin + (input * (newMax - newMin)));
	}




	public static float interpolateBilinear(float xfrac, float yfrac, float s00, float s10, float s01, float s11)
	{
		return (1 - yfrac) * ((1 - xfrac)*s00 + xfrac*s10) + yfrac * ((1 - xfrac)*s01 + xfrac*s11);
	}

	public static Color interpolateBilinear(float xfrac, float yfrac, Color s00, Color s10, Color s01, Color s11, Color store)
	{
		store.r = interpolateBilinear(xfrac, yfrac, s00.r, s10.r, s01.r, s11.r);
		store.g = interpolateBilinear(xfrac, yfrac, s00.g, s10.g, s01.g, s11.g);
		store.b = interpolateBilinear(xfrac, yfrac, s00.b, s10.b, s01.b, s11.b);
		store.a = interpolateBilinear(xfrac, yfrac, s00.a, s10.a, s01.a, s11.a);
		return store;
	}

	public static Vector3 interpolateBilinear(float xfrac, float yfrac, Vector3 s00, Vector3 s10, Vector3 s01, Vector3 s11)
	{
		Vector3 store = new Vector3();
		store.x = interpolateBilinear(xfrac, yfrac, s00.x, s10.x, s01.x, s11.x);
		store.y = interpolateBilinear(xfrac, yfrac, s00.y, s10.y, s01.y, s11.y);
		store.z = interpolateBilinear(xfrac, yfrac, s00.z, s10.z, s01.z, s11.z);
		return store;
	}


	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
	 * @param iValue The integer to examine.
	 * @return The integer's sign.
	 */
	public static int sign(int iValue) {
		if (iValue > 0) {
			return 1;
		}
		if (iValue < 0) {
			return -1;
		}
		return 0;
	}


	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
	 * @param fValue The float to examine.
	 * @return The float's sign.
	 */
	public static float sign(float fValue) {
		return Math.signum(fValue);
	}

	public static float randomFloat(float min, float max) {
		return MathUtils.random(min, max); //return (min + (FastMath.range.nextFloat() * (max - min)));
	}

	public static float randomSign() {
		return (MathUtils.randomBoolean() ? 1f : -1f);
	}

	public static boolean isPointInQuadrilateral(Vector3 point, Vector3 q1, Vector3 q2, Vector3 q3, Vector3 q4) {
		return Intersector.isPointInTriangle(point, q1,q3,q4) || Intersector.isPointInTriangle(point, q1,q2,q3);
	}

	public static boolean isPointInRect(Vector2 point, Vector2 q1, Vector2 q2, Vector2 q3, Vector2 q4){
		return Intersector.isPointInTriangle(point,q1,q3,q4) || Intersector.isPointInTriangle(point,q1,q2,q3);
	}

	public static Vector2 toVector2(Vector3 vec3, Vector2 store){
		store.x = vec3.x;
		store.y = vec3.z;
		return store;
	}

	public static Vector3 toVector3(Vector2 vec2, Vector3 store){
		store.x = vec2.x;
		store.y = 0;
		store.z = vec2.y;
		return store;
	}

	/** Converts the components of a color, as specified by the HSB model, to an
	 * equivalent set of values for the default RGB model.
	 * <p>
	 * The <code>saturation</code> and <code>brightness</code> components should
	 * be floating-point values between zero and one (numbers in the range
	 * 0.0-1.0). The <code>hue</code> component can be any floating-point
	 * number. The floor of this number is subtracted from it to create a
	 * fraction between 0 and 1. This fractional number is then multiplied by
	 * 360 to produce the hue angle in the HSB color model.
	 * <p>
	 * The integer that is returned by <code>HSBtoRGB</code> encodes the value
	 * of a color in bits 0-23 of an integer value that is the same format used
	 * by the method getRGB(). This integer can be
	 * supplied as an argument to the <code>Color</code> constructor that takes
	 * a single integer argument.
	 *
	 * @param hue the hue component of the color
	 * @param saturation the saturation of the color
	 * @param brightness the brightness of the color
	 * @return the RGB value of the color with the indicated hue, saturation,
	 * and brightness.
	 * @see java.awt.Color#getRGB()
	 * @see java.awt.Color#Color(int)
	 * @see java.awt.image.ColorModel#getRGBdefault()
	 * @since JDK1.0 */
	public static int HSBtoRGBA8888(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int)(brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int)h) {
				case 0:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(t * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int)(q * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(q * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int)(t * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(q * 255.0f + 0.5f);
					break;
			}
		}
		return (r << 24) | (g << 16) | (b << 8) | 0x000000ff;
	}




	private UtMath() {
	}
}
