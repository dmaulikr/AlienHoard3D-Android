package nz.ac.aut.alienhoard3d;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by jony on 24/09/15.
 */
public class CubeModelMaker {
    public float[] vertices;
    public float[] normals;
    public float[] colours;

    // Member Methods
    public CubeModelMaker(float Height, float Width, float[] colors)
    {
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
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],

                // right, blue
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],

                // back, also green
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],

                // left, also blue
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],

//                // top, red
//                0.0f,0.0f,0.0f,1.0f,
//                0.0f,0.0f,0.0f,1.0f,
//                0.0f,0.0f,0.0f,1.0f,
//                0.0f,0.0f,0.0f,1.0f,
//                0.0f,0.0f,0.0f,1.0f,
//                0.0f,0.0f,0.0f,1.0f,

                // bottom, also red
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
                colors[0], colors[1], colors[2], colors[3],
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

        public static final float[] texture =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

}
