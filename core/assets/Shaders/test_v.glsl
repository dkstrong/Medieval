

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
 // a_tangent
 // a_binormal
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_cameraPosition;

uniform float u_time;

varying vec2 v_texCoord0;
varying vec4 v_color;
varying vec4 v_worldCoord;

 
void main() {
	// Calculate Billboard
	float Dist = length(u_cameraPosition - a_position);
	vec3 vAt = normalize(u_cameraPosition - a_position).xyz;
	vec3 vUp = vec3(0.0,1.0,0.0);
	vec3 vRight = normalize(cross( vUp, vAt ));

	// Fade
	float FadeOutStart = 200.0;
	float FadeOutDist = 250.0;
	v_color = a_color;
	v_color.w =  max( ( Dist - FadeOutStart ) / (FadeOutDist * v_color.w ), 0.0 );

	v_texCoord0 = a_texCoord0;
	vec4 vPos = a_position;
	if(v_color.w < 1.0)
	{
		vec4 vMove = vec4( gl_MultiTexCoord0.s * vRight + v_texCoord0.t * vUp, 0.0 );

		// Calculate wind
      	float fWind = ( (pow(((sin((-u_time * 4.0 + a_position.x / 20.0 + sin(a_position.y * 25.4) / 1.0 )) + 1.0) / 3.0), 4.0)) +

                 (sin( u_time * 10.0 + a_position.y * 25.4 ) * 0.02) ) * v_texCoord0.t;



      // Add wind

      vMove.xz += fWind * 1.0;

      vPos+= vMove;
	}
	else
	{

	}


	v_worldCoord = u_worldTrans * vPos;
	gl_Position = u_projViewTrans  * u_worldTrans * vPos;

}