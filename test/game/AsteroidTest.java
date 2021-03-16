package game;

import tools.Polygon;
import tools.Vector;

import java.util.List;

import static org.junit.Assert.*;

public class AsteroidTest {

    @org.junit.Test
    public void contains() {
        Polygon shape = new Polygon(List.of(
                new Vector(-5, -5),
                new Vector(5, -5),
                new Vector(4, 2),
                new Vector(-6, 4)
        ));
        Asteroid asteroid = new Asteroid(
                new Vector(40,15),
                shape, new Vector(0,0), 90, 1);

        assertTrue(asteroid.contains(new Vector(40,15)));
        assertFalse(asteroid.contains(new Vector(20,45)));

    }

}