package asf.medieval.view;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class RtsCamController implements InputProcessor {



	public enum CamDegree {

		SIDE,
		FWD,
		ROT,
		ZOOM;
	}

	private final CameraManager cameraManager;
	private final ElevationProvider elevationProvider;

	private boolean dragMouse = false;
	//private Listener listener;
	private int[] direction = new int[4];
	private float[] accelPeriod = new float[4];
	private float[] maxSpeed = new float[4];
	private float[] maxAccelPeriod = new float[4];
	private float[] minValue = new float[4];
	private float[] maxValue = new float[4];
	public final Vector3 center = new Vector3();
	private float rot = 0;
	private float distance = 45;
	private float minElevation =0;

	public RtsCamController(CameraManager cameraManager, ElevationProvider elevationProvider) {
		this.cameraManager = cameraManager;
		this.elevationProvider = elevationProvider;

		setMinMaxValues(CamDegree.FWD, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(CamDegree.SIDE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(CamDegree.ROT, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(CamDegree.ZOOM, 4, 100);

		setMaxSpeed(CamDegree.FWD, 24f*2f, 0.25f);
		setMaxSpeed(CamDegree.SIDE, 24f*2f, 0.25f);
		setMaxSpeed(CamDegree.ROT, 2f, 0.3f);
		setMaxSpeed(CamDegree.ZOOM, 60f, 0.25f);

		minElevation = 0;
	}

	// SIDE and FWD min/max values are ignored, need to fix this..
	public final void setMinMaxValues(CamDegree dg, float min, float max) {
		minValue[dg.ordinal()] = min;
		maxValue[dg.ordinal()] = max;
	}

	public final void setMaxSpeed(CamDegree dg, float maxSpeed, float accelerationTime) {
		this.maxSpeed[dg.ordinal()] = maxSpeed / accelerationTime;
		this.maxAccelPeriod[dg.ordinal()] = accelerationTime;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public void update(float delta) {

		for (int i = 0; i < direction.length; i++) {
			int dir = direction[i];
			switch (dir) {
				case -1:
					accelPeriod[i] = UtMath.clamp(accelPeriod[i] - delta, -maxAccelPeriod[i], accelPeriod[i]);
					break;
				case 0:
					if (accelPeriod[i] != 0) {
						double oldSpeed = accelPeriod[i];
						if (accelPeriod[i] > 0) {
							accelPeriod[i] -= delta;
						} else {
							accelPeriod[i] += delta;
						}
						if (oldSpeed * accelPeriod[i] < 0) {
							accelPeriod[i] = 0;
						}
					}
					break;
				case 1:
					accelPeriod[i] = UtMath.clamp(accelPeriod[i] + delta, accelPeriod[i], maxAccelPeriod[i]);
					break;
			}
		}
		distance += maxSpeed[CamDegree.ZOOM.ordinal()] * accelPeriod[CamDegree.ZOOM.ordinal()] * delta;
		rot += maxSpeed[CamDegree.ROT.ordinal()] * accelPeriod[CamDegree.ROT.ordinal()] * delta;

		distance = UtMath.clamp(distance, minValue[CamDegree.ZOOM.ordinal()], maxValue[CamDegree.ZOOM.ordinal()]);
		rot = UtMath.clamp(rot, minValue[CamDegree.ROT.ordinal()], maxValue[CamDegree.ROT.ordinal()]);

		float tilt = UtMath.scalarLimitsInterpolation(distance, minValue[CamDegree.ZOOM.ordinal()], maxValue[CamDegree.ZOOM.ordinal()], UtMath.PI / 22f, UtMath.QUARTER_PI);

		float offX = maxSpeed[CamDegree.SIDE.ordinal()] * accelPeriod[CamDegree.SIDE.ordinal()] * delta;
		float offZ = maxSpeed[CamDegree.FWD.ordinal()] * accelPeriod[CamDegree.FWD.ordinal()] * delta;

		center.x += offX * Math.cos(-rot) + offZ * Math.sin(rot);
		center.z += offX * Math.sin(-rot) + offZ * Math.cos(rot);

		center.x = UtMath.clamp(center.x, minValue[CamDegree.SIDE.ordinal()], maxValue[CamDegree.SIDE.ordinal()]);
		center.z = UtMath.clamp(center.z, minValue[CamDegree.FWD.ordinal()], maxValue[CamDegree.FWD.ordinal()]);

		final float elevation = elevationProvider.getElevationAt(center.x, center.z);
		//float targetY = (int) (Math.ceil(elevation / 2f) * 2f);
		if(elevation < minElevation){
			center.y = minElevation;
		}else {
			center.y = elevation * 0.45f;
		}



		cameraManager.cam.position.x = center.x + (distance * (float)Math.cos(tilt) * (float)Math.sin(rot));
		cameraManager.cam.position.y = center.y + (distance * (float)Math.sin(tilt)); //
		cameraManager.cam.position.z = center.z + (distance * (float)Math.cos(tilt) * (float)Math.cos(rot));

//		final float elevationCam = elevationProvider.getElevationAt(cameraManager.cam.position.x, cameraManager.cam.position.z) + 0.1f;
//		if(cameraManager.cam.position.y < elevationCam){
//			cameraManager.cam.position.y = elevationCam;;
//		}


		//cameraManager.cam.position.y += elevationAt;
		//center.y += elevationAt;

		cameraManager.cam.up.set(Vector3.Y);
		cameraManager.cam.lookAt(center);
		//cameraManager.cam.up.set(Vector3.Y);
		//System.out.println("center: "+center);
		cameraManager.cam.update();

		//cam.setLocation(vec);
		//cam.lookAt(center, Vector3f.UNIT_Y);

	}

	public boolean hasMouseFocus() {
		return dragMouse;
	}

	public String getDebugString()
	{
		String out = "RtsCamController: \n"+
			"\tPosition: "+cameraManager.cam.position+"\n"+
			"\tCenter: "+center+"\n";

		return out;
	}

	@Override
	public boolean keyDown(int keycode) {
		return keyAction(keycode, 1);
	}

	@Override
	public boolean keyUp(int keycode) {
		return keyAction(keycode, -1);
	}

	private boolean keyAction(int keycode, int press)
	{
		//System.out.println("key action: "+keycode);
		switch(keycode)
		{
			case Input.Keys.W:
				direction[CamDegree.FWD.ordinal()] += -press;
				break;
			case Input.Keys.S:
				direction[CamDegree.FWD.ordinal()] += press;
				break;
			case Input.Keys.A:
				direction[CamDegree.SIDE.ordinal()] += -press;
				break;
			case Input.Keys.D:
				direction[CamDegree.SIDE.ordinal()] += press;
				break;
			case Input.Keys.Q:
				direction[CamDegree.ROT.ordinal()] += press;
				break;
			case Input.Keys.E:
				direction[CamDegree.ROT.ordinal()] += -press;
				break;
			case Input.Keys.Z:
				direction[CamDegree.ZOOM.ordinal()] += press;
				break;
			case Input.Keys.X:
				direction[CamDegree.ZOOM.ordinal()] += -press;
				break;

		}

		//if (CamToggleDrag.equals(name) && isPressed) {
		//	dragMouse = !dragMouse;
		//	inputManager.setCursorVisible(!dragMouse);
		//}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private int dragMouseLastScreenX;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE && !dragMouse)
		{
			dragMouse = true;
			dragMouseLastScreenX = screenX;
			Gdx.input.setCursorCatched(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE && dragMouse)
		{
			dragMouse = false;
			Gdx.input.setCursorCatched(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(dragMouse)
		{
			final int deltaScreenX =  dragMouseLastScreenX-screenX;
			dragMouseLastScreenX = screenX;
			rot += maxSpeed[CamDegree.ROT.ordinal()] * 0.02f* deltaScreenX * Gdx.graphics.getDeltaTime();
			//System.out.println("drag mouse: "+deltaScreenX);
			//accelPeriod[CamDegree.ROT.ordinal()] -= 0.15f * deltaScreenX * Gdx.graphics.getDeltaTime();
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		//accelPeriod[CamDegree.ZOOM.ordinal()] += 13 * amount * Gdx.graphics.getDeltaTime();
		distance += maxSpeed[CamDegree.ZOOM.ordinal()] * amount * Gdx.graphics.getDeltaTime();
		return false;
	}

	public static interface ElevationProvider{
		public float getElevationAt(float x, float z);
	}

}
