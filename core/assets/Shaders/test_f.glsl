#ifdef GL_ES
	precision mediump float;
#else

#endif

uniform vec2 u_mouseCoords;
uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;

uniform sampler2D u_tex1;
uniform sampler2D u_tex2;
uniform sampler2D u_tex3;
uniform float u_tex1Scale;
uniform float u_tex2Scale;
uniform float u_tex3Scale;
uniform float u_time;



varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec4 v_worldCoord;




void main() {

	//vec4 diffColor = texture2D(u_diffuseTexture, v_texCoord0);
	//gl_FragColor = v_color * diffColor;

	vec4 texColor1 = texture2D(u_tex1, fract(v_texCoord0 * u_tex1Scale));
	vec4 texColor2 = texture2D(u_tex2, fract(v_texCoord0 * u_tex2Scale));
	vec4 texColor3 = texture2D(u_tex3, fract(v_texCoord0 * u_tex3Scale));

	vec4 weightedColor = vec4(0,0,0,0);

	float m_regionMin = 0.0;
	float m_regionMax = 0.0;
	float m_regionRange = 0.0;
	float m_regionWeight = 0.0;
	float height = v_worldCoord.y;

	m_regionMin = 0.0;
	m_regionMax = 5.0;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_regionWeight = max(0.1, m_regionWeight);
	weightedColor += texColor1 * m_regionWeight ;

	m_regionMin = 3.0;
	m_regionMax = 20.0;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_regionWeight = max(0.0, m_regionWeight);
	weightedColor += texColor2 * m_regionWeight;

	m_regionMin = -1.0;
	m_regionMax = 1.0;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_regionWeight = max(0.0, m_regionWeight);
	weightedColor += texColor3 * m_regionWeight;





	gl_FragColor = v_color * weightedColor;

	//gl_FragColor = vec4(1.0,0.0, 0.0, 1.0);
}