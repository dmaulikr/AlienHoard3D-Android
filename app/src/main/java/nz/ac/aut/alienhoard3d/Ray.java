package nz.ac.aut.alienhoard3d;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by jony on 24/09/15.
 */
public class Ray extends Entity {
        float[] vertices;
        float[] normals;
        float[] colours;

        // Set a timer for how long Ray entity can be alive for
        public float mTimer = 0.0f;
        public float mLifeLimit = 5.0f;

    public boolean isFiredByPlayer() {
        return isFiredByPlayer;
    }

    public void setIsFiredByPlayer(boolean isFiredByPlayer) {
        this.isFiredByPlayer = isFiredByPlayer;
    }

    public boolean isFiredByPlayer = true;

        public Ray()
        {
                float Width = 0.2f;
                float Height = 0.9f;

                vertices = new float[] {
                        // Front face
                        -Width, Height, Width,
                        -Width, -Height, Width,
                        Width, Height, Width,
                        -Width, -Height, Width,
                        Width, -Height, Width,
                        Width, Height, Width,

                        // Right face
                        Width, Height, Width,
                        Width, -Height, Width,
                        Width, Height, -Width,
                        Width, -Height, Width,
                        Width, -Height, -Width,
                        Width, Height, -Width,

                        // Back face
                        Width, Height, -Width,
                        Width, -Height, -Width,
                        -Width, Height, -Width,
                        Width, -Height, -Width,
                        -Width, -Height, -Width,
                        -Width, Height, -Width,

                        // Left face
                        -Width, Height, -Width,
                        -Width, -Height, -Width,
                        -Width, Height, Width,
                        -Width, -Height, -Width,
                        -Width, -Height, Width,
                        -Width, Height, Width,

                        // Top face
                        -Width, Height, -Width,
                        -Width, Height, Width,
                        Width, Height, -Width,
                        -Width, Height, Width,
                        Width, Height, Width,
                        Width, Height, -Width,

                        // Bottom face
                        Width, -Height, -Width,
                        Width, -Height, Width,
                        -Width, -Height, -Width,
                        Width, -Height, Width,
                        -Width, -Height, Width,
                        -Width, -Height, -Width,
                };

                colours = new float[] {
                        // front, green
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,

                        // right, blue
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,

                        // back, also green
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,

                        // left, also blue
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,

                        // top, red
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,

                        // bottom, also red
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                };

                normals = new float[] {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };
        }

        public void Initialise(EGLConfig config)
        {

        }

        public void Process(float delta)
        {
                mTimer += delta;

                if(mTimer > mLifeLimit)
                {
                        isAlive = false;
                }

                // Check to see if we are below the map if we are respawn to the alien ship that fired us
                super.Process(delta);

        }
}

