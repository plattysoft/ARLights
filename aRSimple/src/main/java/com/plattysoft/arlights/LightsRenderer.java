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

import com.threed.jpct.Camera;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
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
        // TODO: make this vatriable based on the current light values
        mWorld.setAmbientLight(150,150,150);
//        Object3D[] model = Loader.load3DS(mParent.getResources().openRawResource(R.raw.candle), 1);
//
//        mWorld.addObject(model[0]);
        mWorld.buildAllObjects();

        mCamera = mWorld.getCamera();
    }

    /**
	 * Override the draw function from ARRenderer.
	 */
	@Override
	public void draw(GL10 gl) {
		// TODO: Use jpct to load some nice models that can interact

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Apply the ARToolKit projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
	
		gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);        
    	gl.glFrontFace(GL10.GL_CW);
    			
		// If the marker is visible, apply its transformation, and draw a cube
		if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
			cube.draw(gl);
			// Show the options
			mParent.showOptions();
		}
		else {
			// Hide the options
			mParent.hideOptions();
		}

	}

//	private Object3D loadModel(String filename, float scale) {
//		Object3D[] model = Loader.load3DS(filename, scale);
//		Object3D o3d = new Object3D(0);
//		Object3D temp = null;
//		for (int i = 0; i < model.length; i++) {
//			temp = model[i];
//			temp.setCenter(SimpleVector.ORIGIN);
//			temp.rotateX((float)( -.5*Math.PI));
//			temp.rotateMesh();
//			temp.setRotationMatrix(new Matrix());
//			o3d = Object3D.mergeObjects(o3d, temp);
//			o3d.build();
//		}
//		return o3d;
//	}
}