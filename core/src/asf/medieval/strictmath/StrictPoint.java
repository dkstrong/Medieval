package asf.medieval.strictmath;

/**
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictPoint {

	public static final StrictPoint ZERO = new StrictPoint("0");
	public static final StrictPoint ONE = new StrictPoint("1");
	public static final StrictPoint TWO = new StrictPoint("2");
	public static final StrictPoint THREE = new StrictPoint("3");
	public static final StrictPoint FOUR = new StrictPoint("4");
	public static final StrictPoint FIVE = new StrictPoint("5");
	public static final StrictPoint SIX = new StrictPoint("6");
	public static final StrictPoint SEVEN = new StrictPoint("7");
	public static final StrictPoint EIGHT = new StrictPoint("8");
	public static final StrictPoint NINE = new StrictPoint("9");
	public static final StrictPoint TEN = new StrictPoint("10");

	public static final StrictPoint MIN_ROUNDING_ERROR = new StrictPoint(0.000001f);
	public static final StrictPoint MAX_VALUE = new StrictPoint(Float.MAX_VALUE);
	public static final StrictPoint PI = new StrictPoint(3.1415927f);
	public static final StrictPoint PI2 = new StrictPoint(3.1415927f * 2f);
	public static final StrictPoint HALF_PI = new StrictPoint(3.1415927f /2f);
	private static final StrictPoint RAD_DEG = new StrictPoint(180f/PI.val);
	private static final StrictPoint DEG_RAD = new StrictPoint(PI.val/180f);

	private static final StrictPoint TEMP_POINT = new StrictPoint();

	public float val;


	public StrictPoint() {
	}

	private StrictPoint(float val){
		this.val = val;
	}

	public StrictPoint(int val) {
		this.val = (float) val;
	}

	public StrictPoint(String val) {
		this.val = Float.valueOf(val);
	}

	public StrictPoint(StrictPoint val){
		this.val = val.val;
	}

	public StrictPoint cpy(){
		return new StrictPoint(this);
	}

	public StrictPoint set(int value){
		this.val = (float) value;
		return this;
	}

	/**
	 * bringing in float values is not deterministic, this should only be used for the user input
	 * when  creating commands to be processed by the server, the server will then respond with a
	 * a proper strict point..
	 *
	 * @param value
	 * @return
	 */
	public StrictPoint fromFloat(float value){
		this.val = value;
		return this;
	}

	public StrictPoint set(String value){
		this.val = Float.valueOf(value);
		return this;
	}

	public StrictPoint set(StrictPoint value){
		this.val = value.val;
		return this;
	}

	public StrictPoint add(StrictPoint other){
		val += other.val;
		return this;
	}

	public StrictPoint sub(StrictPoint other){
		val -= other.val;
		return this;
	}

	public StrictPoint mul(StrictPoint other){
		val *= other.val;
		return this;
	}

	public StrictPoint mul(String other){
		val *= TEMP_POINT.set(other).val;
		return this;
	}

	public StrictPoint negate(){
		val *= -1f;
		return this;
	}

	public StrictPoint div(StrictPoint other){
		val /= other.val;
		return this;
	}

	public StrictPoint sqr(){
		val *= val;
		return this;
	}

	public StrictPoint sqrt(){
		val = (float)StrictMath.sqrt(val);
		return this;
	}

	public StrictPoint abs(){
		if(val <0) val *= -1f;
		return this;
	}

	public StrictPoint larger(StrictPoint other){
		if(other.val > val)
			val = other.val;
		return this;
	}

	public StrictPoint smaller(StrictPoint other){
		if(other.val < val)
			val = other.val;
		return this;
	}

	public StrictPoint sin(){
		val = (float)StrictMath.sin(val);
		return this;
	}

	public StrictPoint cos(){
		val = (float)StrictMath.cos(val);
		return this;
	}

	public StrictPoint tan(){
		val = (float)StrictMath.tan(val);
		return this;
	}

	public StrictPoint atan(){
		val = (float)StrictMath.atan(val);
		return this;
	}

	public StrictPoint atan2(StrictPoint other){
		val = (float)StrictMath.atan2(val, other.val);
		return this;
	}

	public StrictPoint degRad(){
		val *= DEG_RAD.val;
		return this;
	}

	public StrictPoint radDeg(){
		val *= RAD_DEG.val;
		return this;
	}

	public int ceil(){
		return StrictMath.round((float)StrictMath.ceil(val));
	}

	public int floor(){
		return StrictMath.round((float)StrictMath.floor(val));
	}

	public int round(){
		return StrictMath.round(val);
	}

	public boolean isZero () {
		return StrictMath.abs(val) <= MIN_ROUNDING_ERROR.val;
	}

	public boolean isZero (StrictPoint tolerance) {
		return StrictMath.abs(val) <= tolerance.val;
	}

	public boolean lessThan(StrictPoint other){return val < other.val;}

	public boolean lessThanOrEqual(StrictPoint other){return val <= other.val;}

	public boolean greaterThan(StrictPoint other){return val > other.val;}

	public boolean greaterThanOrEqual(StrictPoint other){return val >= other.val;}


	public float toFloat(){return val;}

	@Override
	public String toString(){
		return String.valueOf(val);
	}



}
