package asf.medieval.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class CameraManager {


	public final PerspectiveCamera cam;

	public RtsCamController rtsCamController;

	private View chaseTarget;

	public CameraManager(MedievalWorld world) {

		cam = new PerspectiveCamera(52, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//cam.vec.set(0, 11.5f, 25f);  // 50 fov
		//cam.lookAt(0, 9.5f, -20);

		rtsCamController = new RtsCamController(this, world);
		// TODO: camManager needs to act as a middleman for the Cam controllers
		// so if the camera type changes so will the relevant input processing...
	}

	public void resize(int width, int height){
		//System.out.println("cam resize: "+width+"x"+height);
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.near = .1f;
		cam.far = 800f;

		cam.fieldOfView = 52;
		//cam.up.set(Vector3.Y);
		cam.position.set(0, 13.5f, 29f);
		//cam.vec.set(-38, 22f, -9);
		cam.lookAt(0, 0, 0);


		cam.update();
	}

	public void update(float delta){

		rtsCamController.update(delta);

		//System.out.println("cam pos: "+cam.vec);
	}

}
