package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtMath;
import asf.medieval.utility.UtPixmap;
import asf.medieval.utility.shadertest.ShaderTestAttribute;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


/**
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/assets/loaders/PixmapLoader.java
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/assets/loaders/BitmapFontLoader.java
 * <p/>
 * Created by daniel on 11/18/15.
 */
public class TerrainLoader extends AsynchronousAssetLoader<Terrain, TerrainLoader.TerrainParameter> {

	public TerrainLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {

	}

	@Override
	public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter param) {
		// TODO: apply min/mag
		Terrain terrain = new Terrain();
		terrain.corner00.set(-param.terrainScale, 0, -param.terrainScale);
		terrain.corner11.set(param.terrainScale, 0, param.terrainScale);
		terrain.magnitude = param.terrainMagnitude;
		terrain.chunkDataMaxWidth = param.chunkWidth;
		terrain.chunkDataMaxHeight = param.chunkHeight;

		createFieldData(terrain, param);
		terrain.createRenderables(this, param);
		return terrain;

	}

	private void createFieldData(Terrain terrain, TerrainParameter param) {
		if (param.heightmapName != null) {
			loadFieldData(terrain, param);
		} else {
			generateFieldData(terrain, param);
		}
	}

	private void generateFieldData(Terrain terrain, TerrainParameter param) {
		// max smooth: 181 x 181
		// max unsmooth: 128 x 128

		final int fieldWidth = param.fieldWidth;
		final int fieldHeight = param.fieldHeight;
		terrain.fieldData = new float[fieldWidth * fieldHeight];


		final double featureSize = 20d;
		OpenSimplexNoise noise = new OpenSimplexNoise(param.seed);
		for (int x = 0; x < fieldWidth; x++) {
			for (int y = 0; y < fieldHeight; y++) {

				terrain.fieldData[y * fieldWidth + x] = UtMath.scalarLimitsInterpolation((float) noise.eval(x / featureSize, y / featureSize), -1f, 1f, 0f, 1f);
				//data[y * fieldWidth + x] = 1;
			}
		}

		terrain.fieldWidth = param.fieldWidth;
		terrain.fieldHeight = param.fieldHeight;
		terrain.createHeightField();
	}

	private void loadFieldData(Terrain terrain, TerrainParameter param) {
		Pixmap heightPix = new Pixmap(resolve(param.heightmapName));
		final int fieldWidth = heightPix.getWidth();
		final int fieldHeight = heightPix.getHeight();
		terrain.fieldData = TerrainChunk.heightColorsToMap(heightPix.getPixels(), heightPix.getFormat(), fieldWidth, fieldHeight);
		heightPix.dispose();

		terrain.fieldWidth = fieldWidth;
		terrain.fieldHeight = fieldHeight;
		terrain.createHeightField();
	}

	protected Material getMaterial(TerrainChunk terrainChunk, TerrainParameter param) {
		// TODO; the textures that are loaded here arent stored to be disposed.
		// TODO: I need to dispose them somewhere..
		// TODO: i might also want to try packing these into an atlas and providing
		// TODO: texture regions to the material instead of seperate textures
		Texture diffuseMap = getDiffuseMap(terrainChunk, param);
		Texture tex1 = new Texture(resolve(param.tex1));
		Texture tex2 = new Texture(resolve(param.tex2));
		Texture tex3 = new Texture(resolve(param.tex3));

		Material mat = new Material(
			new TextureAttribute(TextureAttribute.Diffuse, diffuseMap),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex1, tex1, param.tex1Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex2, tex2, param.tex2Scale),
			new TerrainTextureAttribute(TerrainTextureAttribute.Tex3, tex3, param.tex3Scale)
		);


		return mat;
	}

	protected Texture getDiffuseMap(TerrainChunk terrainChunk, TerrainParameter param) {
		if (param.diffusemapName != null) {
			return loadDiffuseMap(param);
		} else {
			return generateDiffuseMap(terrainChunk, param);
		}
	}

	private Texture generateDiffuseMap(TerrainChunk terrainChunk, TerrainParameter param) {
		Pixmap pix1 = new Pixmap(resolve(param.tex1));
		Pixmap pix2 = new Pixmap(resolve(param.tex2));
		Pixmap pix3 = new Pixmap(resolve(param.tex3));

		final int splatResolution = param.generatedDiffuseMapSize;
		Pixmap splatPix = new Pixmap(splatResolution, splatResolution, Pixmap.Format.RGBA8888);

		final Color c1 = new Color();
		final Color c2 = new Color();
		final Color c3 = new Color();
		final Color cStore = new Color();

		for (int x = 0; x < splatResolution; x++) {
			for (int y = 0; y < splatResolution; y++) {
				float chunkX = (x / (float) splatResolution) * terrainChunk.width;
				float chunkY = (y / (float) splatResolution) * terrainChunk.height;

				float a = terrainChunk.getElevation((int) chunkX, (int) chunkY) / terrainChunk.magnitude.y;
				Color.rgba8888ToColor(c1, UtPixmap.getColorStretchLinearTile(pix1, param.tex1Scale, param.tex1Scale, splatPix, x, y));
				Color.rgba8888ToColor(c2, UtPixmap.getColorStretchLinearTile(pix2, param.tex2Scale, param.tex2Scale, splatPix, x, y));
				//Color.rgb888ToColor(c3,UtPixmap.getColorStretchLinearTile(pix3, param.tex3Scale.x, param.tex3Scale.y, splatPix, x, y));
				if (a < 0 || a > 1)
					System.out.println("a: " + a);
				final float a1 = 0.27f;
				final float a2 = 0.75f;
				final float a3 = 1f;
				final float a1Strength = 1 - UtMath.range(a, a1);
				final float a2Strength = 1 - UtMath.range(a, a2);
				final float a3Strength = 1 - UtMath.range(a, a3);
				//c1.mul(a1Strength);
				//c2.mul(a2Strength);
				//c3.mul(a3Strength);
				//a=1;
				c1.lerp(c2, a);
				//UtMath.interpolate(Interpolation.linear,a, c1,c2,cStore);

				splatPix.setColor(c1);

				splatPix.drawPixel(x, y);

				//splatPix.drawPixel(x,y);
			}
		}


		Texture diffuseTexture = new Texture(splatPix);
		diffuseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		pix1.dispose();
		pix2.dispose();
		pix3.dispose();
		splatPix.dispose();

		return diffuseTexture;
	}

	private Texture loadDiffuseMap(TerrainParameter parameter) {
		Texture diffuseTexture = new Texture(resolve(parameter.diffusemapName));
		diffuseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		return diffuseTexture;
	}


	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
		return null;
	}

	static public class TerrainParameter extends AssetLoaderParameters<Terrain> {

		public String heightmapName;        // heightmap texture to load height data from (currently supports RGB888, RGBA8888, and Alpha formats)
		// setting this value is what tells the terrain to load from a texture instead of generate from seed

		public long seed; // seed to use to generate height data (if heightmapName != null then this does nothing)
		public int fieldWidth = 128; // how wide the field data array is, only used if generating terrain.
		public int fieldHeight = 128; // how tall the field data array is, only used if generating terrain.

		public float terrainScale = 200; // the terrains "extent" (half the width) in world units
		public float terrainMagnitude = 30; // the highest peaks of the terrain in world units

		public int chunkWidth = 128; // how many data points wide each chunk is. , max chunk is 180x180 or the mesh will be too big to render
		public int chunkHeight = 128; // how many data points tall each chunk is

		public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
		public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;

		public String diffusemapName;  // diffusemap texture to use for each terrain chunk.
		// setting this values tells terrain to load diffusemap from texture instead of generating it

		public int generatedDiffuseMapSize;
		public String tex1;
		public float tex1Scale = 1f;
		public String tex2;
		public float tex2Scale = 1f;
		public String tex3;
		public float tex3Scale = 1f;


	}
}
