package nz.ac.aut.alienhoard3d;

import android.opengl.GLES20;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import java.util.Vector;

/**
 * Created by jony on 30/09/15.
 */
public class Particle extends Entity{

    private Camera camera;

    protected float maxAge = 0.0f;

    public float getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(float currentAge) {
        this.currentAge = currentAge;
    }

    public float getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(float maxAge) {
        this.maxAge = maxAge;
    }

    protected float currentAge = 0.0f;

    public Particle()
    {
        super();
    }

    public Particle(Model model)
    {
        super(model);
    }

    public void Process(float delta)
    {
        super.Process(delta);

        if(position.y < 0.0f)
        {
            isAlive = false;
        }
        if(maxAge > 0.0f && currentAge > maxAge)
        {
            isAlive = false;
        }
    }

    public void Draw(float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        Vector3d lookAt = new Vector3d(0.0,0.0,1.0);
        Vector3d objToCamProj = new Vector3d(camera.eyeX - position.x,0.0,camera.eyeZ - position.z);
        Vector3d upAex= new Vector3d(0.0,0.0,0.0);



        double angleCosine;

        objToCamProj.normalize();

        Vector3d.cross(lookAt,objToCamProj,upAex);

        angleCosine = (lookAt.x*objToCamProj.x) + (lookAt.y*objToCamProj.y) + (lookAt.z*objToCamProj.z);

        if(angleCosine < 0.99990 && angleCosine > -0.99990)
        {
            this.rotate((float)(Math.acos(angleCosine)*180.0f/3.14f), (float)(upAex.x), (float)(upAex.y),(float)(upAex.z));
        }


        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        super.Draw(modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
