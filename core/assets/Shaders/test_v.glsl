attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
 // a_tangent
 // a_binormal
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec4 v_worldCoord;

 
void main() {
	v_texCoord0 = a_texCoord0;
	v_color = a_color;

	v_worldCoord = u_worldTrans * a_position;

	gl_Position = u_projViewTrans  * u_worldTrans * a_position;
}