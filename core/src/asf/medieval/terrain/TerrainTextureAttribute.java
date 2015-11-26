package asf.medieval.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;


/**
 * Created by daniel on 11/21/15.
 */
public class TerrainTextureAttribute extends TextureAttribute {
	private final static String WeightMap1Alias = "TerrainWeightMap1";
	public final static long WeightMap1 = register(WeightMap1Alias);
	private final static String Tex1Alias = "WeightMap1Alias";
	public final static long Tex1 = register(Tex1Alias);
	private final static String Tex2Alias = "TerrainTex2";
	public final static long Tex2 = register(Tex2Alias);
	private final static String Tex3Alias = "TerrainTex3";
	public final static long Tex3 = register(Tex3Alias);
	private final static String Tex4Alias = "TerrainTex4";
	public final static long Tex4 = register(Tex4Alias);
	private final static String TexMaskAlias = "TerrainTexMask";
	public final static long TexMask1 = register(TexMaskAlias);

	static {
		Mask |= WeightMap1 | Tex1 | Tex2 | Tex3 | Tex4 | TexMask1;
	}
	/** Prevent instantiating this class */
	private TerrainTextureAttribute() {
		super(0);
	}

	public TerrainTextureAttribute(long type, Texture texture) {
		super(type, texture);
	}

	public TerrainTextureAttribute(long type, TextureRegion region) {
		super(type, region);
	}

	public TerrainTextureAttribute(long type, Texture texture, float scale) {
		super(type, texture);
		super.scaleU = scale;
		super.scaleV = scale;
	}

	public TerrainTextureAttribute(long type, TextureRegion region, float scale) {
		super(type, region);
		super.scaleU = scale;
		super.scaleV = scale;
	}
}
