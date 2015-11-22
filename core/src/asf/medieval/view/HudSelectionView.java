package asf.medieval.view;

import asf.medieval.model.Token;
import asf.medieval.utility.StretchableImage;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/20/15.
 */
public class HudSelectionView implements View,InputProcessor {
	private final MedievalWorld world;
	private StretchableImage selectionBox;

	public Array<SelectableView> selectedViews = new Array<SelectableView>(true, 8, SelectableView.class);

	public HudSelectionView(MedievalWorld world) {
		this.world = world;
		selectionBox = new StretchableImage(world.pack.findRegion("Interface/Hud/selection-box"));
	}

	public void resize(int width, int height) {

	}

	@Override
	public void update(float delta){
		for (SelectableView selectedView : selectedViews) {
			if(!canSelectToken(selectedView.getToken()))
			{
				selectedView.setSelected(false);
				selectedViews.removeValue(selectedView, true);
			}
		}
	}

	@Override
	public void render(float delta)
	{
		if(mouseLeftDrag)
		{
			if(selectionBox.getParent() == null)
				world.stage.addActor(selectionBox);

			final float x = dragStartCoords.x;
			final float y = Gdx.graphics.getHeight() - dragStartCoords.y;
			final float width = dragEndCoords.x - dragStartCoords.x;
			final float height = dragStartCoords.y - dragEndCoords.y;

			selectionBox.setBounds(x,y,width,height);
		}
		else
		{
			selectionBox.remove();
		}
	}

	private boolean canSelectToken(Token token){
		if(token.damage!=null && token.damage.health <=0) return false;
		if(token.owner.id!= world.gameClient.player.id) return false;

		return true;
	}

	public Vector2 getScreenCord(Vector3 worldCoord, Vector2 storeScreenCoord){
		Vector3 screenCoord = world.cameraManager.cam.project(new Vector3(worldCoord));
		// we flip screenCoord.y because the screen coordinates that come
		// from input listeners use Y down, this will make it match that coordinate
		// system
		storeScreenCoord.set(screenCoord.x, Gdx.graphics.getHeight()-screenCoord.y);
		return storeScreenCoord;
	}

	private final Vector3 terrainIntersectionPoint = new Vector3();
	private boolean mouseLeftDown = false;
	private int mouseLeftDragCount = 0;
	public boolean mouseLeftDrag = false;
	public final Vector2 dragStartCoords = new Vector2();
	public final Vector2 dragEndCoords = new Vector2();
	private final Vector2 tokenScreenCoord = new Vector2();
	private final Vector3 tokenCenterWorldCoord = new Vector3();
	private final Vector2 tokenCenterScreenCoord = new Vector2();
	private final Vector2 dragCorner1 = new Vector2();
	private final Vector2 dragCorner2 = new Vector2();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
		{
			dragStartCoords.set(screenX, screenY);
			mouseLeftDown = true;
			mouseLeftDragCount = 0;
			mouseLeftDrag = false;

		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT)
		{
			mouseLeftDown = false;
			if(!mouseLeftDrag)
			{
				directSelect(screenX,screenY);
			}
			else
			{
				boxSelect(screenX,screenY);
			}

		}
		return false;
	}

	private void directSelect(int screenX, int screenY)
	{
		Ray ray = world.cameraManager.cam.getPickRay(screenX, screenY);
		final boolean intersectsTerrain = world.scenario.terrain.intersect(ray, terrainIntersectionPoint);
		final float groundDst2 = intersectsTerrain ? ray.origin.dst2(terrainIntersectionPoint) : Float.MAX_VALUE;

		SelectableView closestSgo = null;
		float closestDistance = Float.MAX_VALUE; // this distance value is only useful for comparing shapes intersections
		selectedViews.clear();
		for (View view : world.gameObjects) {
			if(view instanceof SelectableView)
			{
				SelectableView sgo = (SelectableView) view;
				sgo.setSelected(false);
				if(canSelectToken(sgo.getToken()))
				{
					float sgoDistance = sgo.intersects(ray);
					if(sgoDistance > 0 && sgoDistance < closestDistance && ray.origin.dst2(sgo.getTranslation()) < groundDst2)
					{
						closestDistance = sgoDistance;
						closestSgo = sgo;
					}
				}
			}
		}

		if(closestSgo!=null){
			closestSgo.setSelected(true);
			selectedViews.add(closestSgo);
		}
	}

	private void boxSelect(int screenX, int screenY){
		mouseLeftDrag = false;
		dragEndCoords.set(screenX, screenY);

		dragCorner1.set(dragEndCoords.x, dragStartCoords.y);
		dragCorner2.set(dragStartCoords.x, dragEndCoords.y);

		selectedViews.clear();
		for (View view : world.gameObjects) {
			if(view instanceof SelectableView)
			{
				SelectableView sgo = (SelectableView) view;
				sgo.setSelected(false);
				if(canSelectToken(sgo.getToken())){
					// TODO: this treats the selectable spot as a point in space, isntead it should be a box
					// TODO: the box should be the same as the tokens shape, but only half the height
					// TODO: so that only the bottom half of the token is box selectable.
					getScreenCord(sgo.getTranslation(), tokenScreenCoord);
					boolean isInSelectionBox = UtMath.isPointInRect(tokenScreenCoord, dragStartCoords, dragCorner1, dragEndCoords, dragCorner2);
					if(isInSelectionBox)
					{
						// TODO: instead of just checking for terrain obstruction, also check
						// TODO: check for obstruction by structures..
						// TODO: will need to make some kind of "terrainish" array that stores
						// TODO: the terrain and structures and anything else that obstructs selection
						tokenCenterWorldCoord.set(sgo.getTranslation()).add(0,sgo.getToken().shape.dimensions.y/2f,0);
						getScreenCord(tokenCenterWorldCoord, tokenCenterScreenCoord);

						Ray ray = world.cameraManager.cam.getPickRay(tokenCenterScreenCoord.x, tokenCenterScreenCoord.y);
						final boolean intersectsTerrain = world.scenario.terrain.intersect(ray, terrainIntersectionPoint);
						final float groundDst2 = intersectsTerrain ? ray.origin.dst2(terrainIntersectionPoint) : Float.MAX_VALUE;
						final float viewDst2 = ray.origin.dst2(tokenCenterWorldCoord);

						if(viewDst2-2f <= groundDst2)
						{
							sgo.setSelected(true);
							if(sgo.isSelected()){
								selectedViews.add(sgo);
							}
						}
						//System.out.println("view: "+viewDst2+", ground: "+groundDst2);


					}
				}

			}
		}
	}


	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(mouseLeftDown)
		{
			if(mouseLeftDragCount++ > 2)
			{
				mouseLeftDrag = true;
				dragEndCoords.set(screenX, screenY);
			}
		}
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
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}




}
