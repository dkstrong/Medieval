package asf.medieval.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 *
 *
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/assets/loaders/PixmapLoader.java
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/assets/loaders/BitmapFontLoader.java
 *
 * Created by daniel on 11/18/15.
 */
public class TerrainLoader extends AsynchronousAssetLoader<Terrain, TerrainLoader.TerrainParameter> {

	public TerrainLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	private Texture diffuseTexture;

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter)
	{

	}

	@Override
	public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
		// TODO: apply min/mag
		diffuseTexture = new Texture(resolve(parameter.diffusemapName));
		diffuseTexture.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);

		if(parameter.heightmapName != null){
			Pixmap heightmapPixmap = new Pixmap(resolve(parameter.heightmapName));
			Terrain terrain = new Terrain(heightmapPixmap, diffuseTexture);
			return terrain;
		}else{
			Terrain terrain = new Terrain(parameter.seed, diffuseTexture);
			return terrain;
		}

	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
		return null;
	}

	static public class TerrainParameter extends AssetLoaderParameters<Terrain> {
		public String heightmapName;
		public String diffusemapName;

		public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
		public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;

		public long seed;
	}
}
