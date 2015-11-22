package asf.medieval.terrain;

import asf.medieval.utility.UtMath;
import asf.medieval.utility.UtPixmap;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
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

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter)
	{

	}

	@Override
	public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
		// TODO: apply min/mag
		Terrain terrain = new Terrain();

		if(parameter.heightmapName != null && false){
			Pixmap heightPix = new Pixmap(resolve(parameter.heightmapName));
			terrain.createHeightField(heightPix);
		}else{
			terrain.createHeightField(parameter.seed);
		}
		terrain.createRenderables(this,parameter);
		return terrain;

	}

	protected Texture getDiffuseMap(TerrainChunk terrainChunk, TerrainParameter parameter){
		if(parameter.generatedDiffuseMapSize >0 && false){
			return generateDiffuseMap(terrainChunk, parameter);
		}else{
			return loadDiffuseMap(parameter);
		}
	}

	private Texture generateDiffuseMap(TerrainChunk terrainChunk, TerrainParameter param){
		Pixmap pix1 = new Pixmap(resolve(param.tex1));
		Pixmap pix2 = new Pixmap(resolve(param.tex2));
		Pixmap pix3 = new Pixmap(resolve(param.tex3));

		final int splatResolution = param.generatedDiffuseMapSize;
		Pixmap splatPix = new Pixmap( splatResolution, splatResolution, Pixmap.Format.RGBA8888 );

		final Color c1 = new Color();
		final Color c2 = new Color();
		final Color c3 = new Color();
		final Color cStore = new Color();

		for(int x=0; x< splatResolution; x++){
			for(int y=0; y<splatResolution; y++){
				float fieldX = (x/(float)splatResolution) * terrainChunk.width;
				float fieldY = (y/(float)splatResolution) * terrainChunk.height;

				float a = terrainChunk.getElevationField((int)fieldX,(int)fieldY) /terrainChunk.magnitude.y;
				Color.rgba8888ToColor(c1, UtPixmap.getColorStretchLinearTile(pix1, param.tex1Scale.x, param.tex1Scale.y, splatPix, x, y));
				Color.rgba8888ToColor(c2, UtPixmap.getColorStretchLinearTile(pix2, param.tex2Scale.x, param.tex2Scale.y, splatPix, x, y));
				//Color.rgb888ToColor(c3,UtPixmap.getColorStretchLinearTile(pix3, param.tex3Scale.x, param.tex3Scale.y, splatPix, x, y));
				if(a<0 || a >1)
					System.out.println("a: "+a);
				final float a1 = 0.27f;
				final float a2 = 0.75f;
				final float a3 = 1f;
				final float a1Strength = 1 - UtMath.range(a,a1);
				final float a2Strength = 1 - UtMath.range(a,a2);
				final float a3Strength = 1 - UtMath.range(a,a3);
				//c1.mul(a1Strength);
				//c2.mul(a2Strength);
				//c3.mul(a3Strength);
				//a=1;
				c1.lerp(c2,a);
				//UtMath.interpolate(Interpolation.linear,a, c1,c2,cStore);

				splatPix.setColor(c1);

				splatPix.drawPixel(x, y);

				//splatPix.drawPixel(x,y);
			}
		}


		Texture diffuseTexture = new Texture( splatPix );
		diffuseTexture.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);

		pix1.dispose();
		pix2.dispose();
		pix3.dispose();
		splatPix.dispose();

		return diffuseTexture;
	}

	private Texture loadDiffuseMap(TerrainParameter parameter){
		Texture diffuseTexture = new Texture(resolve(parameter.diffusemapName));
		diffuseTexture.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
		return diffuseTexture;
	}



	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
		return null;
	}

	static public class TerrainParameter extends AssetLoaderParameters<Terrain> {
		public String heightmapName;
		public String diffusemapName;

		public int generatedDiffuseMapSize;
		public String tex1;
		public final Vector2 tex1Scale = new Vector2(1,1);
		public String tex2;
		public final Vector2 tex2Scale = new Vector2(1,1);
		public String tex3;
		public final Vector2 tex3Scale = new Vector2(1,1);

		public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
		public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;

		public long seed;
	}
}
