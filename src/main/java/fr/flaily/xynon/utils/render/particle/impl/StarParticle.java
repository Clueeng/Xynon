package fr.flaily.xynon.utils.render.particle.impl;

import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.particle.Particle;

public class StarParticle extends Particle {

    public StarParticle(float x, float y, float vx, float vy, float life, int color) {
        super(x, y, vx, vy, life, color);
    }

    @Override
    public void render() {
//        RenderUtil.drawStar(x, y, 5, 6f, 3f, color);
    }
}
