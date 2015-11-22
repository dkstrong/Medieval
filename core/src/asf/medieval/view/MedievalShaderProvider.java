package asf.medieval.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 * https://github.com/libgdx/libgdx/wiki/ModelBatch#shaderprovider
 * Created by daniel on 11/21/15.
 */
public class MedievalShaderProvider extends DefaultShaderProvider {
	DefaultShader.Config testShaderConfig;

	DefaultShader.Config unofficialShaderConfig;

	public MedievalShaderProvider() {
		testShaderConfig = new DefaultShader.Config();
		testShaderConfig.vertexShader = Gdx.files.internal("Shaders/test_v.glsl").readString();
		testShaderConfig.fragmentShader =Gdx.files.internal("Shaders/test_f.glsl").readString();

		unofficialShaderConfig = new DefaultShader.Config();
		unofficialShaderConfig.vertexShader = Gdx.files.internal("Shaders/unofficial_v.glsl").readString();
		unofficialShaderConfig.fragmentShader = Gdx.files.internal("Shaders/unofficial_f.glsl").readString();
	}

	@Override
	protected Shader createShader (Renderable renderable) {
		if(renderable.material.has(ShaderTestAttribute.AlbedoColor)){
			return new DefaultShader(renderable, testShaderConfig);
			//return new ShaderTestShader();

		}else{
			//return new DefaultShader(renderable, unofficialShaderConfig);
			return super.createShader(renderable);
		}

	}
}
