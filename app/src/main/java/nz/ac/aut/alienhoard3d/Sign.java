package nz.ac.aut.alienhoard3d;

/**
 * Created by jony on 29/09/15.
 */
public class Sign extends Entity {
    static float[] vertices;
    static float[] normals;
    static float[] colours;
    static float[] textures;

    public static void vertexData(float fWidth, float fHeight)
    {
        vertices = new float[]{
                -fWidth, fHeight, 0.0f,
                -fWidth, -fHeight, 0.0f,
                fWidth, fHeight, 0.0f,
                -fWidth, -fHeight, 0.0f,
                fWidth, -fHeight, 0.0f,
                fWidth, fHeight, 0.0f,
        };

        normals = new float[]{
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
        };

        textures = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };

        colours = new float[]{
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
        };
    }

    public Sign()
    {
        super();
    }

    public Sign(Model model)
    {
        super(model);
    }
}
