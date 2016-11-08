package dvpermyakov.ex3.Activities;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

import dvpermyakov.ex3.Models.Screen;
import dvpermyakov.ex3.R;

/**
 * Created by dvpermyakov on 07.11.2016.
 */
public class MainActivity extends GvrActivity implements GvrView.StereoRenderer {
    private float[] projectionMatrix;
    private float[] viewMatrix;
    private List<Screen> screens;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        gvrView.setRenderer(this);
        setGvrView(gvrView);

        projectionMatrix = new float[16];
        viewMatrix = new float[16];

        screens = new ArrayList<>();
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Screen.loadShaders(this);

        int amount = getRandomI(2, 7);
        float radius = 1.0f;
        double angle = Math.PI / amount;
        double length = 2.0 * radius * Math.sin(angle / 2.0);
        for (int i = 0; i < amount; i++) {
            Screen screen = new Screen(this, (float) length, radius);
            screen.place(i);
            screens.add(screen);
        }
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {}

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] cameraMatrix = new float[16];
        float eyeZ = 0.0f;
        float centerX = 1.0f;
        Matrix.setLookAtM(cameraMatrix, 0, 0, 0, eyeZ, centerX, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(viewMatrix, 0, eye.getEyeView(), 0, cameraMatrix, 0);

        float zNear = 0.01f;
        float zFar = 100.0f;
        projectionMatrix = eye.getPerspective(zNear, zFar);

        for (Screen screen : screens) {
            screen.render(projectionMatrix, viewMatrix);
        }
    }

    private int getRandomI(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public void onSurfaceChanged(int width, int height) {}

    @Override
    public void onFinishFrame(Viewport viewport) {}

    @Override
    public void onRendererShutdown() {}
}
