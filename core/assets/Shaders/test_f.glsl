#ifdef GL_ES
	precision mediump float;
#else

#endif

uniform vec2 u_mouseCoords;
uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;

varying vec2 v_texCoord0;
varying vec4 v_color;

 
void main() {
	vec4 texColor = texture2D(u_diffuseTexture, v_texCoord0);
	texColor.rgb = 1.0 - texColor.rgb;

	//gl_FragColor = vec4(1.0,0.0, 0.0, 1.0);
	//gl_FragColor = vec4(u_mouseCoords.x,u_mouseCoords.y, 0.0, 1.0);
	//gl_FragColor =  texColor * (u_diffuseColor * 0.5);
	gl_FragColor = vec4(vec3(v_texCoord0.s), 1.0);
}