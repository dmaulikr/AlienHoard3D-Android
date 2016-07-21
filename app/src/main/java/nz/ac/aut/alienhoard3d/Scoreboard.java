package nz.ac.aut.alienhoard3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by jony on 1/10/15.
 */
public class Scoreboard extends Sign{
    String message = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Context context = null;
    public Scoreboard()
    {
        super();
    }

    public Scoreboard(Model model)
    {
        super(model);
    }

    public void Draw(float[] modelView, float[] modelViewProjection,
                     float[] view, float[] perspective, float[] lightPosInEyeSpace)
    {
        int[] textTextures = new int[1];
        GLES20.glGenTextures(textTextures.length, textTextures, 0);

// Create an empty, mutable bitmap
            Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
// get a canvas to paint over the bitmap
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(0);

// get a background image from resources
// note the image format must match the bitmap format
            Drawable background = context.getResources().getDrawable(R.drawable.background);
            background.setBounds(0, 0, 256, 256);
            background.draw(canvas); // draw the background to our bitmap

// Draw the text
            Paint textPaint = new Paint();
            textPaint.setTextSize(32);
            textPaint.setAntiAlias(true);
            textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
// draw the text centered
            canvas.drawText(message, 16, 112, textPaint);

//Generate one texture pointer...

//...and bind it to our array
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textTextures[0]);

//Create Nearest Filtered Texture
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();

        this.model.setTexture(textTextures[0]);

        super.Draw(modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);
    }


}
