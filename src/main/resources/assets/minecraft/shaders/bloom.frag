#version 120

uniform sampler2D textureIn;
uniform vec2 texelSize, direction;
uniform float radius;
uniform vec4 color;

float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
}

void main() {
    vec2 st = gl_TexCoord[0].st;

    // JUICE FIX: Shift the sampling coordinate slightly
    // but keep it small enough so the left side still gets some 'glow'
    vec2 shadowOffset = vec2(-1.0, 1.0) * texelSize * (radius / 4.0);
    vec2 shiftedSt = st + shadowOffset;

    float alpha = 0.0;
    float totalWeight = 0.0;
    float sigma = radius / 2.0;

    for (float f = -radius; f <= radius; f++) {
        float weight = gaussian(f, sigma);
        // We blend the centered sample and the shifted sample
        float sampleA = texture2D(textureIn, st + f * (texelSize * direction)).a;
        float sampleB = texture2D(textureIn, shiftedSt + f * (texelSize * direction)).a;

        alpha += max(sampleA, sampleB) * weight;
        totalWeight += weight;
    }

    float finalAlpha = (alpha / totalWeight) * color.a;

    // This curve makes the shadow 'heavy' near the box but soft at the edges
    finalAlpha = pow(finalAlpha, 1.1) * 1.8;

    gl_FragColor = vec4(color.rgb, finalAlpha);
}