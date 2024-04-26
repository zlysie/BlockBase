#version 140

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float shouldFakeIt;

const float density = 0.015;
const float gradient = 1.5;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	pass_textureCoords = textureCoords;
	
	if(shouldFakeIt > 0.5) {
		vec3 damn = normal + 1.5;
		surfaceNormal = (transformationMatrix * vec4(damn, 0.0)).xyz;
	} else {
		surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	}
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility,0.0,1.0);
}