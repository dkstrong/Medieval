package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.FileManager;
import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by daniel on 11/18/15.
 */
public class Terrain implements RenderableProvider, Disposable {

	private Array<Array<TerrainChunk>> chunkGrid;
	//public int maxVertsPerChunks = 32767;
	//public int maxVertsPerChunks = 1000;

	public final Vector3 corner00 = new Vector3(-250, 0, -250);
	public final Vector3 corner11 = new Vector3(250, 0, 250);
	public final Color color = new Color(0.75f, 0.75f, 0.75f, 1f);
	public float magnitude = 30;

	public float[] fieldData;
	public int fieldWidth = 128;
	public int fieldHeight = 128;
	public int chunkDataMaxWidth = 128;
	public int chunkDataMaxHeight = 128;

	/**
	 * camera to be used for frustum culling of terrain chunks- optional
	 */
	public Camera camera;

	/**
	 * do not modify externally
	 */
	public TerrainLoader.TerrainParameter parameter;
	/**
	 * do not modify externally
	 */
	public Material material;
	/**
	 * Array of disposable resources like textures or meshes the Terrain is responsible for disposing
	 **/
	private final Array<Disposable> disposables = new Array<Disposable>(false, 4, Disposable.class);

	public Terrain() {
		this.parameter = null;
	}

	public void initNewTerrain(String name)
	{
		init(TerrainLoader.getNewTerrainParamter(name));
	}

	public void init(FileHandle terrainFile){
		init(TerrainLoader.loadTerrainParamter(terrainFile));
	}

	//public void init(TerrainLoader.TerrainParameter param) {
	//init(param, true);
	//}
	public void init(TerrainLoader.TerrainParameter param) {

		material = null;
		while(disposables.size >0){
			disposables.removeIndex(0).dispose();
		}

		rebuildTerrain(param);


		//saveTerrain("test");
	}

	public void rebuildTerrain(TerrainLoader.TerrainParameter param){
		// TODO: combine init() with reubildTerrain() so that
		// rebuildTerrain() will intelligently reuse materials/renderables
		// and only build new ones if needed (just like how it will only build a new mesh if the field data cahnged)
		this.parameter = param;

		boolean reconfigureOnly = fieldData!= null && fieldData == parameter.fieldData && fieldWidth == parameter.fieldWidth && fieldHeight == parameter.fieldWidth;

		if(!reconfigureOnly)
		{
			//System.out.println("create new");
			// clear out/dispose any data from the previous init() call
			if(chunkGrid!=null){
				for (Array<TerrainChunk> terrainChunks : chunkGrid) {
					for (TerrainChunk terrainChunk : terrainChunks) {
						terrainChunk.dispose();
					}
				}
				chunkGrid= null;
			}

			if(param.fieldData!=null){
				//UtLog.info("creating heightfield from memory");
				insertFieldData();
			}else if (param.heightmapName != null) {
				//UtLog.info("creating heightfield from "+param.heightmapName);
				loadFieldData(param.heightmapName);
			} else {
				//UtLog.info("creating heightfield from seed "+param.seed);
				generateFieldData(param.seed, param.fieldWidth, param.fieldHeight);
			}

			createHeightField();
			createRenderables();
		}else{
			//System.out.println("reconfigure");
			insertFieldData();
			reconfigureHeightField();
			reconfigureRenderables();
		}

	}

	private void insertFieldData(){
		this.fieldWidth = parameter.fieldWidth;
		this.fieldHeight = parameter.fieldHeight;
		this.fieldData = parameter.fieldData;
	}

	private void loadFieldData(final String heightmapName) {
		Pixmap heightPix;
		if(heightmapName.endsWith(".cim")){
			heightPix = PixmapIO.readCIM(resolve(heightmapName));
		}else{
			heightPix = new Pixmap(resolve(heightmapName));
		}

		this.fieldWidth = heightPix.getWidth();
		this.fieldHeight = heightPix.getHeight();
		fieldData = TerrainChunk.heightColorsToMap(heightPix.getPixels(), heightPix.getFormat(), fieldWidth, fieldHeight);
		heightPix.dispose();
	}

