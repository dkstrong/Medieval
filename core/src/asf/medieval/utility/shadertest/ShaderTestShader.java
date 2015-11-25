package asf.medieval.utility.shadertest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by daniel on 11/21/15.
 */
public class ShaderTestShader implements Shader {
	ShaderProgram program;
	Camera camera;
	RenderContext context;
	int u_projViewTrans;
	int u_worldTrans;
	int u_mouseCoords;

	@Override
	public void init() {
		String vert = Gdx.files.internal("Shaders/test_v.glsl").readString();
		String frag = Gdx.files.internal("Shaders/test_f.glsl").readString();
		program = new ShaderProgram(vert, frag);
		if (!program.isCompiled())
			throw new Error(program.getLog());
		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_mouseCoords = program.getUniformLocation("u_mouseCoords");

	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		program.begin();
		program.setUniformMatrix(u_projViewTrans, camera.combined);
		//Gdx.input.getX()
		float[] mouseCords = new float[]{Gdx.input.getX()/(float)Gdx.graphics.getWidth(), Gdx.input.getY()/(float)Gdx.graphics.getHeight()};
		program.setUniform2fv(u_mouseCoords,mouseCords,0,2);

		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
	}

	@Override
	public void render(Renderable renderable) {
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		program.end();
	}

	@Override
	public void dispose() {
		program.dispose();
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable renderable) {
		return renderable.material.has(ShaderTestAttribute.AlbedoColor);
	}
}
