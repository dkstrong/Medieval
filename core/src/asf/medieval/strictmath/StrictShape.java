package asf.medieval.strictmath;

/**
 * represents either a square or a circle, the choice is yours and yours alone
 *
 * Created by daniel on 12/11/15.
 */
public strictfp class StrictShape {
	public final StrictVec2 center = new StrictVec2();
	public final StrictVec2 dimensions = new StrictVec2();
	public final StrictPoint radius = new StrictPoint();

	public StrictShape fromRadius(String r){

		return fromRadius(new StrictPoint(r));
	}

	public StrictShape fromRadius(StrictPoint r){

		dimensions.x.val = r.val*2f;
		dimensions.y.val = r.val*2f;

		center.x.val =0;
		center.y.val=0;

		radius.val = r.val;
		return this;
	}

	public StrictShape fromExtents(final String xExtent, final String yExtent, final String xCenter, final String yCenter){
		return fromExtents(new StrictPoint(xExtent),
			new StrictPoint(yExtent),
			new StrictPoint(xCenter),
			new StrictPoint(yCenter)
			);
	}
	public StrictShape fromExtents(final StrictPoint xExtent, final StrictPoint yExtent, final StrictPoint xCenter, final StrictPoint yCenter){

		dimensions.x.val = xExtent.val*2f;
		dimensions.y.val = yExtent.val*2f;

		center.x.val = xCenter.val;
		center.y.val = yCenter.val;

		radius.val = xExtent.val > yExtent.val ? xExtent.val : yExtent.val;
		return this;
	}

	public StrictShape fromDimensions(StrictVec2 min, StrictVec2 max){

		dimensions.set(max).sub(min);

		center.x.val = (max.x.val + min.x.val) / 2f;
		center.y.val = (max.y.val + min.y.val) / 2f;

		final float xExtent = dimensions.x.val/2f;
		final float yExtent = dimensions.y.val/2f;

		radius.val = xExtent > yExtent ? xExtent : yExtent;
		return this;
	}

}
