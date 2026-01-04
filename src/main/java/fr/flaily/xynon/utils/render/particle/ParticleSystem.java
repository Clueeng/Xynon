package fr.flaily.xynon.utils.render.particle;

import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.particle.impl.CircleParticle;
import fr.flaily.xynon.utils.render.particle.impl.LinkedParticle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ParticleSystem {

    private static final Random RANDOM = new Random();
    private final ArrayList<Particle> particles = new ArrayList<>();

    public void addCircle(float x, float y, int color) {
        particles.add(new CircleParticle(
                x, y,
                randVel(), randVel(),
                1.2f,
                color,
                2f
        ));
    }

    public void addLinked(float x, float y, int color) {
        particles.add(new LinkedParticle(
                x, y,
                randVel(), randVel(),
                1.5f,
                color
        ));
    }

    private float randVel() {
        return (RANDOM.nextFloat() - 0.5f) * 40f;
    }

    public void update(float delta) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(delta);
            if (p.isDead()) it.remove();
        }
    }

    public void render() {
        // 1. Draw links
        renderLinks();

        // 2. Draw particles
        for (Particle p : particles) {
            p.render();
        }
    }

    private void renderLinks() {
        float maxDist = 60f;
        float maxDistSq = maxDist * maxDist;

        for (int i = 0; i < particles.size(); i++) {
            Particle a = particles.get(i);
            if (!(a instanceof LinkedParticle)) continue;

            for (int j = i + 1; j < particles.size(); j++) {
                Particle b = particles.get(j);
                if (!(b instanceof LinkedParticle)) continue;

                float dx = a.x - b.x;
                float dy = a.y - b.y;
                float distSq = dx * dx + dy * dy;

                if (distSq <= maxDistSq) {
                    float alpha = 1f - (distSq / maxDistSq);
                    RenderUtil.drawLine(
                            a.x, a.y,
                            b.x, b.y,
                            RenderUtil.withAlpha(a.color, alpha), 2f
                    );
                }
            }
        }
    }
}
