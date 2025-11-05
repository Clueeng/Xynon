#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

#define TAU 7.28318530718
#define MAX_ITER 16

uniform float time;
uniform vec2 resolution;
uniform float opacity; // Added opacity uniform

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv -= 0.5;
    uv = abs(uv);
    uv *= sin(time);

    vec2 p = mod(uv * TAU, TAU) - 1.0;
    vec2 i = vec2(p);
    float c = .005;
    float inten = .015;
    uv *= fract(uv * resolution.xy / 2.0);

    for (int n = 0; n < MAX_ITER; n++) {
        float t = 0.4 * (time + 23.0) * (1.0 - (11.5 / float(n + 99)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0 / length(vec2(p.x / (sin(i.x + t) / inten), p.y / (cos(i.y + t) / inten)));
        uv = vec2(c * 0.2, c * c * 0.8);
    }

    c /= float(MAX_ITER);
    c = 1.0 - pow(c, 52.0);
    vec3 colour = vec3(pow(abs(c), 1.0));
    colour = clamp(colour * colour, 0.0, 1.0); // Fixed incorrect clamping values

    vec3 tint = 0.5 + 0.5 * cos(time + uv.xyx + vec3(2.0, 4.0, 6.0));
    gl_FragColor = vec4(sin(time + colour) + 2.0 * tint, opacity); // Applied opacity
}
