package game;

import tools.Vector;

import java.util.List;

/**
 * Models a spaceship controlled by a player.
 */
public class Spaceship {

    private static final double MAIN_ENGINE_POWER = 40;
    public static final double MAIN_ENGINE_POWER1 = MAIN_ENGINE_POWER;
    public static final double MAIN_ENGINE_POWER_1 = MAIN_ENGINE_POWER1;
    private static final double ANGULAR_VELOCITY = 3.5;
    private static final double ENGINE_DECELERATION = -10;
    private static final double TANK_CAPACITY = 5;
    private static final double FUEL_REFILL = 0.2;
    private static final double MAIN_ENGINE_CONSUMPTION = 1;
    private static final double LATERALS_ENGINE_CONSUMPTION = 0.3;
    private static final double RECOIL_ENGINE_CONSUMPTION = 0.5;
    /**
     * A list of points on the boundary of the spaceship, used
     * to detect collision with other objects.
     */
    private static final List<Vector> contactPoints =
            List.of(
                    new Vector(0, 0),
                    new Vector(27, 0),
                    new Vector(14.5, 1.5),
                    new Vector(2, 3),
                    new Vector(0, 18),
                    new Vector(-13, 18),
                    new Vector(-14, 2),
                    new Vector(-14, -2),
                    new Vector(-13, -18),
                    new Vector(0, -18),
                    new Vector(2, -3),
                    new Vector(14.5, -1.5)
            );
    /**
     * The position of the center of the spaceship
     */
    private Vector position;
    private Vector velocity;
    private double fuel;
    private double invulnerability;
    private double NumberOfLives;
    /**
     * The forward direction for the spaceship, encoding the rotation
     * from horizontal of its image and the direction of acceleration.
     */
    private Vector direction = new Vector(1, 0);
    /**
     * Controls if the main engine, with forward acceleration, is powered on.
     */
    private boolean isMainEngineOn = false;
    private boolean isLeftEngineOn = false;
    private boolean isRightEngineOn = false;
    private boolean isMainEngineRecoil = false;

    /**
     * Initially the spaceship will be positioned at the center of space.
     */
    public Spaceship() {
        this.position =
                new Vector(
                        Space.SPACE_HEIGHT / 2,
                        Space.SPACE_WIDTH / 2
                );
        velocity = new Vector(0, 0);
        NumberOfLives = 5;
    }

    public static List<Vector> getContactPoints() {
        return contactPoints;
    }

    /**
     * @return the position of the spaceship
     */
    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }
    /**
     * @return the angle of the spaceship in degree, where 0 is facing right.
     */
    public double getDirectionAngle() {
        return direction.angle();
    }

    public Vector getAcceleration() {
        if (isMainEngineOn())
            return direction.multiply(MAIN_ENGINE_POWER_1);
        if (isMainEngineRecoil())
            return direction.multiply(ENGINE_DECELERATION);
        return direction.multiply(0);
    }

    private double getCurrentConsumption() {
        if (isMainEngineOn() && (isLeftEngineOn() || isRightEngineOn()))
            return MAIN_ENGINE_CONSUMPTION
                   + LATERALS_ENGINE_CONSUMPTION
                    - FUEL_REFILL;
        else if (isMainEngineRecoil() && (isLeftEngineOn() || isRightEngineOn()))
            return RECOIL_ENGINE_CONSUMPTION
                    + LATERALS_ENGINE_CONSUMPTION
                    - FUEL_REFILL;
        else if (isLeftEngineOn() && (!isLeftEngineOn() || !isRightEngineOn() || !isMainEngineRecoil()))
         return MAIN_ENGINE_CONSUMPTION -FUEL_REFILL;
        else if (isMainEngineRecoil() && (!isMainEngineOn() || !isLeftEngineOn() || !isRightEngineOn()))
            return RECOIL_ENGINE_CONSUMPTION -FUEL_REFILL;
        return LATERALS_ENGINE_CONSUMPTION - FUEL_REFILL;
    }

    private double getAutonomy(double dt) {
        return Math.min(fuel / getCurrentConsumption(), dt);
    }

    public double getFuelPercentage() {
        return fuel / TANK_CAPACITY * 100;
    }

    public double getInvulnerabilityTime() {
        return invulnerability;
    }

    public double getLifeNumbers() {
        return NumberOfLives;
    }

    /**
     * @return whether the main engine is on (forward acceleration).
     */
    public boolean isMainEngineOn() {
        return isMainEngineOn;
    }

    public boolean isLeftEngineOn() {
        return isLeftEngineOn;
    }

    public boolean isRightEngineOn() {
        return isRightEngineOn;
    }

    public boolean isMainEngineRecoil() {
        return isMainEngineRecoil;
    }

    public boolean isInvulnerable() {
        return !(invulnerability < 0);
    }

    /**
     * The spaceship is a moving object. Every now and then, its position
     * must be updated, as well as other parameters evolving with time. This
     * method simulates the effects of a delay <em>dt</em> over the spaceship.
     * For good accuracy this delay should be kept small.
     *
     * @param dt the time delay to simulate.
     */
    public void update(double dt) {
        if (isMainEngineOn()) {
            position = position.add(getVelocity().multiply(dt));
            position = Space.toricRemap(position);
            updateVelocity(dt);
        }
        updateDirection(dt);
        if (getAutonomy(dt) < TANK_CAPACITY)
            fuel = TANK_CAPACITY - getCurrentConsumption();
        invulnerability -= dt;
    }

    public void updateDirection(double dt) {
        if (isRightEngineOn() || isLeftEngineOn())
            direction = direction.rotate((ANGULAR_VELOCITY + getAutonomy(dt)));
    }

    public void updateVelocity(double dt) {
        velocity = velocity.add(getAcceleration().multiply(getAutonomy(dt)));
    }

    /**
     * Switches the main engine (powering forward acceleration) on.
     */
    public void startMainEngine() {
        isMainEngineOn = true;
    }

    /**
     * Switches the main engine (powering forward acceleration) off.
     */
    public void stopMainEngine() {
        isMainEngineOn = false;
    }

    public void startLeftEngine() {
        isLeftEngineOn = true;
    }

    public void stopLeftEngine() {
        isLeftEngineOn = false;
    }

    public void startRightEngine() {
        isRightEngineOn = true;
    }

    public void stopRightEngine() {
        isRightEngineOn = false;
    }

    public void startBrake() {
        isMainEngineRecoil = true;
    }

    public void stopBreak() {
        isMainEngineRecoil = false;
    }

    public void setInvulnerability(double dt) {
        if (!isInvulnerable())
            invulnerability = Math.max(invulnerability, dt);
    }
    public boolean collides(Asteroid asteroid) {

        for (Vector point : getContactPoints()) {
            if (invulnerability>=0)
                break;
            if (asteroid.contains(point.rotate(getDirectionAngle()).translate(position))) {
                NumberOfLives -= 1;
                return true;
            }
        }
        return false;
    }

    public Projectile fire() {
       return new Projectile(
               position.add(direction.multiply(30)),
        direction.multiply(100).add(getVelocity()));
    }


}

