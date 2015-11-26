#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

	/////////////////////////////////////////////////////////////////////////
	/////////////////// SIMPLEX NOISE FROM WEBGL-NOISE //////////////////////
	/////////////////////////////////////////////////////////////////////////
	//            https://github.com/ashima/webgl-noise/wiki               //
	/////////////////////////////////////////////////////////////////////////

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v) {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}


	/////////////////////////////////////////////////////////////////////////
	////////////////////       END SIMPLEX NOISE     ////////////////////////
	/////////////////////////////////////////////////////////////////////////

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#ifdef normalFlag
varying vec3 v_normal;
#endif //normalFlag

#if defined(colorFlag)
varying vec4 v_color;
#endif

#ifdef blendedFlag
varying float v_opacity;
#ifdef alphaTestFlag
varying float v_alphaTest;
#endif //alphaTestFlag
#endif //blendedFlag

#if defined(diffuseTextureFlag) || defined(specularTextureFlag)
#define textureFlag
#endif

#ifdef diffuseTextureFlag
varying MED vec2 v_diffuseUV;
#endif

#ifdef specularTextureFlag
varying MED vec2 v_specularUV;
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef specularColorFlag
uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
#endif

#ifdef lightingFlag
varying vec3 v_lightDiffuse;

#if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
#define ambientFlag
#endif //ambientFlag

#ifdef specularFlag
varying vec3 v_lightSpecular;
#endif //specularFlag

#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));	
}

float getShadow() 
{
	return (//getShadowness(vec2(0,0)) + 
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif //shadowMapFlag

#if defined(ambientFlag) && defined(separateAmbientFlag)
varying vec3 v_ambientLight;
#endif //separateAmbientFlag

#endif //lightingFlag

#ifdef fogFlag
uniform vec4 u_fogColor;
varying float v_fog;
#endif // fogFlag

uniform sampler2D u_tex1;
uniform sampler2D u_tex2;
uniform sampler2D u_tex3;
uniform sampler2D u_tex4;
uniform sampler2D u_texMask1;
uniform float u_tex1Scale;
uniform float u_tex2Scale;
uniform float u_tex3Scale;
uniform float u_tex4Scale;
uniform float u_texMask1Scale;
uniform float u_time;

varying vec4 v_worldCoord;

void main() {
	#if defined(normalFlag) 
		vec3 normal = v_normal;
	#endif // normalFlag
		
	#if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
	#elif defined(diffuseTextureFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;
	#elif defined(diffuseTextureFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
	#elif defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = u_diffuseColor * v_color;
	#elif defined(diffuseColorFlag)
		vec4 diffuse = u_diffuseColor;
	#elif defined(colorFlag)
		vec4 diffuse = v_color;
	#else
		vec4 diffuse = vec4(1.0);
	#endif

	//////
	////	Begin calc diffuse from height

	float height = v_worldCoord.y;
	vec2 v_texCoord0 = v_diffuseUV;
	vec2 distort = vec2(0.01275,0.0125) * vec2(snoise(v_texCoord0 + vec2(0.0, u_time/3.0)),
							snoise(v_texCoord0 + vec2(u_time/3.0, 0.0)) );
	//vec4 texMaskColor1 = texture2D(u_texMask13, fract(v_texCoord0 * u_texMask1Scale));
	//float mask = texture2D(u_texMask1, fract(v_texCoord0 * 1.0)+distort).a;

	vec4 texColor1 = texture2D(u_tex1, fract(v_texCoord0 * u_tex1Scale));
	vec4 texColor2 = texture2D(u_tex2, fract(v_texCoord0 * u_tex2Scale));
	vec4 texColor3 = texture2D(u_tex3, fract(v_texCoord0 * u_tex3Scale)+distort);
	vec4 texColor4 = texture2D(u_tex4, fract(v_texCoord0 * u_tex4Scale));

	float m_tex1Weight =0.0;
	float m_tex2Weight=0.0;
	float m_tex3Weight=0.0;
	float m_tex4Weight=0.0;

	float m_regionMin = 0.0;
	float m_regionMax = 0.0;
	float m_regionRange = 0.0;
	float m_regionWeight = 0.0;

	m_regionMin = 0.0;
	m_regionMax = 5.0;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_tex1Weight = max(0.1, m_regionWeight);

	m_regionMin = 3.0;
	m_regionMax = 20.0;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_tex2Weight = max(0.0, m_regionWeight);

	m_regionMin = -1.0 - distort.x;
	m_regionMax = 1.0- distort.x;
	m_regionRange = m_regionMax - m_regionMin;
	m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	m_tex3Weight = max(0.0, m_regionWeight);

	//m_regionMin = 4.7;
	//m_regionMax = 5.1;
	//m_regionRange = m_regionMax - m_regionMin;
	//m_regionWeight = (m_regionRange - abs(height - m_regionMax)) / m_regionRange;
	//m_tex4Weight = max(0.0, m_regionWeight);

	float totalWeight = m_tex1Weight+ m_tex2Weight+m_tex3Weight+m_tex4Weight;
	m_tex1Weight = m_tex1Weight / totalWeight;
	m_tex2Weight = m_tex2Weight / totalWeight;
	m_tex3Weight = m_tex3Weight / totalWeight;
	m_tex4Weight = m_tex4Weight / totalWeight;

	diffuse = texColor1 * m_tex1Weight + texColor2 * m_tex2Weight + texColor3 * m_tex3Weight + texColor4 * m_tex4Weight;



	/////  End calc diffuse from height
	//////

	#if (!defined(lightingFlag))  
		gl_FragColor.rgb = diffuse.rgb;
	#elif (!defined(specularFlag))
		#if defined(ambientFlag) && defined(separateAmbientFlag)
			#ifdef shadowMapFlag
				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse));
				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
			#else
				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse));
			#endif //shadowMapFlag
		#else
			#ifdef shadowMapFlag
				gl_FragColor.rgb = getShadow() * (diffuse.rgb * v_lightDiffuse);
			#else
				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse);
			#endif //shadowMapFlag
		#endif
	#else
		#if defined(specularTextureFlag) && defined(specularColorFlag)
			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb * v_lightSpecular;
		#elif defined(specularTextureFlag)
			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * v_lightSpecular;
		#elif defined(specularColorFlag)
			vec3 specular = u_specularColor.rgb * v_lightSpecular;
		#else
			vec3 specular = v_lightSpecular;
		#endif
			
		#if defined(ambientFlag) && defined(separateAmbientFlag)
			#ifdef shadowMapFlag
			gl_FragColor.rgb = (diffuse.rgb * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular;
				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
			#else
				gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular;
			#endif //shadowMapFlag
		#else
			#ifdef shadowMapFlag
				gl_FragColor.rgb = getShadow() * ((diffuse.rgb * v_lightDiffuse) + specular);
			#else
				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular;
			#endif //shadowMapFlag
		#endif
	#endif //lightingFlag

	#ifdef fogFlag
		gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
	#endif // end fogFlag

	#ifdef blendedFlag
		gl_FragColor.a = diffuse.a * v_opacity;
		#ifdef alphaTestFlag
			if (gl_FragColor.a <= v_alphaTest)
				discard;
		#endif
	#else
		gl_FragColor.a = 1.0;
	#endif

}