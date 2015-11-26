package asf.medieval.view;

import asf.medieval.terrain.TerrainShaderProvider;
import asf.medieval.terrain.TerrainTextureAttribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

/**
 * https://github.com/libgdx/libgdx/wiki/ModelBatch#shaderprovider
 * Created by daniel on 11/21/15.
 */
public class MedievalShaderProvider implements ShaderProvider {
	public final DefaultShaderProvider defaultShaderProvider;
	public final TerrainShaderProvider terrainShaderProvider;

	public MedievalShaderProvider() {
		defaultShaderProvider = new DefaultShaderProvider();
		terrainShaderProvider = new TerrainShaderProvider();

		defaultShaderProvider.config.numDirectionalLights = 2;
		defaultShaderProvider.config.numPointLights = 0;
		defaultShaderProvider.config.numSpotLights = 0;
		defaultShaderProvider.config.numBones = 12;

	}

	@Override
	public Shader getShader(Renderable renderable) {

		if(renderable.material.has(TerrainTextureAttribute.Tex1)){
			return terrainShaderProvider.getShader(renderable);
		}else{
			return defaultShaderProvider.getShader(renderable);
		}

	}

	@Override
	public void dispose() {
		terrainShaderProvider.dispose();
		defaultShaderProvider.dispose();

	}
}
