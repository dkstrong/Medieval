package asf.medieval.strictmath;

/**
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictPoint {

	public static final StrictPoint MIN_ROUNDING_ERROR = new StrictPoint(0.000001f);
	public static final StrictPoint PI = new StrictPoint(3.1415927f);
	public static final StrictPoint radDeg = new StrictPoint(180f/PI.val);
	public static final StrictPoint degRad = new StrictPoint(PI.val/180f);

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
		val *= degRad.val;
		return this;
	}

	public StrictPoint radDeg(){
		val *= radDeg.val;
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

	@Override
	public String toString(){
		return String.valueOf(val);
	}



}
