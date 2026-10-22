#version 330
#extension GL_ARB_separate_shader_objects : require

precision lowp float;

layout (location = 0) in vec2 uv;

uniform sampler2D u_Texture;

layout (location = 0) out vec4 color;

void main() {
    color = texture(u_Texture, uv);
}
