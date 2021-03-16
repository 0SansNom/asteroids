package game;


import tools.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Space contains all the information determining the current state of
 * the game, and methods implementing how the game state changes, and how
 * the game ends (basically the rules of the game).
 */
public class Space {

  public static final double SPACE_WIDTH = 800;
  public static final double SPACE_HEIGHT = 800;

  public static final int INITIAL_ASTEROID_COUNT = 10;
  public static final double INITIAL_ASTEROID_SIZE = 1;
  public static final double MINIMAL_ASTEROID_SIZE = 0.5;
  public static final double NUMBER_ASTEROID_FRAGMENTS = 2;
  public static final double INITIAL_AND_FRAGMENT_RATIO = 0.5;
  /**
   * We don't want asteroids to spawn on the spaceship. This parameter
   * controls how close an asteroid can be from the spaceship initially,
   * in pixels.
   */
  private static final double STARTING_SECURITY_DISTANCE = 80;

  /**
   * An object able to create random items, like asteroids or positions.
   */
  public static final RandomGenerator generator = new RandomGenerator();

  private final Spaceship spaceship;
  private final List<Asteroid> asteroids;

  private final ArrayList<Projectile> projectiles;
  private final Score score;


  public Space() {
    score =new Score();
    spaceship = new Spaceship();
    asteroids = new ArrayList<>(INITIAL_ASTEROID_COUNT);
    for (int i = 0; i < INITIAL_ASTEROID_COUNT; i++) {
      asteroids.add(generateInitialAsteroid());
    }
    projectiles= new ArrayList<>();
  }

  public Spaceship getSpaceship() {
    return spaceship;
  }

  public List<Asteroid> getAsteroids() {
    return asteroids;
  }

  public Score getScore() {
    return score;
  }

  public ArrayList<Projectile> getProjectiles() {
    return projectiles;
  }

  private List<Projectile> getDeadProjectiles() {
    List<Projectile>  deadProjectiles = new ArrayList<>();
    for (Projectile projectile: projectiles) {
      if (projectile.isAlive(0))
        deadProjectiles.add(projectile);
    }
    return deadProjectiles;
  }

  public void update(double dt) {
    score.update(dt);
    for (Asteroid asteroid : asteroids) {
      asteroid.update(dt);
    }
    spaceship.update(dt);
    removeDeadProjectiles();
    processProjectiles(dt);
  }

  private  void updateProjectiles(double dt) {
    for (Projectile projectile:projectiles) {
      projectile.update(dt);
    }
  }
  public boolean isGameOver() {
    return hasCollision() && hasLives();
  }


  /**
   * Generates a random asteroid with standard parameters, whose distance
   * to the spaceship is large enough.
   *
   * @return a random asteroid
   */
  public Asteroid generateInitialAsteroid() {
    Asteroid asteroid = generator.asteroid(INITIAL_ASTEROID_SIZE);
    double distanceFromSpaceship =
      asteroid.getPosition().distanceTo(spaceship.getPosition());
    if (distanceFromSpaceship < STARTING_SECURITY_DISTANCE) {
      return generateInitialAsteroid();
    }
    return asteroid;
  }


  /**
   * Because the space is toric (things leaving the window on one side
   * reappear on the other side), we need to compute the positions of items
   * leaving the screen to get them back on the other side. This method takes
   * an arbitrary vector and maps it to valid toric coordinates.
   *
   * @param position any position
   * @return the same position with canonical toric coordinates
   */
  public static Vector toricRemap(Vector position) {
    return new Vector(
      clamp(position.getX(), SPACE_WIDTH),
      clamp(position.getY(), SPACE_HEIGHT)
    );
  }


  /**
   * Used by remapPosition to compute coordinates between 0 and a bound.
   *
   * @param value the coordinate to recompute
   * @param bound the maximum value allowed for this coordinate
   * @return the corrected coordinate
   */
  private static double clamp(double value, double bound) {
    return value - Math.floor( value / bound) * bound;
  }

  public boolean hasCollision() {
    for (Asteroid asteroid: asteroids) {
      if(spaceship.collides(asteroid)) {
        this.getSpaceship().setInvulnerability(5);
        return true;
      }
    }
    return false;
  }

  public boolean hasLives() {
    return (getSpaceship().getLifeNumbers() == 0);
  }

  public void addProjectile(Projectile projectile) {
    projectiles.add(projectile);
  }

  private void removeDeadProjectiles() {
      projectiles.removeAll(getDeadProjectiles());
  }

  private void fragment(Set<Asteroid> hittedAsteroids) {
    for (Asteroid asteroid : hittedAsteroids) {
      asteroids.addAll(asteroid.fragments());
      getScore().notifyAsteroidHit();
    }
    asteroids.removeAll(hittedAsteroids);

  }

  private void remove(Set<Projectile> hittingProjectiles) {
    projectiles.removeAll(hittingProjectiles);
  }

  private void findProjectileHits(Set<Projectile> hittingProjectiles, Set<Asteroid> hittedAsteroids) {
    for (Projectile projectile :projectiles) {
      for (Asteroid asteroid : asteroids) {
        if (projectile.collides(asteroid)) {
          hittingProjectiles.add(projectile);
          hittedAsteroids.add(asteroid);
        }
      }
    }
  }

  public void processProjectiles(double dt) {
    updateProjectiles(dt);
    Set<Projectile> hittingProjectiles = new HashSet<>();
    Set<Asteroid> hittedAsteroids = new HashSet<>();
    findProjectileHits(hittingProjectiles, hittedAsteroids);
    remove(hittingProjectiles);
    fragment(hittedAsteroids);
  }


}
