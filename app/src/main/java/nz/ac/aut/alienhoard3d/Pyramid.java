package nz.ac.aut.alienhoard3d;

/**
 * Created by jony on 24/09/15.
 */
public class Pyramid {
    public static final float[] pyramid = new float[] {
            0.0f, 5.0f, 0.0f,
            -5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, -5.0f,

            0.0f, 5.0f, 0.0f,
            5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, 5.0f,

            0.0f, 5.0f, 0.0f,
            5.0f, 0.0f, 5.0f,
            -5.0f, 0.0f, 5.0f,

            0.0f, 5.0f, 0.0f,
            -5.0f, 0.0f, 5.0f,
            -5.0f, 0.0f, -5.0f,

            -5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, 5.0f,

            -5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, 5.0f,
            -5.0f, 0.0f, 5.0f,
    };

    public static final float[] pyramid_norms = new float[] {
            0.0f, 0.5f, -0.5f,
            0.0f, 0.5f, -0.5f,
            0.0f, 0.5f, -0.5f,

            0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,

            0.0f, 0.5f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.0f, 0.5f, 0.5f,

            -0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,

            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
    };

    public static final float[] pyramid_colours = new float[] {
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
    };
}
