package fr.flaily.xynon.utils.render.particle.impl;

import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.particle.Particle;

public class SquareParticle extends Particle {
    private final float size;

    public SquareParticle(float x, float y, float vx, float vy, float life, int color, float size) {
        super(x, y, vx, vy, life, color);
        this.size = size;
    }

    @Override
    public void render() {
        RenderUtil.drawRect(x - size / 2f, y - size / 2f, size, size, color);
    }
}
