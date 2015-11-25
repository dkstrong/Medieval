package asf.medieval.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/24/15.
 */
public class TerrainShaderProvider extends BaseShaderProvider {
	public final DefaultShader.Config config;

	public TerrainShaderProvider() {
		config = new DefaultShader.Config();

		//config.vertexShader = Gdx.files.internal("Shaders/terrain_v.glsl").readString();
		//config.fragmentShader = Gdx.files.internal("Shaders/terrain_f.glsl").readString();

		config.vertexShader = Gdx.files.internal("Shaders/test_v.glsl").readString();
		config.fragmentShader =Gdx.files.internal("Shaders/test_f.glsl").readString();


		//config.vertexShader = Gdx.files.internal("Shaders/unofficial_v.glsl").readString();
		//config.fragmentShader = Gdx.files.internal("Shaders/unofficial_f.glsl").readString();

	}

	@Override
	protected Shader createShader(Renderable renderable) {
		String prefix = createPrefix(renderable, config) + DefaultShader.createPrefix(renderable,config);

		DefaultShader shader= new DefaultShader(renderable, config, prefix, config.vertexShader, config.fragmentShader);

		BaseShader.Uniform tex1In = new BaseShader.Uniform("u_tex1", TerrainTextureAttribute.Tex1);
		BaseShader.Uniform tex2In = new BaseShader.Uniform("u_tex2", TerrainTextureAttribute.Tex2);
		BaseShader.Uniform tex3In = new BaseShader.Uniform("u_tex3", TerrainTextureAttribute.Tex3);
		BaseShader.Uniform tex1ScaleIn = new BaseShader.Uniform("u_tex1Scale");
		BaseShader.Uniform tex2ScaleIn = new BaseShader.Uniform("u_tex2Scale");
		BaseShader.Uniform tex3ScaleIn = new BaseShader.Uniform("u_tex3Scale");
		BaseShader.Uniform timeIn = new BaseShader.Uniform("u_time");
		BaseShader.Uniform mouseCoordsIn = new BaseShader.Uniform("u_mouseCoords");


		shader.register(tex1In, tex1Set);
		shader.register(tex2In, tex2Set);
		shader.register(tex3In, tex3Set);
		shader.register(tex1ScaleIn, tex1ScaleSet);
		shader.register(tex2ScaleIn, tex2ScaleSet);
		shader.register(tex3ScaleIn, tex3ScaleSet);
		shader.register(timeIn, timeSet);
		shader.register(mouseCoordsIn, mouseCoordsSet);

		return shader;
	}


	public final static BaseShader.Setter tex1Set = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder.bind(((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex1))).textureDescription);
			shader.set(inputID, unit);
		}
	};

	public final static BaseShader.Setter tex2Set = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder.bind(((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex2))).textureDescription);
			shader.set(inputID, unit);
		}
	};

	public final static BaseShader.Setter tex3Set = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder.bind(((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex3))).textureDescription);
			shader.set(inputID, unit);
		}
	};

	public final static BaseShader.Setter tex1ScaleSet = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, ((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex1))).scaleU);
		}
	};

	public final static BaseShader.Setter tex2ScaleSet = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, ((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex2))).scaleU);
		}
	};

	public final static BaseShader.Setter tex3ScaleSet = new BaseShader.LocalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, ((TerrainTextureAttribute)(combinedAttributes.get(TerrainTextureAttribute.Tex3))).scaleU);
		}
	};

	public final static BaseShader.Setter timeSet = new BaseShader.LocalSetter() {
		private float time;
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, time+= Gdx.graphics.getDeltaTime());
		}
	};

	public final static BaseShader.Setter mouseCoordsSet = new BaseShader.LocalSetter() {
		private Vector2 mousePos = new Vector2();
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			mousePos.x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
			mousePos.y = (Gdx.graphics.getHeight() - Gdx.input.getY())/ (float)Gdx.graphics.getHeight();
			shader.set(inputID, mousePos);
		}
	};

	public static String createPrefix (final Renderable renderable, final DefaultShader.Config config) {
		String prefix = "";
		return prefix+"\n";
	}
}