	private void generateFieldData(final long seed, final int fieldWidth, final int fieldHeight) {
		// max smooth: 181 x 181
		// max unsmooth: 128 x 128

		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		fieldData = new float[fieldWidth * fieldHeight];

		final double featureSize = 20d;
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		for (int x = 0; x < fieldWidth; x++) {
			for (int y = 0; y < fieldHeight; y++) {

				fieldData[y * fieldWidth + x] = UtMath.scalarLimitsInterpolation((float) noise.eval(x / featureSize, y / featureSize), -1f, 1f, 0f, 1f);
				//data[y * fieldWidth + x] = 1;
			}
		}
	}



	private void createHeightField() {

		corner00.set(-parameter.scale, 0, -parameter.scale);
		corner11.set(parameter.scale, 0, parameter.scale);
		magnitude = parameter.magnitude;
		chunkDataMaxWidth = parameter.chunkWidth;
		chunkDataMaxHeight = parameter.chunkHeight;

		float heightRatio = fieldHeight / (float) fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;
		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = false; // TODO: this should be passed in from the parameter, in editor mode isStatic = false, otherwise isStatic = true
		final boolean smooth = true;
		chunkGrid = new Array<Array<TerrainChunk>>(true, 1, Array.class);
		TerrainChunk chunk = new TerrainChunk(isStatic, fieldWidth, fieldHeight, smooth, vertexAttributes);
		chunk.terrain = this;
		chunk.gridX = 0;
		chunk.gridY = 0;
		chunk.fieldStartX = 0;
		chunk.fieldStartY = 0;
		chunk.fieldEndX = fieldWidth;
		chunk.fieldEndY = fieldHeight;
		chunk.set(fieldData);
		putTerrainChunk(0, 0, chunk);
		chunk.configureField(corner00.x, corner00.z, corner11.x, corner11.z, color, magnitude);

	}

	private void reconfigureHeightField(){
		corner00.set(-parameter.scale, 0, -parameter.scale);
		corner11.set(parameter.scale, 0, parameter.scale);
		magnitude = parameter.magnitude;
		chunkDataMaxWidth = parameter.chunkWidth;
		chunkDataMaxHeight = parameter.chunkHeight;

		float heightRatio = fieldHeight / (float) fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;
		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = false;
		final boolean smooth = true;
		TerrainChunk chunk = chunkGrid.get(0).get(0);
		chunk.terrain = this;
		chunk.gridX = 0;
		chunk.gridY = 0;
		chunk.fieldStartX = 0;
		chunk.fieldStartY = 0;
		chunk.fieldEndX = fieldWidth;
		chunk.fieldEndY = fieldHeight;
		chunk.set(fieldData);
		chunk.configureField(corner00.x, corner00.z, corner11.x, corner11.z, color, magnitude);

	}


	public TerrainChunk getTerrainChunk(int x, int y) {
		return chunkGrid.get(x).get(y);
	}

	private void putTerrainChunk(int x, int y, TerrainChunk chunk) {
		Array<TerrainChunk> terrainChunks;
		if (chunkGrid.size > x) {
			terrainChunks = chunkGrid.get(x);
		} else {
			terrainChunks = new Array<TerrainChunk>(true, 4, TerrainChunk.class);
			chunkGrid.add(terrainChunks);
			if (chunkGrid.get(x) != terrainChunks)
				throw new IllegalStateException("Chunks not added in correct order (x)");
		}

		terrainChunks.add(chunk);
		if (terrainChunks.get(y) != chunk)
			throw new IllegalStateException("Chunks not added in correct order (y)");
	}


