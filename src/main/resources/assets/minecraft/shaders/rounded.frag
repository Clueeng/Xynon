#version 120

uniform vec2 location;
uniform vec2 rectSize;
uniform float radius;
uniform sampler2D textureIn;
uniform float alpha;

float roundRectSDF(vec2 p, vec2 b, float r) {
    vec2 d = abs(p) - b + vec2(r);
    return min(max(d.x, d.y), 0.0) + length(max(d, 0.0)) - r;
}

void main() {
    vec2 rectCenter = location + (rectSize / 2.0);
    float distance = roundRectSDF(gl_FragCoord.xy - rectCenter, rectSize / 2.0, radius);

    float smoothedAlpha = 1.0 - smoothstep(-0.5, 0.5, distance);

    vec4 texColor = texture2D(textureIn, gl_TexCoord[0].st);
    gl_FragColor = vec4(texColor.rgb, texColor.a * smoothedAlpha * alpha);
}