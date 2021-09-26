#version 330 core
//simple shader for models that don't require light
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;

void main() {
    // return the colour of the texture at given coordinates
    fragColor = vec4(texture(first_texture, aTexCoord).rgb, 1.0f);
}