package nz.ac.aut.alienhoard3d;

import android.opengl.Matrix;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by jony on 24/09/15.
 */
public class Alien extends Entity {

    public boolean isShouldFire() {
        return shouldFire;
    }

    public void setShouldFire(boolean shouldFire) {
        this.shouldFire = shouldFire;
    }

    public float getTimerDelay() {
        return timerDelay;
    }

    public void setTimerDelay(float timerDelay) {
        this.timerDelay = timerDelay;
    }

    boolean shouldFire = false;
    float timerDelay = 0.0f;
    float timer = 0.0f;

    public Alien()
    {
        super();

        Random r = new Random();

        timerDelay = 2.0f + 2.0f * r.nextFloat();
    }

    @Override
    public void Process(float delta)
    {
        super.Process(delta);
        Matrix.rotateM(modelMatrix, 0, 180, 1.0f, 0.0f, 0.0f);

        timer += delta;
        if(timer > timerDelay)
        {
            timer = 0.0f;
            shouldFire = true;
        }
    }
}
