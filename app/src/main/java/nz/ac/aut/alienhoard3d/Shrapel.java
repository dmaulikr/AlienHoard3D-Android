package nz.ac.aut.alienhoard3d;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

/**
 * Created by jony on 1/10/15.
 */
public class Shrapel extends Particle{
    private Vector3d acc;

    public Shrapel()
    {
        acc = new Vector3d(0.0, -0.01, 0.0);
    }

    public Shrapel(Model model)
    {
        super(model);
        acc = new Vector3d(0.0, -0.01, 0.0);
    }

    public void Process(float delta)
    {
        Vector3d.add(velocity, acc, velocity);

        super.Process(delta);
    }
}
