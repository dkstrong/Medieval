package asf.medieval.terrain;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;


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
			terrain.init(file);
		}
		return terrain;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
		return null;
	}

	public static TerrainParameter getNewTerrainParamter(String name){
		TerrainParameter parameter = new TerrainParameter();
		parameter.name=name;
		parameter.displayName=name;
		//parameter.heightmapName = "Textures/HeightMaps/heightmap.png"; // heightmap.png // map8.jpg // mountains128.png // map4.jpg // map7.png
		parameter.seed = MathUtils.random.nextLong();
		parameter.fieldWidth = 180;
		parameter.fieldHeight = 180;
		parameter.scale = 200;
		parameter.magnitude = 30;
		parameter.chunkWidth = 180;
		parameter.chunkHeight = 180;
		parameter.weightMap1 = "Textures/HeightMaps/blankmap.png"; // sand512.jpg
		parameter.tex1="Textures/Terrain/grass.png"; // grass_2.png
		parameter.tex1Scale = 5;
		parameter.tex2="Textures/Terrain/dirt.png";
		parameter.tex2Scale = 4;
		parameter.tex3="Textures/Terrain/water.jpg";
		parameter.tex3Scale = 10;
		parameter.tex4="Textures/Terrain/stone_diffuse_02.png";  // brickRound_diffuse
		parameter.tex4Scale = 70;

		return parameter;
	}

	public static TerrainParameter loadTerrainParamter(FileHandle terrainFile)
	{
		if(terrainFile == null)
			throw new IllegalArgumentException("terrainFile can not be null");
		if(terrainFile.isDirectory())
			throw new IllegalArgumentException("provided terrainFile ("+terrainFile.file().getAbsolutePath()+") is a directory");
		if(!terrainFile.exists())
			throw new IllegalArgumentException("provided terrainFile ("+terrainFile.file().getAbsolutePath()+") does not exist");

		TerrainParameter parameter = new TerrainParameter();
		String name = terrainFile.nameWithoutExtension();


		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(terrainFile);
			parameter.name = root.getAttribute("name",name);
			parameter.displayName = root.getAttribute("displayName",name);
			parameter.scale = root.getFloatAttribute("scale", parameter.scale);
			parameter.magnitude = root.getFloatAttribute("magnitude", parameter.magnitude);
			parameter.chunkWidth = root.getIntAttribute("chunkWidth", parameter.chunkWidth);
			parameter.chunkHeight = root.getIntAttribute("chunkHeight", parameter.chunkHeight);


			XmlReader.Element heightData = root.getChildByName("heightData");
			if(heightData != null){
				parameter.fieldData = null;
				parameter.heightmapName = heightData.getAttribute("heightmapName",null);
				parameter.seed = Long.parseLong(heightData.getAttribute("seed","0"));
				parameter.fieldWidth = root.getIntAttribute("fieldWidth", parameter.fieldWidth);
				parameter.fieldHeight = root.getIntAttribute("fieldHeight", parameter.fieldHeight);
			}

			XmlReader.Element weightMap1 = root.getChildByName("weightMap1");
			if(weightMap1!= null){
				parameter.weightMap1 = weightMap1.getAttribute("tex",null);
			}

			XmlReader.Element tex1 = root.getChildByName("tex1");
			if(tex1!= null){
				parameter.tex1 = tex1.getAttribute("tex",null);
				parameter.tex1Scale = tex1.getFloatAttribute("scale", parameter.tex1Scale);
			}

			XmlReader.Element tex2 = root.getChildByName("tex2");
			if(tex2!= null){
				parameter.tex2 = tex2.getAttribute("tex",null);
				parameter.tex2Scale = tex2.getFloatAttribute("scale",parameter.tex2Scale);
			}

			XmlReader.Element tex3 = root.getChildByName("tex3");
			if(tex3!= null){
				parameter.tex3 = tex3.getAttribute("tex",null);
				parameter.tex3Scale = tex3.getFloatAttribute("scale",parameter.tex3Scale);
			}

			XmlReader.Element tex4 = root.getChildByName("tex4");
			if(tex4!= null){
				parameter.tex4 = tex4.getAttribute("tex",null);
				parameter.tex4Scale = tex4.getFloatAttribute("scale",parameter.tex4Scale);
			}

			return parameter;
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load file: "+terrainFile.file().getAbsolutePath()+"", e);
		}


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
