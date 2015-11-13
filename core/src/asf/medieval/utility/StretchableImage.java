package asf.medieval.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class StretchableImage extends Actor {

	private Drawable drawable;

	public StretchableImage(Skin skin, String drawableName) {
		drawable = skin.getDrawable(drawableName);
	}

	public StretchableImage(TextureAtlas.AtlasRegion region) {

		if (region.splits != null) {
			final int[] splits = region.splits;
			NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
			final int[] pads = region.pads;
			if (pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
			drawable = new NinePatchDrawable(patch);
		} else{
			drawable = new TextureRegionDrawable(region);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		drawable.draw(batch, getX(), getY(), getWidth(), getHeight());
	}
}
