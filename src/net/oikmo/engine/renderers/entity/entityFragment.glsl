#version 150

in vec2 pass_textureCoords;
in float visibility;
in vec3 surfaceNormal;

out vec4 out_Colour;

uniform sampler2D textureS;
uniform float whiteOffset;
uniform vec3 skyColour;

void main(void) {
	vec4 texture = texture(textureS, pass_textureCoords);
	if(texture.a < 0.5) {
		discard;
	}
	
	texture.rgb += whiteOffset;
	
	vec3 unitNormal = normalize(surfaceNormal);
	float nDot1 = dot(unitNormal, surfaceNormal);
	vec3 diffuse = nDot1 * vec3(1,1,1);
	
	out_Colour = vec4(diffuse, 1.0) * texture;
	out_Colour = mix(vec4(skyColour,1.0), out_Colour, visibility);
}