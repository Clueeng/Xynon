#version 120

attribute vec2 vertex;
varying vec2 uv;

void main() {
    uv = vertex;
    gl_Position = vec4(vertex, 0.0, 1.0);
}
