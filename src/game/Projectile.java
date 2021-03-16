package game;

import tools.Vector;

public class Projectile {

    private Vector position;
    private final Vector velocity;
    private double remainingLife;

    public Projectile(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
        remainingLife = 1.25;
    }

    public Vector getPosition() {
        return position;
    }

    public void update(double dt) {
     position = position.add(velocity.multiply(dt)) ;
     remainingLife -= dt;
    }

    public boolean isAlive(double dt) {
        return remainingLife<dt;
    }

    public boolean collides(Asteroid asteroid)
    {
        return  asteroid.contains(getPosition());
    }


}

