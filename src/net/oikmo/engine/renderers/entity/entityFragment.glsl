#version 140

in vec2 pass_textureCoords;

uniform sampler2D textureS;

out vec4 out_Colour;

void main(void) {
	out_Colour = texture(textureS, pass_textureCoords);
}