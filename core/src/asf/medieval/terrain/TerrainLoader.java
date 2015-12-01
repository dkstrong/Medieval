package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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
		Terrain terrain;
		if(param != null){
			terrain = new Terrain();
			terrain.init(param);
		}else{
			terrain = new Terrain();
			terrain.loadTerrain(file);
		}
		return terrain;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
		return null;
	}

	static public class TerrainParameter extends AssetLoaderParameters<Terrain> {

		public String name;
		public String displayName;

		public float[] fieldData;     // fielddata directly supplied (overides heigtmapName and seed)
		public String heightmapName;  // fielddata will be set by a heightmap texture (currently supports RGB888, RGBA8888, and Alpha formats) (overides seed)
		public long seed; 	      // fielddata generate by simplex noise using this seed  (used only if fielddata and heightmapName is null)
		public int fieldWidth = 128;  // how wide the field data array is, only used if generating terrain.
		public int fieldHeight = 128; // how tall the field data array is, only used if generating terrain.

		public float scale = 200; // the terrains "extent" (half the width) in world units
		public float magnitude = 30; // the highest peaks of the terrain in world units

		public int chunkWidth = 180; // how many data points wide each chunk is. , max chunk is 180x180 or the mesh will be too big to render
		public int chunkHeight = 180; // how many data points tall each chunk is

		public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
		public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;

		public String weightMap1;  // weight map/splat map that defines how much of each texture to use
		public String tex1;
		public float tex1Scale = 1f;
		public String tex2;
		public float tex2Scale = 1f;
		public String tex3;
		public float tex3Scale = 1f;
		public String tex4;
		public float tex4Scale = 1f;
	}
}
