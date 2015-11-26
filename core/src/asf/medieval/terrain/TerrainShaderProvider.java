package asf.medieval.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/24/15.
 */
public class TerrainShaderProvider implements ShaderProvider {
	public final DefaultShader.Config config;

	public TerrainShaderProvider() {
		config = new DefaultShader.Config();
		clearShaderCache();

	}

	private Array<Shader> shaders = new Array<Shader>(false, 1, Shader.class);

	@Override
	public Shader getShader(Renderable renderable) {

		Shader suggestedShader = renderable.shader;
		if (suggestedShader != null && suggestedShader.canRender(renderable))
		{
			return suggestedShader;
		}

		for (Shader shader : shaders) {
			if (shader.canRender(renderable))
			{
				return shader;
			}
		}
		final Shader shader = createShader(renderable);
		shader.init();
		shaders.add(shader);
		return shader;
	}

	@Override
	public void dispose() {
		for (Shader shader : shaders) {
			shader.dispose();
		}
		shaders.clear();
	}


	public void clearShaderCache() {
		if(shaders.size > 0)
			System.out.println("clear shader cache: "+shaders.size);
		config.vertexShader = Gdx.files.internal("Shaders/terrain_v.glsl").readString();
		config.fragmentShader = Gdx.files.internal("Shaders/terrain_f.glsl").readString();

		//config.vertexShader = Gdx.files.internal("Shaders/test_v.glsl").readString();
		//config.fragmentShader =Gdx.files.internal("Shaders/test_f.glsl").readString();


		//config.vertexShader = Gdx.files.internal("Shaders/unofficial_v.glsl").readString();
		//config.fragmentShader = Gdx.files.internal("Shaders/unofficial_f.glsl").readString();
		while (shaders.size > 0) {
			shaders.removeIndex(0).dispose();
		}
	}

	private Shader createShader(Renderable renderable) {
		String prefix = createPrefix(renderable, config) + DefaultShader.createPrefix(renderable, config);

		DefaultShader shader = new DefaultShader(renderable, config, prefix, config.vertexShader, config.fragmentShader);

		BaseShader.Uniform u_weightMap1 = new BaseShader.Uniform("u_weightMap1", TerrainTextureAttribute.WeightMap1);
		BaseShader.Uniform u_tex1 = new BaseShader.Uniform("u_tex1", TerrainTextureAttribute.Tex1);
		BaseShader.Uniform u_tex2 = new BaseShader.Uniform("u_tex2", TerrainTextureAttribute.Tex2);
		BaseShader.Uniform u_tex3 = new BaseShader.Uniform("u_tex3", TerrainTextureAttribute.Tex3);
		BaseShader.Uniform u_tex4 = new BaseShader.Uniform("u_tex4", TerrainTextureAttribute.Tex4);
		BaseShader.Uniform u_texMask1 = new BaseShader.Uniform("u_texMask1", TerrainTextureAttribute.TexMask1);
		BaseShader.Uniform u_tex1Scale = new BaseShader.Uniform("u_tex1Scale");
		BaseShader.Uniform u_tex2Scale = new BaseShader.Uniform("u_tex2Scale");
		BaseShader.Uniform u_tex3Scale = new BaseShader.Uniform("u_tex3Scale");
		BaseShader.Uniform u_tex4Scale = new BaseShader.Uniform("u_tex4Scale");
		BaseShader.Uniform u_texMask1Scale = new BaseShader.Uniform("u_texMask1Scale");
		BaseShader.Uniform u_time = new BaseShader.Uniform("u_time");
		BaseShader.Uniform u_mouseCoords = new BaseShader.Uniform("u_mouseCoords");

		shader.register(u_weightMap1, new TexSetter(TerrainTextureAttribute.WeightMap1));
		shader.register(u_tex1, new TexSetter(TerrainTextureAttribute.Tex1));
		shader.register(u_tex2, new TexSetter(TerrainTextureAttribute.Tex2));
		shader.register(u_tex3, new TexSetter(TerrainTextureAttribute.Tex3));
		shader.register(u_tex4, new TexSetter(TerrainTextureAttribute.Tex4));
		shader.register(u_texMask1, new TexSetter(TerrainTextureAttribute.TexMask1));
		shader.register(u_tex1Scale, new TexScaleSetter(TerrainTextureAttribute.Tex1));
		shader.register(u_tex2Scale, new TexScaleSetter(TerrainTextureAttribute.Tex2));
		shader.register(u_tex3Scale, new TexScaleSetter(TerrainTextureAttribute.Tex3));
		shader.register(u_tex4Scale, new TexScaleSetter(TerrainTextureAttribute.Tex4));
		shader.register(u_texMask1Scale, new TexScaleSetter(TerrainTextureAttribute.TexMask1));
		shader.register(u_time, timeSet);
		shader.register(u_mouseCoords, mouseCoordsSet);

		return shader;
	}


	private static class TexSetter extends BaseShader.LocalSetter {
		public long attribute;

		public TexSetter(long attribute) {
			this.attribute = attribute;
		}

		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder.bind(((TerrainTextureAttribute) (combinedAttributes.get(attribute))).textureDescription);
			shader.set(inputID, unit);
		}
	}

	private static class TexScaleSetter extends BaseShader.LocalSetter {
		public long attribute;

		public TexScaleSetter(long attribute) {
			this.attribute = attribute;
		}

		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, ((TerrainTextureAttribute) (combinedAttributes.get(attribute))).scaleU);
		}
	}


	public final static BaseShader.Setter timeSet = new BaseShader.GlobalSetter() {
		private float time;

		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, time += Gdx.graphics.getDeltaTime());
		}
	};

	public final static BaseShader.Setter mouseCoordsSet = new BaseShader.GlobalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			float x = Gdx.input.getX() / (float) Gdx.graphics.getWidth();
			float y = (Gdx.graphics.getHeight() - Gdx.input.getY()) / (float) Gdx.graphics.getHeight();
			shader.set(inputID, x, y);
		}
	};

	public static String createPrefix(final Renderable renderable, final DefaultShader.Config config) {
		String prefix = "";
		return prefix + "\n";
	}
}
