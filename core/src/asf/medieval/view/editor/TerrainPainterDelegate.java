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
	public float[] fieldData;
	public int fieldWidth;
	public int fieldHeight;

	public TerrainPainterDelegate(TerrainHeightMapUi terrainHeightMapUi) {
		this.terrainHeightMapUi = terrainHeightMapUi;

		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;
		this.fieldData = terrain.fieldData;
		this.fieldWidth = terrain.fieldWidth;
		this.fieldHeight = terrain.fieldHeight;

	}

	@Override
	public void setPainter(PixmapPainter painter) {
		this.painter = painter;

	}

	@Override
	public HistoryState createHistory() {
		return new HistoryState(fieldData, fieldWidth, fieldHeight);
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
				fieldData[y*fieldWidth+x] =historyState.pixelData[x][y] / 255.0f;
			}
		}
	}

	@Override
	public void recall(HistoryState historyState, Array<Point> affectedPixels) {
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}

		for (Point affectedPixel : affectedPixels) {
			fieldData[affectedPixel.y*fieldWidth+affectedPixel.x] =historyState.pixelData[affectedPixel.x][affectedPixel.y] / 255.0f;
		}
	}

	@Override
	public int drawPoint(Color brushColor, int x, int y, float opacity) {
		float cr;
		if (opacity < 1f) {
			cr = fieldData[y*fieldWidth+x];
			cr = opacity * brushColor.r + (1 - opacity) * cr;
		} else {
			cr = brushColor.r;
		}

		int colorCode = Color.alpha(cr);
		fieldData[y*fieldWidth+x] = cr;
		return colorCode;
	}

	@Override
	public int erasePoint(Color brushColor, int x, int y, float opacity) {

		float cr;
		cr = fieldData[y*fieldWidth+x];
		cr = (1 - opacity) * cr - opacity * brushColor.r;
		cr = UtMath.largest(cr,0);

		int colorCode = Color.alpha(cr);
		fieldData[y*fieldWidth+x] = cr;
		return colorCode;
	}

	@Override
	public void output() {
		Terrain terrain = terrainHeightMapUi.world.terrainView.terrain;
		terrain.parameter.fieldData = fieldData;
		terrain.createTerrain(terrain.parameter);
		//terrainHeightMapUi.terrainEditorView.refreshHeightMapWeightMapPainters();
		//terrainHeightMapUi.refreshHeightMapUi();
	}

	@Override
	public void output(FileHandle fh)
	{
		Pixmap pixmap = new Pixmap(fieldWidth, fieldHeight, Pixmap.Format.Alpha);
		if (Pixmap.getBlending() != Pixmap.Blending.None) {
			Pixmap.setBlending(Pixmap.Blending.None);
		}
		for(int x=0; x<fieldWidth; x++){
			for(int y=0; y<fieldWidth; y++){
				int colorCode = Color.alpha(fieldData[y*fieldWidth+x]);
				pixmap.drawPixel(x,y,colorCode);
			}
		}
		//PixmapIO.PNG writer = new PixmapIO.PNG();

		//PixmapIO.writePNG(fh, pixmap);
		PixmapIO.writeCIM(fh, pixmap);

		pixmap.dispose();
	}

	@Override
	public int getWidth() {
		return fieldWidth;
	}

	@Override
	public int getHeight() {
		return fieldHeight;
	}

	@Override
	public void dispose() {

	}
}
