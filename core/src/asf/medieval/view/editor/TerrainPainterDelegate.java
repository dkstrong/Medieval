package asf.medieval.view.editor;

import asf.medieval.painter.HistoryState;
import asf.medieval.painter.PainterDelegate;
import asf.medieval.painter.PixmapPainter;
import asf.medieval.painter.Point;
import asf.medieval.terrain.Terrain;
import asf.medieval.terrain.TerrainChunk;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/30/15.
 */
public class TerrainPainterDelegate implements PainterDelegate {

	TerrainHeightMapUi terrainHeightMapUi;
	private PixmapPainter painter;
	public Pixmap pixmap;
	public float[] fieldData;
	public float fieldWidth;
	public float fieldHeight;

	public TerrainPainterDelegate(TerrainHeightMapUi terrainHeightMapUi) {
		this.terrainHeightMapUi = terrainHeightMapUi;
		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;

		pixmap = new Pixmap(terrain.fieldWidth, terrain.fieldHeight, Pixmap.Format.RGBA8888);
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		this.fieldData = terrain.fieldData;
		this.fieldWidth = terrain.fieldWidth;
		this.fieldHeight = terrain.fieldHeight;


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

	}

	@Override
	public HistoryState createHistory() {
		return new HistoryState(pixmap);
	}

	@Override
	public HistoryState createHistory(HistoryState currentState, Array<Point> affectedPixels) {
		return new HistoryState(currentState, affectedPixels);
	}

	@Override
	public void recall(HistoryState historyState) {

		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for(int x=0; x<historyState.pixelData.length; x++){
			for(int y=0; y<historyState.pixelData[0].length; y++){
				pixmap.drawPixel(x, y, historyState.pixelData[x][y]);
			}
		}
	}

	@Override
	public void recall(HistoryState historyState, Array<Point> affectedPixels) {
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (Point affectedPixel : affectedPixels) {
			pixmap.drawPixel(affectedPixel.x, affectedPixel.y, historyState.pixelData[affectedPixel.x][affectedPixel.y]);
		}
	}

	@Override
	public int drawPoint(Color brushColor, int x, int y, float opacity) {
		Color c;
		if (opacity < 1f) {
			c = new Color();
			Color.rgba8888ToColor(c, pixmap.getPixel(x, y));

			c.r = opacity * brushColor.r + (1 - opacity) * c.r;
			c.g = opacity * brushColor.g + (1 - opacity) * c.g;
			c.b = opacity * brushColor.b + (1 - opacity) * c.b;
			c.a = opacity * brushColor.a + (1 - opacity) * c.a;
			//UtDebugPrint.print(c);
		} else {
			c = brushColor;
		}

		int colorCode = Color.rgba8888(c);
		pixmap.drawPixel(x, y, colorCode);
		return colorCode;
	}

	@Override
	public int erasePoint(Color brushColor, int x, int y, float opacity) {
		Color c;
		c = new Color();
		Color.rgba8888ToColor(c, pixmap.getPixel(x, y));

		c.r = UtMath.largest((1 - opacity) * c.r - opacity * brushColor.r, 0f);
		c.g = UtMath.largest((1 - opacity) * c.g - opacity * brushColor.g, 0f);
		c.b = UtMath.largest((1 - opacity) * c.b - opacity * brushColor.b, 0f);
		c.a = UtMath.largest((1 - opacity) * c.a - opacity * brushColor.a, 0f);

		int colorCode = Color.rgba8888(c);

		pixmap.drawPixel(x, y, colorCode);
		return colorCode;
	}

	@Override
	public void output() {
		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;
		fieldData = TerrainChunk.heightColorsToMap(pixmap.getPixels(), pixmap.getFormat(), pixmap.getWidth(), pixmap.getHeight());
		terrain.parameter.fieldData = fieldData;
		terrain.createTerrain(terrain.parameter);
		//terrainHeightMapUi.terrainEditorView.refreshHeightMapWeightMapPainters();
		//terrainHeightMapUi.refreshHeightMapUi();
	}

	@Override
	public void output(FileHandle fh)
	{
		PixmapIO.writePNG(fh, pixmap);
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
