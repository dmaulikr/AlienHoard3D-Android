package nz.ac.aut.alienhoard3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import java.util.Vector;

/**
 * Created by jony on 24/09/15.
 */
public class Point {

    // Define a simple shader program for our point.
    final static String pointVertexShader =
            "uniform mat4 u_MVPMatrix;      \n"
                    + "attribute vec4 a_Position;     \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_Position = u_MVPMatrix   \n"
                    + "               * a_Position;   \n"
                    + "   gl_PointSize = 5.0;         \n"
                    + "}                              \n";

    final static String pointFragmentShader =
            "precision mediump float;       \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_FragColor = vec4(1.0,    \n"
                    + "   1.0, 1.0, 1.0);             \n"
                    + "}                              \n";

    private int pointVertexShaderHandle;
    private int pointFragmentShaderHandle;

    private int pointProgram;

    private int pointMVPMatrixHandle;
    private int pointPositionHandle;

    private Vector3d position;
    private Vector3d velocity;

    private float[] model;

    private float[] mMVPMatrix = new float[16];
    private float[] mLightModelMatrix = new float[16];

    public Point()
    {
        position = new Vector3d();
    }

    public void Initialise(int pointVertexHandle, int pointFragmentHandle)
    {
        pointVertexShaderHandle = pointVertexHandle;
        pointFragmentShaderHandle = pointFragmentHandle;

        pointProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(pointProgram, pointVertexShaderHandle);
        GLES20.glAttachShader(pointProgram, pointFragmentShaderHandle);
        GLES20.glLinkProgram(pointProgram);
        GLES20.glUseProgram(pointProgram);

        MainActivity.checkGLError("99Point");

        pointMVPMatrixHandle = GLES20.glGetUniformLocation(pointProgram, "u_MVPMatrix2");

        MainActivity.checkGLError("Poin55t");

        pointPositionHandle = GLES20.glGetUniformLocation(pointProgram, "a_Position2");

        MainActivity.checkGLError("Point");




    }

    public void Draw(float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        // Pass in the position of this point
        GLES20.glVertexAttrib3f(pointPositionHandle, (float)(position.x), (float)(position.y), (float)(position.z));

        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Passin the transformation matrix
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, (float) (position.x), (float) (position.y), (float) (position.z));

        Matrix.multiplyMM(mMVPMatrix, 0, view, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, modelViewProjection, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    public void setPosition(float x, float y, float z)
    {
        position.set(x, y, z);
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, (float)(position.x), (float)(position.y), (float)(position.z));
    }

}
