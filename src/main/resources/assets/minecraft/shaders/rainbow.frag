#version 120
uniform float time;
uniform vec2 resolution;
varying vec2 uv;

vec3 hsv2rgb(vec3 c){
    vec3 p = abs(fract(c.xxx + vec3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);
    return c.z * mix(vec3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}
void main() {
    float hue = uv.y; // horizontal gradient
    vec3 rgb = hsv2rgb(vec3(hue, 1.0, 1.0));
    gl_FragColor = vec4(rgb, 1.0);
}