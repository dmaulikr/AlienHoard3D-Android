package nz.ac.aut.alienhoard3d;

import com.google.vrtoolkit.cardboard.HeadTransform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by jony on 30/09/15.
 */
public class Emitter extends Entity{
    protected ArrayList<Particle> particles = new ArrayList<>();

    private float spawnTimer = 0.0f;

    public float getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(float maxAge) {
        this.maxAge = maxAge;
    }

    public float getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(float currentAge) {
        this.currentAge = currentAge;
    }

    protected float maxAge = 0.0f;
    protected float currentAge = 0.0f;

    private Camera camera;

    public float getSpawnDelay() {
        return spawnDelay;
    }

    public void setSpawnDelay(float spawnDelay) {
        this.spawnDelay = spawnDelay;
    }

    public boolean isSpawning() {
        return isSpawning;
    }

    public void setIsSpawning(boolean isSpawning) {
        this.isSpawning = isSpawning;
    }

    private float spawnDelay = 0.0f;
    private boolean isSpawning = false;

    public Emitter()
    {
        super();
    }

    public Emitter(Model model)
    {
        super(model);
    }

    public void Process(float delta)
    {
        super.Process(delta);

        spawnTimer += delta;
        currentAge += delta;
        if(isSpawning && spawnTimer > spawnDelay)
        {
            spawnTimer = 0.0f;
            SpawnParticle();
        }

        if(maxAge > 0.0f && currentAge > maxAge)
        {
            isAlive = false;
        }



        Iterator<Particle> particleIterator = particles.iterator();
        while(particleIterator.hasNext())
        {
            Particle p = particleIterator.next();
            if(p.isAlive()) {
                p.Process(delta);
            }
            else
            {
                particleIterator.remove();
            }
        }
    }
    
    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public void Draw(float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        // Pass the camera object to the particles so they can orient to face it
        Iterator<Particle> particleIterator = particles.iterator();
        while(particleIterator.hasNext())
        {
            Particle p = particleIterator.next();
            p.setCamera(camera);
            p.Draw(modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);
        }
    }

    public void SpawnParticle()
    {
        Particle p = new Particle(model);
        Random r = new Random();
        float posX = (r.nextFloat() * 200.0f) - 100.0f;
        float posZ = (r.nextFloat() * 200.0f) - 100.0f;
        p.setPosition(posX, position.y, posZ);
        p.setVelocity(0.0f, -0.5f, 0.0f);

        particles.add(p);
    }
}