	private void createRenderables() {

		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.createRenderable(getMaterial(terrainChunk));
			}
		}
	}

	private void reconfigureRenderables(){
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.renderable.meshPart.update();
			}
		}
	}

	private FileHandle resolve(String fileName) {
		return FileManager.moddable(fileName);
	}

	private Material getMaterial(TerrainChunk terrainChunk) {
		if (material != null)
			return material;

		//UtLog.info("create new terrain material");

		TerrainLoader.TerrainParameter param = this.parameter;

		Texture weightMap1 = new Texture(resolve(param.weightMap1));

		Texture tex1 = new Texture(resolve(param.tex1));
		Texture tex2 = new Texture(resolve(param.tex2));
		Texture tex3 = new Texture(resolve(param.tex3));
		tex3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		Texture tex4 = new Texture(resolve(param.tex4));
		tex4.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		Texture texMask1 = new Texture(resolve("Textures/mask.png"));

		Material mat = new Material(
			new TextureAttribute(TextureAttribute.Diffuse, weightMap1), // fool the shader into thinking we have a diffuse map for now, less hacking
			new TerrainTextureAttribute(TerrainTextureAttribute.WeightMap1, weightMap1),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex1, tex1, param.tex1Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex2, tex2, param.tex2Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex3, tex3, param.tex3Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex4, tex4, param.tex4Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.TexMask1, texMask1, param.tex3Scale)
		);

		disposables.add(weightMap1);
		disposables.add(tex1);
		disposables.add(tex2);
		disposables.add(tex3);
		disposables.add(tex4);
		disposables.add(texMask1);



		this.material = mat;
		return mat;
	}

	public Texture getMaterialAttribute(long attributeId)
	{
		if (material == null)
			throw new IllegalStateException("Can not set material attribute before Terrain has been initialized");
		Attribute attribute = material.get(attributeId);
		if(attribute instanceof  TerrainTextureAttribute){
			TerrainTextureAttribute terrainTextureAttribute = (TerrainTextureAttribute) attribute;
			return terrainTextureAttribute.textureDescription.texture;
		}else if(attribute !=null){
			throw new IllegalArgumentException("provided attribute id is not a TerrainTextureAttribute");
		}
		return null;
	}


	public void setOverrideMaterialAttribute(long attributeId, Texture texture, float textureScale){
		Attribute attribute = material.get(attributeId);

		if (attribute instanceof TerrainTextureAttribute) {
			TerrainTextureAttribute terrainTextureAttribute = (TerrainTextureAttribute) attribute;
			Texture oldTex = terrainTextureAttribute.textureDescription.texture;
			if (disposables.removeValue(oldTex, true)) {
				oldTex.dispose();
			}

			attribute = null;
		}

		Texture newTex = texture;
		disposables.add(newTex);
		TerrainTextureAttribute terrainTextureAttribute = new TerrainTextureAttribute(attributeId,newTex,textureScale);
		material.set(terrainTextureAttribute);

		// doesnt change parameter because this is just to swap out the texture to an unmanaged texture

	}

	public void setMaterialAttribute(long attributeId, String textureLocation, float textureScale) {
		if (material == null)
			throw new IllegalStateException("Can not set material attribute before Terrain has been initialized");

		Attribute attribute = material.get(attributeId);

		if (attribute instanceof TerrainTextureAttribute) {
			TerrainTextureAttribute terrainTextureAttribute = (TerrainTextureAttribute) attribute;
			Texture oldTex = terrainTextureAttribute.textureDescription.texture;
			if (disposables.removeValue(oldTex, true)) {
				oldTex.dispose();
			}

			attribute = null;
		}

		Texture newTex = new Texture(resolve(textureLocation));
		disposables.add(newTex);
		TerrainTextureAttribute terrainTextureAttribute = new TerrainTextureAttribute(attributeId,newTex,textureScale);
		material.set(terrainTextureAttribute);

		setTexParameter(attributeId, textureLocation, textureScale);
	}

	public void removeMaterialAttribute(long attributeId){
		if (material == null)
			throw new IllegalStateException("Can not set material attribute before Terrain has been initialized");
		material.remove(attributeId);

		setTexParameter(attributeId, null, 1f);
	}

	private void setTexParameter(long attributeId, String tex, float texScale){
		if(attributeId == TerrainTextureAttribute.WeightMap1){
			parameter.weightMap1 = tex;
		}else if(attributeId == TerrainTextureAttribute.Tex1){
			parameter.tex1 = tex;
			parameter.tex1Scale = texScale;
		}else if(attributeId == TerrainTextureAttribute.Tex2){
			parameter.tex2 = tex;
			parameter.tex2Scale = texScale;
		}else if(attributeId == TerrainTextureAttribute.Tex3){
			parameter.tex3 = tex;
			parameter.tex3Scale = texScale;
		}else if(attributeId == TerrainTextureAttribute.Tex4){
			parameter.tex4 = tex;
			parameter.tex4Scale = texScale;
		}
	}





	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate, Vector3 store) {
		// TODO: has same issue as getElevation(), the calcualted fieldX and fieldY values are wrong
		// when there is more than 1 chunk
		float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, corner00.x, corner11.x, 0, fieldWidth - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, corner00.z, corner11.z, 0, fieldHeight - 1);

		int gridX = (int) (fieldX / (float) chunkDataMaxWidth);
		int gridY = (int) (fieldY / (float) chunkDataMaxHeight);

		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		float chunkX = fieldX - (gridX * chunkDataMaxWidth);
		float chunkY = fieldY - (gridY * chunkDataMaxHeight);

		//int x0 = Math.round(fieldX);
		//int y0 = Math.round(fieldY) ;
		int x0 = (int) chunkX;
		int y0 = (int) chunkY;
		return chunk.getWeightedNormalAt(x0, y0, store);
	}

	public Vector3 getWorldCoordinate(int fieldX, int fieldY, Vector3 store) {
		if (fieldX >= fieldWidth) fieldX = fieldWidth - 1;
		if (fieldY >= fieldHeight) fieldY = fieldHeight - 1;
		int gridX = (int) (fieldX / (float) chunkDataMaxWidth);
		int gridY = (int) (fieldY / (float) chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		int chunkX = fieldX - (gridX * chunkDataMaxWidth);
		int chunkY = fieldY - (gridY * chunkDataMaxHeight);
		return chunk.getWorldCoordinate(chunkX, chunkY, store);

	}

	public Vector3 getWorldCoordinate(float fieldX, float fieldY, Vector3 store) {
		if (fieldX >= fieldWidth) fieldX = fieldWidth - 1;
		if (fieldY >= fieldHeight) fieldY = fieldHeight - 1;
		int gridX = (int) (fieldX / (float) chunkDataMaxWidth);
		int gridY = (int) (fieldY / (float) chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		float chunkX = fieldX - (gridX * chunkDataMaxWidth);
		float chunkY = fieldY - (gridY * chunkDataMaxHeight);
		return chunk.getWorldCoordinate(chunkX, chunkY, store);

	}

	public Vector2 getFieldCoordiante(Vector3 worldCoordinate, Vector2 store){
		store.x = UtMath.scalarLimitsInterpolation(worldCoordinate.x, corner00.x, corner11.x, 0, fieldWidth - 1);
		store.y = UtMath.scalarLimitsInterpolation(worldCoordinate.z, corner00.z, corner11.z, 0, fieldHeight - 1);
		return store;
	}

	public float getElevation(Vector3 worldCoordinate) {
		return getElevation(worldCoordinate.x, worldCoordinate.z);
	}

	public float getElevation(float worldX, float worldZ) {
		//TODO: this only works properly if thres only 1 terrain chunk
		// the calcualted fieldX and fieldY values are wrong if there is more than 1 chunk
		// I need to figure out how to back in to these values.
		// convert world coordinates to field coordinates
		float fieldX = UtMath.scalarLimitsInterpolation(worldX, corner00.x, corner11.x, 0, fieldWidth - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldZ, corner00.z, corner11.z, 0, fieldHeight - 1);

		int gridX = (int) (fieldX / (float) chunkDataMaxWidth);
		int gridY = (int) (fieldY / (float) chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);

		float chunkX = fieldX - (gridX * chunkDataMaxWidth);
		float chunkY = fieldY - (gridY * chunkDataMaxHeight);

		return chunk.getElevation(chunkX, chunkY);
	}

	public boolean intersect(Ray ray, Vector3 store) {
		// TODO: seems to do some false negatives with mutliple chunks when the terrain formes sort of a  concave
		// (ie tall hill). Ithink intersectRayTriangles exits out too early after hitting the backside
		// of a normal or something..
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if (Intersector.intersectRayTriangles(ray, terrainChunk.vertices, terrainChunk.indices, terrainChunk.stride, store))
					return true;
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.dispose();
			}
		}

		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {

				if (camera == null || camera.frustum.boundsInFrustum(
					terrainChunk.renderable.meshPart.center.x,
					terrainChunk.renderable.meshPart.center.y,
					terrainChunk.renderable.meshPart.center.z,
					terrainChunk.renderable.meshPart.halfExtents.x,
					terrainChunk.renderable.meshPart.halfExtents.y,
					terrainChunk.renderable.meshPart.halfExtents.z

				)) {
					renderables.add(terrainChunk.renderable);
				}

			}
		}
	}

	private void createHeightFieldMultipleChunks() {

		float heightRatio = fieldHeight / (float) fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;

		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		//final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		chunkGrid = new Array<Array<TerrainChunk>>(true, 4, Array.class);
		int chunk = 0;
		int gridX = 0;
		int gridY = 0;
		for (int chunkStartX = 0; chunkStartX < fieldWidth; chunkStartX += chunkDataMaxWidth) {
			gridY = 0;
			for (int chunkStartY = 0; chunkStartY < fieldHeight; chunkStartY += chunkDataMaxHeight) {
				int chunkEndX = UtMath.smallest(chunkStartX + chunkDataMaxWidth + 1, fieldWidth);
				int chunkEndY = UtMath.smallest(chunkStartY + chunkDataMaxHeight + 1, fieldHeight);
				int chunkWidth = chunkEndX - chunkStartX;
				int chunkHeight = chunkEndY - chunkStartY;
				final float[] chunkData = new float[chunkWidth * chunkHeight];
				int chunkX = 0;
				int chunkY = 0;
				for (int x = chunkStartX; x < chunkEndX; x++) {
					chunkY = 0;
					for (int y = chunkStartY; y < chunkEndY; y++) {
						chunkData[chunkY * chunkWidth + chunkX] = fieldData[y * fieldWidth + x];
						chunkY++;
					}
					chunkX++;
				}


				//System.out.println("creating chunk: "+chunk);
				TerrainChunk terrainChunk = new TerrainChunk(isStatic, chunkWidth, chunkHeight, smooth, vertexAttributes);
				terrainChunk.terrain = this;
				terrainChunk.gridX = gridX;
				terrainChunk.gridY = gridY;
				terrainChunk.fieldStartX = chunkStartX;
				terrainChunk.fieldStartY = chunkStartY;
				terrainChunk.fieldEndX = chunkEndX;
				terrainChunk.fieldEndY = chunkEndY;
				terrainChunk.set(chunkData);

				chunk++;
				putTerrainChunk(gridX, gridY, terrainChunk);
				gridY++;

			}

			gridX++;
		}

		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				// TODO: i think this is what is causing slight "stretching" of the diffuse texture
				// and is probably also why the elevation finding code seems to be slightly off
				// also if you use scalarLimitsExtrapolate instead of Interpolate, then
				// get the weighted normal of the highest (in Y) edge- youll get an index
				// out of bounds error.
				// I think this is because chunks need one extra row/col of verticies to connect
				// chunks, i dont think this properly accounts for that... (ie somekind of +1 or -1 thing)
				float chunkWorldStartX = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldStartX + terrainChunk.gridX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldStartY = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldStartY + terrainChunk.gridY, 0, fieldHeight, corner00.z, corner11.z);
				float chunkWorldEndX = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldEndX + terrainChunk.gridX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldEndY = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldEndY + terrainChunk.gridY, 0, fieldHeight, corner00.z, corner11.z);

				terrainChunk.configureField(chunkWorldStartX, chunkWorldStartY, chunkWorldEndX, chunkWorldEndY, color, magnitude);
			}
		}


	}

	/**
	 * if the value of store = the provided world coordinate- then that means
	 * getWorldCoordinate() is working right... I put this here when trying to debug
	 * terrain with multiple chunks
	 * @param worldCoordinate
	 * @param store
	 * @return
	 */
	public Vector3 verifyWorldCoordinateSlow(Vector3 worldCoordinate, Vector3 store) {
		// convert world coordinates to field coordinates
		float worldX = worldCoordinate.x;
		float worldZ = worldCoordinate.z;
		store.set(worldX, 0, worldZ);
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if (UtMath.isBetween(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x) && UtMath.isBetween(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z)) {

					float fieldX = UtMath.scalarLimitsInterpolation(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x, terrainChunk.fieldStartX, terrainChunk.fieldEndX - 1);
					float fieldY = UtMath.scalarLimitsInterpolation(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z, terrainChunk.fieldStartY, terrainChunk.fieldEndY - 1);
					return getWorldCoordinate(fieldX, fieldY, store);
				}
			}
		}

		throw new IllegalStateException("no coord found for: " + UtMath.round(worldCoordinate, 2));
	}

	/**
	 * this elevation finder is kind of "brute force", but it works even with multiple chunks
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	public float getElevationSlow(float worldX, float worldZ) {
		Vector3 store = new Vector3();
		store.set(worldX, 0, worldZ);
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if (UtMath.isBetween(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x) && UtMath.isBetween(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z)) {

					float fieldX = UtMath.scalarLimitsInterpolation(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x, terrainChunk.fieldStartX, terrainChunk.fieldEndX - 1);
					float fieldY = UtMath.scalarLimitsInterpolation(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z, terrainChunk.fieldStartY, terrainChunk.fieldEndY - 1);
					return getWorldCoordinate(fieldX, fieldY, store).y;
				}
			}
		}
		UtLog.error("could not find elevation, returning zero");
		return 0;

	}

}
