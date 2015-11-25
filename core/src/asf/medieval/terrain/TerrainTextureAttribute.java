package asf.medieval.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;


/**
 * Created by daniel on 11/21/15.
 */
public class TerrainTextureAttribute extends TextureAttribute {
	public final static String Tex1Alias = "TerrainTex1";
	public final static long Tex1 = register(Tex1Alias);
	public final static String Tex2Alias = "TerrainTex2";
	public final static long Tex2 = register(Tex2Alias);
	public final static String Tex3Alias = "TerrainTex3";
	public final static long Tex3 = register(Tex3Alias);
	public final static String TexMaskAlias = "TerrainTexMask";
	public final static long TexMask = register(TexMaskAlias);

	static {
		Mask |= Tex1 | Tex2 | Tex3 | TexMask;
	}
	/** Prevent instantiating this class */
	private TerrainTextureAttribute() {
		super(0);
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
