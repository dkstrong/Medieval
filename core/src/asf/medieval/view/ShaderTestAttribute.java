package asf.medieval.view;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.GdxRuntimeException;


/**
 * Created by daniel on 11/21/15.
 */
public class ShaderTestAttribute extends ColorAttribute {
	public final static String AlbedoColorAlias = "AlbedoColor"; // step 1: name the type
	public final static long AlbedoColor = register(AlbedoColorAlias); // step 2: register the type
	static {
		Mask |= AlbedoColor; // step 3: Make ColorAttribute accept the type
	}
	/** Prevent instantiating this class */
	private ShaderTestAttribute() {
		super(0);
	}
}
