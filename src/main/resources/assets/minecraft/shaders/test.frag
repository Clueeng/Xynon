#define repeat(i, n) for(int i = 0; i < n; i++)

uniform float time;
uniform vec2 resolution;

void main(void) {
    // Normalize coordinates (-1 to 1) and fix aspect ratio
    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / min(resolution.x, resolution.y);

    // Create a rolling movement over time
    for(float i = 1.0; i < 4.0; i++) {
        uv.x += 0.35 / i * sin(i * 3.0 * uv.y + time * 0.5);
        uv.y += 0.35 / i * cos(i * 3.0 * uv.x + time * 0.5);
    }

    // Base colors (Dark Navy to Deep Purple/Teal)
    vec3 color1 = vec3(0.05, 0.1, 0.2); // Deep Blue
    vec3 color2 = vec3(0.2, 0.05, 0.3); // Deep Purple

    // Mix based on the warped UV coordinates
    float brightness = 0.5 * sin(uv.x + uv.y) + 0.5;
    vec3 finalColor = mix(color1, color2, brightness);

    // Add a subtle "sheen" or highlight layer
    finalColor += 0.08 / length(uv);

    gl_FragColor = vec4(finalColor, 1.0);
}