package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.strictmath.StrictVec3;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainChunk;
import asf.medieval.terrain.TerrainLoader;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.omg.CORBA.FieldNameHelper;

/**
 * Created by daniel on 12/13/15.
 */
public strictfp class TerrainInfo {

	public StrictPoint[] fieldData;
	public int fieldWidth;
	public int fieldHeight;

	public StrictPoint magnitude;
	public StrictVec3 corner00 = new StrictVec3("-250", "0", "-250");
	public StrictVec3 corner11 = new StrictVec3("250", "0", "250");
	private final StrictVec3 corner10 = new StrictVec3();
	private final StrictVec3 corner01 = new StrictVec3();

	public void setFieldData(StrictPoint[] fieldData, int fieldWidth, int fieldHeight, StrictVec3 corner00, StrictVec3 corner11,StrictPoint magnitude){
		this.fieldData = fieldData;
		this.fieldWidth = fieldWidth;
		this.fieldHeight =fieldHeight;

		this.magnitude = magnitude;
		this.corner00 = corner00;
		this.corner11 = corner11;
		corner10.x.set(corner11.x);
		corner10.y.set(StrictPoint.ZERO);
		corner10.z.set(corner00.z);
		corner01.x.set(corner00.x);
		corner01.y.set(StrictPoint.ZERO);
		corner01.z.set(corner11.z);

	}

	public void setFieldDataFromTerrain(Terrain terrain)
	{
		fieldData = new StrictPoint[terrain.fieldData.length];
		for (int i = 0; i < terrain.fieldData.length; i++) {
			fieldData[i] = new StrictPoint().fromFloat(terrain.fieldData[i]);
		}

		fieldWidth = terrain.fieldWidth;
		fieldHeight = terrain.fieldHeight;
		magnitude = new StrictPoint().fromFloat(terrain.magnitude);
		corner00 = new StrictVec3();
		corner00.x.fromFloat(terrain.corner00.x);
		corner00.y.fromFloat(terrain.corner00.y);
		corner00.z.fromFloat(terrain.corner00.z);

		corner11 = new StrictVec3();
		corner11.x.fromFloat(terrain.corner11.x);
		corner11.y.fromFloat(terrain.corner11.y);
		corner11.z.fromFloat(terrain.corner11.z);

		corner10.x.set(corner11.x);
		corner10.y.set(StrictPoint.ZERO);
		corner10.z.set(corner00.z);
		corner01.x.set(corner00.x);
		corner01.y.set(StrictPoint.ZERO);
		corner01.z.set(corner11.z);
	}


	public StrictVec3 getWeightedNormalAt(StrictVec2 worldCoordinate, StrictVec3 store)
	{
		float fieldX = scalarLimitsInterpolation(worldCoordinate.x.val, corner00.x.val, corner11.x.val, 0, fieldWidth - 1);
		float fieldY = scalarLimitsInterpolation(worldCoordinate.y.val, corner00.z.val, corner11.z.val, 0, fieldHeight - 1);
		int x0 = (int) fieldX;
		int y0 = (int) fieldY;

		getWeightedNormalAt(x0,y0, store);
		return store;
	}

	private static final StrictVec3 tmpV1 = new StrictVec3();
	private static final StrictVec3 tmpV2 = new StrictVec3();
	private static final StrictVec3 tmpV3 = new StrictVec3();
	private static final StrictVec3 tmpV4 = new StrictVec3();
	private static final StrictVec3 tmpV5 = new StrictVec3();
	private static final StrictVec3 tmpV6 = new StrictVec3();
	private static final StrictVec3 tmpV7 = new StrictVec3();
	private static final StrictVec3 tmpV8 = new StrictVec3();

	private static final StrictPoint dx = new StrictPoint();
	private static final StrictPoint dy = new StrictPoint();
	private static final StrictPoint a = new StrictPoint();

	private StrictVec3 getPositionAt(int x, int y, StrictVec3 out) {
		//if(terrain!=null && terrain.masterChunk != this){
		//return terrain.masterChunk.getPositionAt(fieldStartX+x, fieldStartY+y, out);
		//}

		dx.val =  (float) x / (float) (fieldWidth - 1);
		dy.val= (float) y / (float) (fieldHeight - 1);
		a.val = fieldData[y * fieldWidth + x].val;

		out.set(corner00).lerp(corner10, dx).lerp(tmpV1.set(corner01).lerp(corner11, dx), dy);
		out.add(tmpV1.set(StrictPoint.ZERO,magnitude,StrictPoint.ZERO).scl(a));
		return out;
	}

	private static final StrictPoint faces= new StrictPoint();

	private StrictVec3 getWeightedNormalAt(int x, int y, StrictVec3 out) {

		// The following approach weights the normal of the four triangles (half quad) surrounding the position.
		// A more accurate approach would be to weight the normal of the actual triangles.

		faces.set(StrictPoint.ZERO);
		out.setZero();

		StrictVec3 center = getPositionAt(x, y, tmpV2);
		StrictVec3 left = x > 0 ? getPositionAt(x - 1, y, tmpV3) : null;
		StrictVec3 right = x < (fieldWidth - 1) ? getPositionAt(x + 1, y, tmpV4) : null;
		StrictVec3 bottom = y > 0 ? getPositionAt(x, y - 1, tmpV5) : null;
		StrictVec3 top = y < (fieldHeight - 1) ? getPositionAt(x, y + 1, tmpV6) : null;
		if (top != null && left != null) {
			out.add(tmpV7.set(top).sub(center).nor().crs(tmpV8.set(center).sub(left).nor()).nor());
			faces.add(StrictPoint.ONE);
		}
		if (left != null && bottom != null) {
			out.add(tmpV7.set(left).sub(center).nor().crs(tmpV8.set(center).sub(bottom).nor()).nor());
			faces.add(StrictPoint.ONE);
		}
		if (bottom != null && right != null) {
			out.add(tmpV7.set(bottom).sub(center).nor().crs(tmpV8.set(center).sub(right).nor()).nor());
			faces.add(StrictPoint.ONE);
		}
		if (right != null && top != null) {
			out.add(tmpV7.set(right).sub(center).nor().crs(tmpV8.set(center).sub(top).nor()).nor());
			faces.add(StrictPoint.ONE);
		}
		if (faces.greaterThan(StrictPoint.ZERO))
			out.div(faces);
		else
			out.set(StrictPoint.ZERO,magnitude,StrictPoint.ZERO).nor();
		return out;
	}

	public StrictPoint getElevation(StrictVec2 worldCoordinate, StrictPoint store) {
		float fieldX = scalarLimitsInterpolation(worldCoordinate.x.val, corner00.x.val, corner11.x.val, 0, fieldWidth - 1);
		float fieldY = scalarLimitsInterpolation(worldCoordinate.y.val, corner00.z.val, corner11.z.val, 0, fieldHeight - 1);

		int x0 = (int) fieldX;
		int y0 = (int) fieldY;
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		if (x1 >= fieldWidth) x1 = fieldWidth - 1;
		if (y1 >= fieldHeight) y1 = fieldHeight - 1;

		float s00 = getElevation(x0, y0);
		float s10 = getElevation(x1, y0);
		float s01 = getElevation(x0, y1);
		float s11 = getElevation(x1, y1);

		store.val = interpolateBilinear(fieldX - x0, fieldY - y0, s00, s10, s01, s11);

		return store;
	}


	private float getElevation(int fieldX, int fieldY) {
		return fieldData[fieldY * fieldWidth + fieldX].val * magnitude.val;
	}

	private static float scalarLimitsInterpolation(float input, float oldMin, float oldMax, float newMin, float newMax) {
		float percent = percentOfScalar(UtMath.clamp(input, oldMin, oldMax), oldMin, oldMax);
		return scalarOfPercent(percent, newMin, newMax);
	}

	public static float percentOfScalar(float input, float oldMin, float oldMax) {
		return (input - oldMin) / (oldMax - oldMin);
	}

	private static float scalarOfPercent(float input, float newMin, float newMax) {
		return (newMin + (input * (newMax - newMin)));
	}

	private static float interpolateBilinear(float xfrac, float yfrac, float s00, float s10, float s01, float s11)
	{
		return (1 - yfrac) * ((1 - xfrac)*s00 + xfrac*s10) + yfrac * ((1 - xfrac)*s01 + xfrac*s11);
	}


}
