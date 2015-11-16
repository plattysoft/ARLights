/*
 *  SimpleRenderer.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package com.plattysoft.arlights;

import android.content.res.Resources;
import android.view.View;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import javax.microedition.khronos.opengles.GL10;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class LightsRenderer extends ARRenderer {

	private final ARLightsActivity mParent;

	private int markerID = -1;
	private Cube cube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);
    private World mWorld;
    private Camera mCamera;
    private Matrix projMatrix = new Matrix();
    private Object3D mModel;
    private Matrix dump = new Matrix();
    private FrameBuffer mBuffer;

    public LightsRenderer(ARLightsActivity arLightsActivity) {
		super();
		mParent = arLightsActivity;
	}

	/**
	 * Markers can be configured here.
	 */
	@Override
	public boolean configureARScene() {

        initJpct();

		// TODO: Load an NFT instead of the hiro marker
		markerID = ARToolKit.getInstance().addMarker("single;Data/patt.hiro;80");
		if (markerID < 0) return false;

		return true;
	}

    private void initJpct() {
        mWorld = new World();
        // TODO: make this variable based on the current light values
        mWorld.setAmbientLight(255, 255, 150);
        Object3D cube = Primitives.getBox(40, 1f/40f);
        cube.setCenter(new SimpleVector(0,0,0));
        mWorld.addObject(cube);

        Object3D plane = Primitives.getPlane(2, 20);
        Resources res = mParent.getResources();
        Texture testTexture = new Texture(res.getDrawable(R.drawable.light_bulb));
        TextureManager.getInstance().addTexture("test", testTexture);
        plane.setTexture("test");
        plane.setOrigin(new SimpleVector(0, 0, 0));
        plane.setBillboarding(true);

        mWorld.addObject(plane);

//        cube.addChild(plane);

        mModel = cube;

//        // TODO: This model has many objects, need to add them all as childs of the tracked onject
//        Object3D model[] = Loader.load3DS(mParent.getResources().openRawResource(R.raw.pillow), 1);
//        for (int i=0; i<model.length; i++) {
//            mWorld.addObject(model[i]);
//        }
        mWorld.buildAllObjects();

        mCamera = mWorld.getCamera();
        mCamera.setPosition(-200, 0, 0);
        mCamera.lookAt(new SimpleVector(0, 0, 0));
        // Config the FOV
//        mCamera.setFovAngle((float) Math.PI*2);
//        mCamera.setYFovAngle((float) Math.PI*2);
    }

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

        float[] projection = ARToolKit.getInstance().getProjectionMatrix();
        projMatrix.setDump(projection);
        projMatrix.transformToGL();
        SimpleVector translation = projMatrix.getTranslation();
        SimpleVector dir = projMatrix.getZAxis();
        SimpleVector up = projMatrix.getYAxis();
        mCamera.setPosition(translation);
        mCamera.setOrientation(dir, up);
    			
		// If the marker is visible, apply its transformation, and draw a cube
		if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            float[] transformation = ARToolKit.getInstance().queryMarkerTransformation(markerID);
            dump.setDump(transformation);
            dump.transformToGL();
            mModel.setOrigin(dump.getTranslation());
            // This produces the same result as clear translation and translate
//            mModel.clearTranslation();
//            mModel.translate(dump.getTranslation());
            mModel.setRotationMatrix(dump);
            mModel.setVisibility(true);

			// Show the options
			mParent.showOptions();
		}
		else {
            mModel.setVisibility(false);
			// Hide the options
			mParent.hideOptions();
		}

        mWorld.renderScene(mBuffer);
        mWorld.draw(mBuffer);
        mBuffer.display();
	}
}