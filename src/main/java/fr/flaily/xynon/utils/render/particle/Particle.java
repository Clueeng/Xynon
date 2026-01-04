package fr.flaily.xynon.utils.render.particle;

public abstract class Particle {
    public float x, y;
    public float vx, vy;
    public float life;
    public int color;

    protected Particle(float x, float y, float vx, float vy, float life, int color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.color = color;
    }

    public void update(float delta) {
        x += vx * delta;
        y += vy * delta;
        life -= delta;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public abstract void render();
}
