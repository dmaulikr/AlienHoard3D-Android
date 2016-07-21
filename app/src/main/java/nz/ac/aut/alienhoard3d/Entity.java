package nz.ac.aut.alienhoard3d;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

/**
 * Created by jony on 27/09/15.
 */
public class Entity {
    // Main object class

    // The model used to draw this object
    protected Model model = null;
    protected Vector3d position;
    protected Vector3d velocity;

    private Vector3d rotationAxis;
    private float rotationDelta;
    private float spin;

    protected float[] modelMatrix;

    private float Radius = 0;

    boolean isCollidiable = false;

    boolean isAlive = true;

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public Entity()
    {
        position = new Vector3d();
        position.setZero();
        velocity = new Vector3d();
        velocity.setZero();
        rotationAxis = new Vector3d();
        rotationAxis.setZero();
        rotationDelta = 0.0f;

        modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix, 0, 0, 0, 0);
    }

    public Entity(Model model)
    {
        this.model = model;

        position = new Vector3d();
        position.setZero();
        velocity = new Vector3d();
        velocity.setZero();
        rotationAxis = new Vector3d();
        rotationAxis.x = 0.0f;
        rotationAxis.y = 1.0f;
        rotationAxis.z = 0.0f;
        rotationDelta = 0.0f;
        spin = 0.0f;

        modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix, 0, 0, 0, 0);
    }

    public boolean getCollidiable()
    {
        return isCollidiable;
    }

    public void setCollidiable(boolean collidiable)
    {
        this.isCollidiable = collidiable;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public Model getModel(Model model)
    {
        return this.model;
    }

    public void setRadius(float radius)
    {
        Radius = radius;
    }

    public float getRadius()
    {
        return Radius;
    }

    public void Process(float delta)
    {
        Vector3d.add(position, velocity, position);

        rotationDelta += spin;

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, (float) (position.x), (float) (position.y), (float) (position.z)); // Floor appears below user.
        Matrix.rotateM(modelMatrix, 0, rotationDelta, (float) (rotationAxis.x), (float) (rotationAxis.y), (float) (rotationAxis.z));
    }

    public void setPosition(float x, float y, float z)
    {
        position.set(x, y, z);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, (float) (position.x), (float) (position.y), (float) (position.z)); // Floor appears below user.
        Matrix.rotateM(modelMatrix, 0, rotationDelta, (float)(rotationAxis.x), (float)(rotationAxis.y), (float)(rotationAxis.z));
    }

    public void setVelocity(float x, float y, float z)
    {
        velocity.set(x, y, z);
    }

    public void Draw(float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        model.Draw(modelMatrix, modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);
    }

    public Vector3d getPosition()
    {
        return position;
    }

    public Vector3d getVelocity()
    {
        return velocity;
    }

    public void rotate(float deltaRadians, float x, float y, float z)
    {
        rotationAxis.set(x,y,z);
        rotationDelta = deltaRadians;
    }

    public float getRotation()
    {
        return rotationDelta;
    }

    public float getSpin()
    {
        return spin;
    }

    public void setSpin(float spin)
    {
        this.spin = spin;
    }

    public boolean isColliding(Entity other)
    {
        boolean isColl = false;
        if(this.getCollidiable() == false){ return isColl; };
        if(this.equals(other)){ return isColl; };
        if(this instanceof Ray && other instanceof Ray){ return isColl;}

        float radiiDistance = other.getRadius() + this.getRadius();

        Vector3d vec3 = new Vector3d();
        vec3.set(Math.abs(this.getPosition().x - other.getPosition().x),
                Math.abs(this.getPosition().y - other.getPosition().y),
                Math.abs(this.getPosition().z - other.getPosition().z));
        double length = vec3.length();

        if((length - radiiDistance) < 0.0f)
        {
            // Objects are colliding return true
            isColl = true;
        }


        return isColl;
    }

    public void setPosition(double x, double y, double z) {
        position.set(x, y, z);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, (float) (position.x), (float) (position.y), (float) (position.z)); // Floor appears below user.
        Matrix.rotateM(modelMatrix, 0, rotationDelta, (float)(rotationAxis.x), (float)(rotationAxis.y), (float)(rotationAxis.z));
    }
}
