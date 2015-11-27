package asf.medieval.view;

import asf.medieval.model.Command;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by daniel on 11/20/15.
 */
public class HudCommandView implements View,InputProcessor {
	private final MedievalWorld world;

	private final Decal moveCommandDecal = new Decal();

	private final Vector3 vec1 = new Vector3();
	private final Vector3 lastMoveCommandLocation = new Vector3();

	private boolean spacebarDown = false;
	private float forceSpacebarTimer;

	public HudCommandView(MedievalWorld world) {
		this.world = world;
		moveCommandDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		moveCommandDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		moveCommandDecal.setDimensions(2, 2);
		moveCommandDecal.setColor(1, 1, 1, 1);
		moveCommandDecal.rotateX(-90);
	}

	public void resize(int width, int height) {


	}

	@Override
	public void update(float delta){
		forceSpacebarTimer -= delta;
	}

	@Override
	public void render(float delta)
	{
		if(spacebarDown || forceSpacebarTimer>0)
		{
			moveCommandDecal.setPosition(lastMoveCommandLocation);
			world.scenario.terrain.getWeightedNormalAt(lastMoveCommandLocation, vec1);
			moveCommandDecal.setRotation(vec1, Vector3.Y);
			vec1.scl(0.11f);
			moveCommandDecal.translate(vec1);

			world.decalBatch.add(moveCommandDecal);
		}
	}

	public Ray getWorldCoord(float screenX, float screenY, Vector3 storeWorldCoord)
	{
		Ray ray = world.cameraManager.cam.getPickRay(screenX, screenY);


		// this just gets the intersection point of the ray to the ground plane (y==0)
		//final float distance = -ray.origin.y / ray.direction.y;
		//storeWorldCoord.set(ray.direction).scl(distance).add(ray.origin);


		world.scenario.terrain.intersect(ray, storeWorldCoord);


		return ray;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}



	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.RIGHT)
		{
			if(world.hudView.hudSelectionView.selectedViews.size > 0)
			{
				forceSpacebarTimer = 3f;
				getWorldCoord(screenX, screenY, lastMoveCommandLocation);
				//System.out.println(lastMoveCommandLocation);

				for (SelectableView sgo : world.hudView.hudSelectionView.selectedViews) {
					if(sgo instanceof InfantryView)
					{
						Command command = new Command();
						command.tokenId = ((InfantryView) sgo).token.id;
						command.location = UtMath.toVector2(lastMoveCommandLocation, new Vector2());
						world.gameClient.sendCommand(command);
						//sgo.token.setTarget(lastMoveCommandLocation);
					}

				}

			}

			return true;
		}
		return false;
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode)
		{
			case Input.Keys.SPACE:
				spacebarDown = true;
				break;

		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode)
		{
			case Input.Keys.SPACE:
				spacebarDown = false;
				break;
			case Input.Keys.I:
				Command command = new Command();
				command.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
				world.gameClient.sendCommand(command);
				return true;
			case Input.Keys.J:
				Command jimmyCommand = new Command();
				jimmyCommand.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
				jimmyCommand.jimmy = true;
				world.gameClient.sendCommand(jimmyCommand);
				return true;
			case Input.Keys.B:
				Command buildCommand = new Command();
				buildCommand.location = UtMath.toVector2(world.cameraManager.rtsCamController.center, new Vector2());
				buildCommand.structure = true;
				world.gameClient.sendCommand(buildCommand);
				return true;
			case Input.Keys.C:
				world.cameraManager.rtsCamController.printCamValues();
				return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}




}
