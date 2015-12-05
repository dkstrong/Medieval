package asf.medieval.model.math;

import java.math.BigDecimal;

/**
 * Created by daniel on 12/5/15.
 */
public class StrictFloat {
	private int value;
	private BigDecimal v;

	public StrictFloat(float floatValue) {
		this.value = encode(floatValue);
		BigDecimal b = new BigDecimal(5d);

		b.add(new BigDecimal("3d"));

	}

	public float getFloatValue(){
		return decode(value);
	}

	public static int encode(float floatValue){
		return Math.round(floatValue * 1000.0f);
	}

	public static float decode(int value){
		return value / 1000.0f;
	}
}
