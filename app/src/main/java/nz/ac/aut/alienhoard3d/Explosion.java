package nz.ac.aut.alienhoard3d;

import java.util.Random;

/**
 * Created by jony on 1/10/15.
 */
public class Explosion extends Emitter {

    public Explosion()
    {
        super();
    }

    public Explosion(Model model)
    {
        super(model);
    }

    public void SpawnParticle()
    {
        // Spawn 20 particles that will be spread out randomly from the emitters position
        Shrapel p = new Shrapel(model);
        Random r = new Random();
        p.setPosition(position.x, position.y, position.z);
        p.setVelocity((r.nextFloat() * 2.0f) - 1.0f, (r.nextFloat() * 2.0f) - 1.0f, (r.nextFloat() * 2.0f) - 1.0f);
        p.setMaxAge(5.0f);

        particles.add(p);
    }
}
