package com.plattysoft.arlights;

import android.view.View;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raul Portales on 07/11/15.
 */
public abstract class JPCTRenderer extends ARRenderer {

    private final ARActivity mParent;

    private World mWorld;
    private Camera mCamera;

    private FrameBuffer mBuffer;

    private Matrix projMatrix = new Matrix();
    private Matrix dump = new Matrix();

    public JPCTRenderer(ARActivity arLightsActivity) {
        super();
        mParent = arLightsActivity;
    }

    /**
     * Markers can be configured here.
     */
    @Override
    public final boolean configureARScene() {

        initJpct();

        return loadARScene();
    }

    protected abstract boolean loadARScene();

    private void initJpct() {
        mWorld = new World();
        // TODO: make this vatriable based on the current light values
        mWorld.setAmbientLight(150, 150, 150);

        loadModels(mWorld);

        mWorld.buildAllObjects();

        mCamera = mWorld.getCamera();
    }

    protected abstract void loadModels(World mWorld);

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {
        if (mBuffer == null) {
            View glView = mParent.getGLView();
            mBuffer = new FrameBuffer(gl, glView.getWidth(), glView.getHeight());
        }
        mBuffer.clear();

        // Handle the camera
        float[] projection = ARToolKit.getInstance().getProjectionMatrix();
        projMatrix.setDump(projection);
        projMatrix.transformToGL();
        SimpleVector translation = projMatrix.getTranslation();
        SimpleVector dir = projMatrix.getZAxis();
        SimpleVector up = projMatrix.getYAxis();
        mCamera.setPosition(translation);
        mCamera.setOrientation(dir, up);

        // If the marker is visible, apply its transformation, and draw a cube
        // TODO: Draw all ARTracked objects

        mWorld.renderScene(mBuffer);
        mWorld.draw(mBuffer);
        mBuffer.display();
    }

}
