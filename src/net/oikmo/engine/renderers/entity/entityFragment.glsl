#version 140

in vec2 pass_textureCoords;
in float visibility;

out vec4 out_Colour;

uniform sampler2D textureS;
uniform float whiteOffset;
uniform vec3 skyColour;

void main(void) {
	vec4 texture = texture(textureS, pass_textureCoords);
	if(texture.a < 0.5) {
		discard;
	}
	
	texture = vec4(texture.r + whiteOffset, texture.g + whiteOffset, texture.b + whiteOffset, texture.a);
	out_Colour = texture;
	out_Colour = mix(vec4(skyColour,1.0), out_Colour, visibility);
}