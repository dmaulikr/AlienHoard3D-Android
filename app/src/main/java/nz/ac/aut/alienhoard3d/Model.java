package nz.ac.aut.alienhoard3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by jony on 22/09/15.
 */
public class Model {
    private FloatBuffer Vertices;
    private FloatBuffer Colors;
    private FloatBuffer Normals;

    // This holds the specific shader program for this Model
    private int Program;

    private int PositionParam;
    private int NormalParam;
    private int ColorParam;
    private int ModelParam;
    private int ModelViewParam;
    private int ModelViewProjectionParam;
    private int LightPosParam;

    private int Radius;

    /** Store our model data in a float buffer. */
    private FloatBuffer mCubeTextureCoordinates;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;

    private int numPoints = 0;
    private static final int COORDS_PER_VERTEX = 3;

    public Model()
    {
        // Initialise this Model




    }

    public void createByteBuffers(EGLConfig config, float[] coords, float[] normals, float[] colours, float[] texture)
    {
        numPoints = coords.length;

        // Create all the ByteBuffer's to hold values for OpenGL ES
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(coords.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        Vertices = bbFloorVertices.asFloatBuffer();
        Vertices.put(coords);
        Vertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(normals.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        Normals = bbFloorNormals.asFloatBuffer();
        Normals.put(normals);
        Normals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(colours.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        Colors = bbFloorColors.asFloatBuffer();
        Colors.put(colours);
        Colors.position(0);

        if(texture != null)

        {
            ByteBuffer bbTexture = ByteBuffer.allocateDirect(texture.length * 4);
            bbTexture.order(ByteOrder.nativeOrder());
            mCubeTextureCoordinates = bbTexture.asFloatBuffer();
            mCubeTextureCoordinates.put(texture);
            mCubeTextureCoordinates.position(0);
        }
    }

    public void initProgram(int vertexShader, int gridShader)
    {
        Program = GLES20.glCreateProgram();
        GLES20.glAttachShader(Program, vertexShader);
        GLES20.glAttachShader(Program, gridShader);
        GLES20.glLinkProgram(Program);
        GLES20.glUseProgram(Program);

        ModelParam = GLES20.glGetUniformLocation(Program, "u_Model");
        ModelViewParam = GLES20.glGetUniformLocation(Program, "u_MVMatrix");
        ModelViewProjectionParam = GLES20.glGetUniformLocation(Program, "u_MVP");
        LightPosParam = GLES20.glGetUniformLocation(Program, "u_LightPos");

        PositionParam = GLES20.glGetAttribLocation(Program, "a_Position");
        NormalParam = GLES20.glGetAttribLocation(Program, "a_Normal");
        ColorParam = GLES20.glGetAttribLocation(Program, "a_Color");

        mTextureUniformHandle = GLES20.glGetUniformLocation(Program, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(Program, "a_TexCoordinate");



        GLES20.glEnableVertexAttribArray(PositionParam);
        GLES20.glEnableVertexAttribArray(NormalParam);
        GLES20.glEnableVertexAttribArray(ColorParam);
    }

    public void setTexture(int textureDataHandle)
    {
        mTextureDataHandle = textureDataHandle;
    }

    public void processFrame(float delta)
    {

    }

    public void Draw(float[] model, float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0,
                modelView, 0);



        GLES20.glUseProgram(Program);

        //mTextureCoordinateHandle

            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(mTextureUniformHandle, 0);


        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(LightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(ModelParam, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(ModelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(ModelViewProjectionParam, 1, false,
                modelViewProjection, 0);
        GLES20.glVertexAttribPointer(PositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, Vertices);
        GLES20.glVertexAttribPointer(NormalParam, 3, GLES20.GL_FLOAT, false, 0,
                Normals);
        GLES20.glVertexAttribPointer(ColorParam, 4, GLES20.GL_FLOAT, false, 0, Colors);

// Pass in the texture coordinate information
        if(mCubeTextureCoordinates != null) {
            mCubeTextureCoordinates.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                    0, mCubeTextureCoordinates);

            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numPoints / 3);
    }
}
