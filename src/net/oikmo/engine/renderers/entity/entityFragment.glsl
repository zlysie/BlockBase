#version 140

in vec2 pass_textureCoords;

uniform sampler2D textureS;

out vec4 out_Colour;

void main(void) {
	vec4 texture = texture(textureS, pass_textureCoords);
	if(texture.a < 0.5) {
		discard;
	}
	out_Colour = texture;
}