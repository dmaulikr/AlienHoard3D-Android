package nz.ac.aut.alienhoard3d;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Matrix4f;
import android.sax.EndElementListener;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity";

    public enum State {
        GAME_MENU,
        GAME_RUNNING,
        GAME_OVER
    };

    private float[] camera;

    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000.0f;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 3.5f, 0.0f, 1.0f };

    private final float[] lightPosInEyeSpace = new float[4];

    private CardboardOverlayView overlayView;

    private State mCurrentState = State.GAME_MENU;
    int vertexShader;// = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
    int gridShader;// = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
    int gridShader2;// = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment2);
    int passthroughShader;// = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);
    int passthroughShaderTexture;// = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment_texture);


    ArrayList<Entity> allEntities = new ArrayList<>();
    ArrayList<Entity> bulletEntities = new ArrayList<>();
    ArrayList<Entity> menuEntities= new ArrayList<>();
    Sign signButton = null;

    ArrayList<Emitter> emitters = new ArrayList<>();

    Model rayModel;
    Model alienModel;

    private float oldtime = 0.0f;
    private float newtime = 0.0f;
    private float spawnTimer = 0.0f;
    private float spawnDelay = 3.0f;

    HeadTransform headTransform;

    Camera cameraObj;

    Vector3d[] AlienSpawnLocations = new Vector3d[] {
            new Vector3d(-50.0, 60.0, 50.0),
            new Vector3d(-50.0, 60.0, 0.0),
            new Vector3d(-50.0, 60.0, -50.0),
            new Vector3d(0.0, 60.0, 50.0),
            new Vector3d(0.0, 60.0, 0.0),
            new Vector3d(0.0, 60.0, -50.0),
            new Vector3d(50.0, 60.0, 50.0),
            new Vector3d(50.0, 60.0, 0.0),
            new Vector3d(50.0, 60.0, -50.0),
    };

    Alien[] aliens = new Alien[] {
            null, null, null,
            null, null, null,
            null, null, null
    };

    Random r = new Random();


    Emitter starEmitter = null;

    Scoreboard remainingTime = null;
    Scoreboard totalScore = null;

    float timeElapsed = 0.0f;
    float gameLength = 60.0f;
    int totalKills = 0;


    SoundPool sp = null;
    int soundIds[] = new int[2];

    Model exploModel = null;

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        headView = new float[16];


        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Hello World :-)");

        newtime = System.nanoTime();
        oldtime = System.nanoTime();
    }


    /**
     * Creates the buffers we use to store information about the 3D world.
     *
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.53f, 0.80f, 0.92f, 0.5f); // Dark background so text shows up well.

        // Shaders for the models
        vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);
        passthroughShaderTexture = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment_texture);


        // Shaders for Points
        int pointVertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.point_vertex);
        int pointFragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.point_fragment);

        MainActivity.checkGLError("Initial Graphics Setup");

        // Initialise the objects for the menu and the actual game
        InitialiseMenu(config);
        InitialiseGame(config);

        cameraObj = new Camera();
        // Set the camera's position
        cameraObj.eyeX = 0.0f;
        cameraObj.eyeY = 5.0f;
        cameraObj.eyeZ = 0.0f;

        cameraObj.lookX = 0.0f;
        cameraObj.lookY = 5.0f;
        cameraObj.lookZ = 1.0f;

        cameraObj.upX = 0.0f;
        cameraObj.upY = 1.0f;
        cameraObj.upZ = 1.0f;


        loadSounds();

        checkGLError("onSurfaceCreated");
    }

    private void InitialiseMenu(EGLConfig config)
    {
        // Generate the geometry for the Signs
        Sign.vertexData(3.0f, 2.0f);

        // Load the textures
        int[] textures = loadTexture(this, R.drawable.turnaround);

        // Create the model for this sign and create the sign object
        Model turnAroundModel = new Model();
        turnAroundModel.initProgram(vertexShader, passthroughShaderTexture);
        turnAroundModel.createByteBuffers(config,
                Sign.vertices,
                Sign.normals,
                Sign.colours,
                Sign.textures);
        turnAroundModel.setTexture(textures[0]);

        Sign turnAroundSign = new Sign(turnAroundModel);
        turnAroundSign.setPosition(0.0f, 3.0f, -10.0f);
        //turnAroundSign.rotate(180.0f, 0.0f, 1.0f, 0.0f);

        menuEntities.add(turnAroundSign);

        // Load the textures
        textures = loadTexture(this, R.drawable.startsign1);

        // Create the model for this sign and create the sign object
        Model startGameModel = new Model();
        startGameModel.initProgram(vertexShader, passthroughShaderTexture);
        startGameModel.createByteBuffers(config,
                Sign.vertices,
                Sign.normals,
                Sign.colours,
                Sign.textures);
        startGameModel.setTexture(textures[0]);

        Sign startGameSign = new Sign(startGameModel);
        startGameSign.setPosition(4.0f, 3.0f, 10.0f);
        startGameSign.rotate(200.0f, 0.0f, 1.0f, 0.0f);

        menuEntities.add(startGameSign);

        // Load the textures
        textures = loadTexture(this, R.drawable.startsign);

        // Create the model for this sign and create the sign object
        Model infoGameModel = new Model();
        infoGameModel.initProgram(vertexShader, passthroughShaderTexture);
        infoGameModel.createByteBuffers(config,
                Sign.vertices,
                Sign.normals,
                Sign.colours,
                Sign.textures);
        infoGameModel.setTexture(textures[0]);

        Sign infoGameSign = new Sign(infoGameModel);
        infoGameSign.setPosition(-5.0f, 3.0f, 10.0f);
        infoGameSign.rotate(160.0f, 0.0f, 1.0f, 0.0f);
        infoGameSign.setCollidiable(true);
        signButton = infoGameSign;

        menuEntities.add(infoGameSign);


        textures = loadTexture(this, R.drawable.part);

        Model starParticle = new Model();
        float[] vert = new float[]{                // Front face
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,};
        float[] normal = new float[]{
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f        };
        float[] colour = new float[]{
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,};
        float[] texture = new float[]{                         0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,};
        starParticle.initProgram(vertexShader, passthroughShaderTexture);
        starParticle.createByteBuffers(config, vert, normal, colour, texture);
        starParticle.setTexture(textures[0]);
        starEmitter = new Emitter(starParticle);
        starEmitter.setPosition(0.0f, 100.0f, 0.0f);
        starEmitter.setIsSpawning(true);
        starEmitter.setSpawnDelay(0.4f);
        starEmitter.setCamera(cameraObj);
        emitters.add(starEmitter);

        exploModel = new Model();
        textures = loadTexture(this, R.drawable.shrap);
        exploModel.initProgram(vertexShader, passthroughShaderTexture);
        exploModel.createByteBuffers(config, vert, normal, colour, texture);
        exploModel.setTexture(textures[0]);
    }

    private void loadSounds()
    {
        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        soundIds[0] = sp.load(getApplicationContext(), R.raw.fire,1);
        soundIds[1] = sp.load(getApplicationContext(), R.raw.hit,1);
    }

    private void InitialiseGame(EGLConfig config)
    {
        // Load Textures
        int[] textures = loadTexture(this, R.drawable.rusty);

        // Create and initialise the floor model, floor entity, set inital position, and add it
        // to the entity container
        Model floorModel = new Model();
        floorModel.initProgram(vertexShader, passthroughShaderTexture);
        floorModel.createByteBuffers(config,
                WorldLayoutData.FLOOR_COORDS,
                WorldLayoutData.FLOOR_NORMALS,
                WorldLayoutData.FLOOR_COLORS,
                WorldLayoutData.FLOOR_TEXTURE);
        floorModel.setTexture(textures[0]);

        Entity floorEntity = new Entity(floorModel);
        floorEntity.setPosition(0.0f, 0.0f, 0.0f);

        allEntities.add(floorEntity);
        menuEntities.add(floorEntity);

        // Create an initialise an enemy model
        alienModel = new Model();
        alienModel.initProgram(vertexShader, passthroughShader);
        alienModel.createByteBuffers(config,
                Pyramid.pyramid,
                Pyramid.pyramid_norms,
                Pyramid.pyramid_colours,
                null);

        for(int i = 0; i < AlienSpawnLocations.length; ++i) {
            // Initialise some enemys
            Alien a1 = new Alien();
            a1.setModel(alienModel);
            a1.setPosition((float) (AlienSpawnLocations[i].x), (float) (AlienSpawnLocations[i].y), (float) (AlienSpawnLocations[i].z));
            a1.rotate(0.0f, 0.0f, 1.0f, 0.0f);
            a1.setSpin(1.0f);
            a1.setCollidiable(true);
            a1.setRadius(1.0f);
            aliens[i] = a1;
            allEntities.add(a1);
        }

        // Initialise the different building models
        int[] buildingTexture = loadTexture(this, R.drawable.building);
        CubeModelMaker buildingOne = new CubeModelMaker(20.0f,10.0f,new float[]{1.0f,1.0f,1.0f,1.0f});
        CubeModelMaker buildingTwo = new CubeModelMaker(10.0f,7.0f,new float[]{1.0f,1.0f,1.0f,1.0f});
        CubeModelMaker buildingThree = new CubeModelMaker(5.0f,3.0f,new float[]{1.0f,1.0f,1.0f,1.0f});
        CubeModelMaker buildingFour = new CubeModelMaker(10.0f,8.0f,new float[]{1.0f,1.0f,1.0f,1.0f});

        Model buildingOneModel = new Model();
        buildingOneModel.createByteBuffers(config,
                buildingOne.vertices,
                buildingOne.normals,
                buildingOne.colours,
                buildingOne.texture);
        buildingOneModel.initProgram(vertexShader, passthroughShaderTexture);
        buildingOneModel.setTexture(buildingTexture[0]);
        Model buildingTwoModel = new Model();
        buildingTwoModel.createByteBuffers(config,
                buildingTwo.vertices,
                buildingTwo.normals,
                buildingTwo.colours,
                buildingTwo.texture);
        buildingTwoModel.initProgram(vertexShader, passthroughShaderTexture);
        buildingTwoModel.setTexture(buildingTexture[0]);
        Model buildingThreeModel = new Model();
        buildingThreeModel.setTexture(buildingTexture[0]);
        buildingThreeModel.createByteBuffers(config,
                buildingThree.vertices,
                buildingThree.normals,
                buildingThree.colours,
                buildingThree.texture);
        buildingThreeModel.initProgram(vertexShader, passthroughShaderTexture);
        buildingThreeModel.initProgram(vertexShader, passthroughShaderTexture);
        Model buildingFourModel = new Model();
        buildingFourModel.setTexture(buildingTexture[0]);
        buildingFourModel.createByteBuffers(config,
                buildingFour.vertices,
                buildingFour.normals,
                buildingFour.colours,
                buildingFour.texture);
        buildingFourModel.initProgram(vertexShader, passthroughShaderTexture);
        buildingFourModel.initProgram(vertexShader, passthroughShaderTexture);


        float startZ = -80.0f;
        float incrementZ = 30.0f;
        for(int i = 0; i < 4; ++i) {
            Entity b1 = new Entity(buildingOneModel);
            b1.setPosition(-30.0f, 11.0f, startZ+incrementZ*(float)(i));
            Entity b2 = new Entity(buildingTwoModel);
            b2.setPosition(-10.0f, 8.0f, startZ+incrementZ*(float)(i));
            Entity b3 = new Entity(buildingThreeModel);
            b3.setPosition(10.0f, 8.0f, startZ+incrementZ*(float)(i));
            Entity b4 = new Entity(buildingFourModel);
            b4.setPosition(30.0f, 8.0f, startZ+incrementZ*(float)(i));

            allEntities.add(b1);
            allEntities.add(b2);
            allEntities.add(b3);
            allEntities.add(b4);
        }





        rayModel = new Model();
        rayModel.initProgram(vertexShader, passthroughShader);
        CubeModelMaker rayCube = new CubeModelMaker(0.5f, 0.5f, new float[]{1.0f,0.0f,0.0f,1.0f});
        rayModel.createByteBuffers(config,
                rayCube.vertices,
                rayCube.normals,
                rayCube.colours,
                null);


        Model remainingTimeModel = new Model();
        remainingTimeModel.initProgram(vertexShader, passthroughShaderTexture);
        remainingTimeModel.createByteBuffers(config,
                Sign.vertices,
                Sign.normals,
                Sign.colours,
                Sign.textures);
        remainingTime = new Scoreboard();
        remainingTime.setModel(remainingTimeModel);
        remainingTime.setContext(getApplicationContext());
        remainingTime.setPosition(-5.0f, 0.0f, 3.0f);
        remainingTime.rotate(90, 0.0f, 1.0f, 0.0f);
        remainingTime.setMessage("120 Seconds Left");
        //allEntities.add(remainingTime);
        //menuEntities.add(remainingTime);
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    public static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Calculate the deltatick for this frame
        newtime = System.nanoTime();
        float delta = (newtime - oldtime) * 0.000000001f;
        oldtime = newtime;


        spawnTimer += delta;
        if(spawnTimer>spawnDelay)
        {
            spawnTimer = 0.0f;

            spawnAlien();
            spawnAlien();
        }

        this.headTransform = headTransform;

        starEmitter.Process(delta);

        // Process depending on which state we are in.
        switch(mCurrentState)
        {
            case GAME_MENU:
            {
                addEntities();
                cleanDeadEntities();
                processCollisionDetections();

                for(int i = 0; i < menuEntities.size(); ++i)
                {
                    menuEntities.get(i).Process(delta);
                }

                // Check to see if the player shoot the start sign
                if(!signButton.isAlive())
                {
                    mCurrentState = State.GAME_RUNNING;
                    cameraObj.eyeX = 3.0f;
                    cameraObj.eyeY = 22.0f;
                    cameraObj.eyeZ = -100.0f;

                    cameraObj.lookX = 3.0f;
                    cameraObj.lookY = 22.0f;
                    cameraObj.lookZ = 1.0f;
                }
            }
            break;
            case GAME_RUNNING:
            {
                addEntities();
                cleanDeadEntities();
                processCollisionDetections();



                for(int i = 0; i < allEntities.size(); ++i)
                {
                    Entity e = allEntities.get(i);
                    e.Process(delta);
                    if(e instanceof Alien)
                    {
                        Alien a = (Alien)(e);
                        if(a.isShouldFire())
                        {
                            // This alien wants to fire a bullet
                            a.setShouldFire(false);
                            Ray r = new Ray();
                            r.setIsFiredByPlayer(false);
                            r.setModel(rayModel);
                            r.setPosition(a.getPosition().x, a.getPosition().y, a.getPosition().z);


                            r.setVelocity(0.0f, -1.0f, 0.0f);
                            r.rotate(0.0f, 0.0f, 1.0f, 0.0f);
                            r.setSpin(-5.0f);
                            r.setCollidiable(true);
                            r.setRadius(5.0f);

                            bulletEntities.add(r);
                        }
                    }
                }

                for(int i = 0; i < emitters.size(); ++i) {
                    Emitter e = emitters.get(i);
                    e.Process(delta);
                }

                // Check to see how much longer the game length has
                // alert the player every 10 seconds
                if((int)(timeElapsed) % 10 == 0)
                {
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run() {
                            overlayView.show3DToast("Time Remaining: " + ((int)(gameLength) - (int)(timeElapsed)) + " Seconds");
                        }
                    });
                }

                if(timeElapsed > gameLength)
                {
                    // The game is over now progress display a 3D toast showing how many aliens the
                    // player killed then progress back to the main menu after 5 seconds have elasped.
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run() {
                            overlayView.show3DToast("Game Over, Total Kills " + totalKills);
                        }
                    });
                    if(timeElapsed > (gameLength + 5.0f))
                    {
                        refresh.post(new Runnable() {
                            public void run() {
                                overlayView.show3DToast("Returning to Main Menu");
                            }
                        });

                        mCurrentState = State.GAME_MENU;
                        cameraObj.eyeX = 0.0f;
                        cameraObj.eyeY = 5.0f;
                        cameraObj.eyeZ = 0.0f;

                        cameraObj.lookX = 0.0f;
                        cameraObj.lookY = 5.0f;
                        cameraObj.lookZ = 1.0f;

                        cameraObj.upX = 0.0f;
                        cameraObj.upY = 1.0f;
                        cameraObj.upZ = 1.0f;
                        totalKills = 0;
                        timeElapsed = 0.0f;

                        allEntities.removeAll(allEntities);
                        menuEntities.removeAll(menuEntities);

                        InitialiseGame(null);
                        InitialiseMenu(null);
                    }
                }

                timeElapsed += delta;
            }
            break;
            case GAME_OVER:
            {


            }
            break;
        }


        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, cameraObj.eyeX, cameraObj.eyeY, cameraObj.eyeZ,
                cameraObj.lookX, cameraObj.lookY, cameraObj.lookZ,
                cameraObj.upX, cameraObj.upY, cameraObj.upZ);
        starEmitter.setCamera(cameraObj);

        headTransform.getHeadView(headView, 0);

        checkGLError("onReadyToDraw");
    }

    private void spawnAlien() {
        int rIndex = r.nextInt(aliens.length);

        if(aliens[rIndex] != null && (!(aliens[rIndex].isAlive())))
        {
            aliens[rIndex] = new Alien();
            aliens[rIndex].setPosition(AlienSpawnLocations[rIndex].x, AlienSpawnLocations[rIndex].y, AlienSpawnLocations[rIndex].z);
            aliens[rIndex].setModel(alienModel);
            aliens[rIndex].rotate(0.0f, 0.0f, 1.0f, 0.0f);
            aliens[rIndex].setSpin(1.0f);
            aliens[rIndex].setCollidiable(true);
            aliens[rIndex].setRadius(1.0f);
            allEntities.add(aliens[rIndex]);
        }

    }

    private void addEntities() {
        switch(mCurrentState)
        {
            case GAME_MENU:
            {
                Iterator<Entity> entityIterator = bulletEntities.iterator();
                while(entityIterator.hasNext())
                {
                    Entity e = entityIterator.next();
                    entityIterator.remove();

                    menuEntities.add(e);
                }
            }
            break;
            case GAME_RUNNING:
            {
                Iterator<Entity> entityIterator = bulletEntities.iterator();
                while(entityIterator.hasNext())
                {
                    Entity e = entityIterator.next();
                    entityIterator.remove();

                    allEntities.add(e);
                }
            }
            break;
            case GAME_OVER:
            {


            }
            break;
        }
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClearColor(0.00f, 0.00f, 0.00f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        checkGLError("colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);



        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        //Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
        //Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        //drawCube();



        switch(mCurrentState)
        {
            case GAME_MENU:
            {
                for(int i = 0; i < menuEntities.size(); ++i)
                {
                    menuEntities.get(i).Draw(modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);
                }
            }
            break;
            case GAME_RUNNING:
            {
                for(int i = 0; i < allEntities.size(); ++i)
                {
                    allEntities.get(i).Draw(modelView, modelViewProjection,view,perspective,lightPosInEyeSpace);
                }
                for(int i = 0; i < emitters.size(); ++i) {
                    Emitter e = emitters.get(i);
                    e.Draw(modelView, modelViewProjection,view,perspective,lightPosInEyeSpace);
                }
            }
            break;
            case GAME_OVER:
            {


            }
            break;
        }

        //starEmitter.Draw(modelView, modelViewProjection, view, perspective, lightPosInEyeSpace);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        Ray r = new Ray();
        r.setModel(rayModel);
        r.setPosition(cameraObj.eyeX, cameraObj.eyeY, cameraObj.eyeZ);

        float[] dir = new float[]{0.0f, 0.0f, -1.0f, 0.0f};


        float[] quaternion = new float[]{
                0.0f,0.0f,0.0f,0.0f};
        float[] complexconj = new float[]{
                0.0f,0.0f,0.0f,0.0f
        };
        headTransform.getQuaternion(quaternion,0);
        complexconj[0] = -1.0f * quaternion[0];
        complexconj[1] = -1.0f * quaternion[1];
        complexconj[2] = -1.0f * quaternion[2];
        complexconj[3] = quaternion[3];

        float[] res = new float[4];
        multiQuaternion(res, dir, complexconj);
        multiQuaternion(dir, quaternion, res);

        // We need to orient the bullet to face the direction we are shooting



        r.setVelocity(-1.0f * dir[0], dir[1], -1.0f * dir[2]);
        r.rotate(0.0f, 0.0f, 1.0f, 0.0f);
        r.setSpin(5.0f);
        r.setCollidiable(true);
        r.setRadius(5.0f);


        sp.play(soundIds[0], 1, 1, 1, 0, 1.0f);

        bulletEntities.add(r);
    }

    public enum Textures {
        CUBE
    };

    public static int[] loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[Textures.values().length];

        GLES20.glGenTextures(Textures.values().length, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);



            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);



            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle;
    }

    private void cleanDeadEntities() {

        switch (mCurrentState) {
            case GAME_MENU: {
                Iterator<Entity> entityIterator = menuEntities.iterator();
                while (entityIterator.hasNext()) {
                    if (entityIterator.next().isAlive() == false) {
                        entityIterator.remove();
                    }
                }
            }
            break;
            case GAME_RUNNING: {
                Iterator<Entity> entityIterator = allEntities.iterator();
                while (entityIterator.hasNext()) {
                    if (entityIterator.next().isAlive() == false) {
                        entityIterator.remove();
                    }
                }
            }
            break;
            case GAME_OVER: {


            }
            break;
        }
    }

    private void processCollisionDetections()
    {
        switch(mCurrentState)
        {
            case GAME_MENU:
            {
                Iterator<Entity> entityIterator = menuEntities.iterator();
                while(entityIterator.hasNext())
                {
                    Entity e = entityIterator.next();
                    if(e.getCollidiable()) {
                        Iterator<Entity> entityIterator2 = menuEntities.iterator();
                        while (entityIterator2.hasNext()) {
                            Entity e2 = entityIterator2.next();
                            if(e2.getCollidiable())
                            {
                                boolean colliding = e.isColliding(e2);
                                if(colliding)
                                {
                                    e.setIsAlive(false);
                                    e2.setIsAlive(false);
                                    sp.play(soundIds[1], 1, 1, 1, 0, 1.0f);

                                }
                            }
                        }
                    }
                }
            }
            break;
            case GAME_RUNNING:
            {
                Iterator<Entity> entityIterator = allEntities.iterator();
                while(entityIterator.hasNext())
                {
                    Entity e = entityIterator.next();
                    if(e.getCollidiable() && e.isAlive()) {
                        Iterator<Entity> entityIterator2 = allEntities.iterator();
                        while (entityIterator2.hasNext()) {
                            Entity e2 = entityIterator2.next();
                            if(e2.getCollidiable() && e2.isAlive())
                            {
                                if((e instanceof Ray && e2 instanceof Alien))
                                {
                                    if(((Ray) e).isFiredByPlayer() == false)
                                    {
                                        // Skip this round because the Aliens bullet should not hit
                                        // themselves
                                        continue;
                                    }
                                }
                                else if((e instanceof Alien && e2 instanceof Ray))
                                {
                                    if(((Ray) e2).isFiredByPlayer() == false)
                                    {
                                        // Skip this round because the Aliens bullet should not hit
                                        // themselves
                                        continue;
                                    }
                                }
                                boolean colliding = e.isColliding(e2);
                                if(colliding)
                                {
                                    e.setIsAlive(false);
                                    e2.setIsAlive(false);

                                    sp.play(soundIds[1], 1, 1, 1, 0, 1.0f);

                                    totalKills += 1;

                                    Handler refresh = new Handler(Looper.getMainLooper());
                                    refresh.post(new Runnable() {
                                        public void run() {
                                            overlayView.show3DToast("Good Shooting, Total Kills:" + totalKills);
                                        }
                                    });

                                    // Create the particle effect of the alien
                                    Explosion explosionEmitter = new Explosion(exploModel);
                                    explosionEmitter.setPosition(e2.getPosition().x, e2.getPosition().y, e2.getPosition().z);
                                    explosionEmitter.setCamera(cameraObj);
                                    explosionEmitter.setMaxAge(5.0f);
                                    int numOfParticles = 50;
                                    for(int i = 0; i < numOfParticles; ++i)
                                    {
                                        explosionEmitter.SpawnParticle();
                                    }

                                    emitters.add(explosionEmitter);
                                }
                            }
                        }
                    }
                }
            }
            break;
            case GAME_OVER:
            {


            }
            break;
        }
    }


    public static void multiQuaternion(float result[], float[] lhs, float[] rhs)
    {
        result[0] = lhs[3] * rhs[0] + lhs[0] * rhs[3] + lhs[1] * rhs[2] - lhs[2] * rhs[1];
        result[1] = lhs[3] * rhs[1] + lhs[1] * rhs[3] + lhs[2] * rhs[0] - lhs[0] * rhs[2];
        result[2] = lhs[3] * rhs[2] + lhs[2] * rhs[3] + lhs[0] * rhs[1] - lhs[1] * rhs[0];
        result[3] = lhs[3] * rhs[3] - lhs[0] * rhs[0] - lhs[1] * rhs[1] - lhs[2] * rhs[2];
    }
}


