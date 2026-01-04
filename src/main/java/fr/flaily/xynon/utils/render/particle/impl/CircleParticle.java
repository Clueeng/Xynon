package fr.flaily.xynon.utils.render.particle.impl;

import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.particle.Particle;

public class CircleParticle extends Particle {
    private final float radius;

    public CircleParticle(float x, float y, float vx, float vy, float life, int color, float radius) {
        super(x, y, vx, vy, life, color);
        this.radius = radius;
    }

    @Override
    public void render() {
        RenderUtil.drawCircle(x, y, radius, color);
    }
}
