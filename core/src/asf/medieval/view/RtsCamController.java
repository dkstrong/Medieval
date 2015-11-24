package asf.medieval.view;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class RtsCamController implements InputProcessor {

	public static final int SIDE = 0;
	public static final int FWD = 1;
	public static final int ROT = 2;
	public static final int TILT = 3;
	public static final int ZOOM = 4;

	private final CameraManager cameraManager;
	private final ElevationProvider elevationProvider;

	private boolean dragMouse = false;
	//private Listener listener;
	private int[] direction = new int[5];
	private float[] accelPeriod = new float[5];
	private float[] maxSpeed = new float[5];
	private float[] maxAccelPeriod = new float[5];
	private float[] minValue = new float[5];
	private float[] maxValue = new float[5];
	private float sideSpeedMin = 24f;
	private float sideSpeedMax = 24f*2f*2f*2f*2f*2f;
	public final Vector3 center = new Vector3();
	public float rot = 0;
	public float tilt = 0.4172428f; //UtMath.PI/8f;
	public float distance = 45;
	public float minElevation =0;

	public RtsCamController(CameraManager cameraManager, ElevationProvider elevationProvider) {
		this.cameraManager = cameraManager;
		this.elevationProvider = elevationProvider;

		setMinMaxValues(FWD, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(SIDE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(ROT, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setMinMaxValues(TILT, UtMath.PI / 42f, UtMath.PI/3f);
		setMinMaxValues(ZOOM, 4, 300);

		setMaxSpeed(FWD, 24f * 2f*2f, 0.25f);
		setMaxSpeed(SIDE, 24f*2f*2f, 0.25f);
		setMaxSpeed(ROT, 2f, 0.3f);
		setMaxSpeed(TILT, 2f, 0.3f);
		setMaxSpeed(ZOOM, 60f, 0.25f);

		minElevation = 1;
	}

	public void printCamValues(){
		RtsCamController rtsCamController = this;

		String out =
			"rtsCamController.center.set("+center.x+"f,"+center.y+"f,"+center.z+"f);" +
			"\nrtsCamController.rot = "+rtsCamController.rot+"f;" +
			"\nrtsCamController.tilt = "+rtsCamController.tilt+"f;"+
			"\nrtsCamController.distance = "+rtsCamController.distance+"f;";

		System.out.println(out);
	}

	// SIDE and FWD min/max values are ignored, need to fix this..
	public final void setMinMaxValues(int dg, float min, float max) {
		minValue[dg] = min;
		maxValue[dg] = max;
	}

	public final void setMaxSpeed(int dg, float maxSpeed, float accelerationTime) {
		this.maxSpeed[dg] = maxSpeed / accelerationTime;
		this.maxAccelPeriod[dg] = accelerationTime;
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
		distance += maxSpeed[ZOOM] * accelPeriod[ZOOM] * delta;
		rot += maxSpeed[ROT] * accelPeriod[ROT] * delta;
		tilt += maxSpeed[TILT] * accelPeriod[TILT] * delta;

		distance = UtMath.clamp(distance, minValue[ZOOM], maxValue[ZOOM]);
		rot = UtMath.clamp(rot, minValue[ROT], maxValue[ROT]);
		tilt = UtMath.clamp(tilt, minValue[TILT], maxValue[TILT]);

		// this tilts based on what the zoom level is
		//tilt = UtMath.scalarLimitsInterpolation(distance, minValue[CamDegree.ZOOM.ordinal()], maxValue[CamDegree.ZOOM.ordinal()], UtMath.PI / 22f, UtMath.QUARTER_PI);
		//System.out.println(tilt);

		final float realMaxSpeed = UtMath.scalarLimitsInterpolation(distance, minValue[ZOOM],maxValue[ZOOM],maxSpeed[SIDE]*0.1f, maxSpeed[SIDE]);

//		float offX = maxSpeed[SIDE] * accelPeriod[SIDE] * delta;
//		float offZ = maxSpeed[FWD] * accelPeriod[FWD] * delta;
		float offX = realMaxSpeed * accelPeriod[SIDE] * delta;
		float offZ = realMaxSpeed * accelPeriod[FWD] * delta;

		final float sinRot = (float)Math.sin(rot);
		final float cosRot = (float)Math.cos(rot);
		final float cosTilt = (float)Math.cos(tilt);
		final float sinTilt = (float)Math.sin(tilt);

		center.x += offX * cosRot + offZ * sinRot; // cos(rot) == cos(-rot)
		center.z += offX * -sinRot+ offZ * cosRot;

		center.x = UtMath.clamp(center.x, minValue[SIDE], maxValue[SIDE]);
		center.z = UtMath.clamp(center.z, minValue[FWD], maxValue[FWD]);

		/*
		final float elevation = elevationProvider.getElevationAt(center.x, center.z);
		//float targetY = (int) (Math.ceil(elevation / 2f) * 2f);
		if(elevation < minElevation){
			center.y = minElevation;
		}else {
			center.y = elevation * 0.45f;
		}
		*/
		center.y = minElevation;



		cameraManager.cam.position.x = center.x + (distance * cosTilt * sinRot);
		cameraManager.cam.position.y = center.y + (distance * sinTilt);
		cameraManager.cam.position.z = center.z + (distance * cosTilt * cosRot);

//		final float elevationCam = elevationProvider.getElevationAt(cameraManager.cam.position.x, cameraManager.cam.position.z) + minElevation*0.45f;
//		if(cameraManager.cam.position.y < elevationCam){
//			cameraManager.cam.position.y = elevationCam;
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
				direction[FWD] += -press;
				break;
			case Input.Keys.S:
				direction[FWD] += press;
				break;
			case Input.Keys.A:
				direction[SIDE] += -press;
				break;
			case Input.Keys.D:
				direction[SIDE] += press;
				break;
			case Input.Keys.Q:
				//direction[ROT] += press;
				break;
			case Input.Keys.E:
				//direction[ROT] += -press;
				break;
			case Input.Keys.Z:
				//direction[ZOOM] += press;
				break;
			case Input.Keys.X:
				//direction[ZOOM] += -press;
				break;
			case Input.Keys.V:
				//direction[TILT] += press;
				break;
			case Input.Keys.C:
				//direction[TILT] += -press;
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
	private int dragMouseLastScreenY;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE && !dragMouse)
		{
			dragMouse = true;
			dragMouseLastScreenX = screenX;
			dragMouseLastScreenY = screenY;
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
			final int deltaScreenY =  dragMouseLastScreenY-screenY;
			dragMouseLastScreenX = screenX;
			dragMouseLastScreenY = screenY;
			rot += maxSpeed[ROT] * 0.01f* deltaScreenX * Gdx.graphics.getDeltaTime();
			//System.out.println("drag mouse: "+deltaScreenX);
			//accelPeriod[CamDegree.ROT.ordinal()] -= 0.15f * deltaScreenX * Gdx.graphics.getDeltaTime();

			tilt += maxSpeed[TILT] * 0.01f* -deltaScreenY * Gdx.graphics.getDeltaTime();
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
		distance += maxSpeed[ZOOM] * amount * Gdx.graphics.getDeltaTime();
		return false;
	}

	public static interface ElevationProvider{
		public float getElevationAt(float x, float z);
	}

}
