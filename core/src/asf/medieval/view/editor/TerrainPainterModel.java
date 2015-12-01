package asf.medieval.view.editor;

import asf.medieval.painter.PainterModel;
import asf.medieval.painter.PixmapPainter;
import asf.medieval.terrain.Terrain;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainPainterModel implements PainterModel {

	TerrainHeightMapUi terrainHeightMapUi;
	private PixmapPainter painter;
	public Pixmap pixmap;
	public float[] fieldData;

	public TerrainPainterModel(TerrainHeightMapUi terrainHeightMapUi) {
		this.terrainHeightMapUi = terrainHeightMapUi;
		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;

		pixmap = new Pixmap(terrain.fieldWidth, terrain.fieldHeight, Pixmap.Format.RGBA8888);
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		this.fieldData = terrain.fieldData;



		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				float data = fieldData[y * terrain.fieldWidth + x];
				pixmap.drawPixel(x, y, Color.rgba8888(data,data,data,data));
			}
		}

	}

	@Override
	public void setPainter(PixmapPainter painter) {
		this.painter = painter;
		painter.pixmap = pixmap;

	}

	@Override
	public void output() {
		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;
		terrain.createTerrain(terrain.parameter, painter.pixmap);
		//terrainHeightMapUi.terrainEditorView.refreshHeightMapWeightMapPainters();
		//terrainHeightMapUi.refreshHeightMapUi();
	}

	@Override
	public int getWidth() {
		return pixmap.getWidth();
	}

	@Override
	public int getHeight() {
		return pixmap.getHeight();
	}

	@Override
	public void dispose() {
		pixmap.dispose();
	}
}
