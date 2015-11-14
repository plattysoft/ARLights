/*
 *  ARSimple.java
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

import org.artoolkit.ar.jpct.ArJpctActivity;
import org.artoolkit.ar.jpct.TrackableObject3d;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A very simple example of extending ARActivity to create a new AR application.
 */
public class ARLightsActivity extends ArJpctActivity implements View.OnClickListener {

	private PHHueSDK phHueSDK;
    private PHLightListener mListener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {
        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> list) {
        }

        @Override
        public void onSearchComplete() {
        }

        @Override
        public void onSuccess() {
            Log.d("ARLightsActivity", "Light has updated");
        }

        @Override
        public void onError(int i, String s) {
            Log.d("ARLightsActivity", "Error: "+s);
        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }
    };

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.main);
		phHueSDK = PHHueSDK.create();

        // Make the size of the layout a multiple of the camera sesolution (keep aspect ratio)
        // Instead of just stretching to fullscreen with match parent

        initButtons();
	}

    private void initButtons() {
        findViewById(R.id.full_bright).setOnClickListener(this);
        findViewById(R.id.quarter_bright).setOnClickListener(this);
        findViewById(R.id.half_bright).setOnClickListener(this);
        findViewById(R.id.dim_bright).setOnClickListener(this);
        findViewById(R.id.low_bright).setOnClickListener(this);
    }

    @Override
    protected void populateTrackableObjects(List<TrackableObject3d> list) {
        TrackableObject3d obj = new TrackableObject3d("single;Data/patt.hiro;80");

        Object3D plane = Primitives.getPlane(2, 180);

        // Load the AR Toolkit texture on top of the plane
        Texture texture = new Texture(getResources().getDrawable(R.drawable.border), false);
        TextureManager.getInstance().addTexture("artoolkit", texture);
        plane.setTexture("artoolkit");
        obj.addChild(plane);

        // Load 6 planes in a 3x2 grid
        for (int i=0; i<2; i++) {
            for (int j=0; j<3; j++) {
                Object3D icon = Primitives.getPlane(2, 40);
                icon.translate((j-1)*100, -i*100+50, 1);
                String textureName = "item_"+(i*3+j);
                Texture iconTexture = new Texture(getResources().getDrawable(getTextureResource(i*3+j)), false);
                TextureManager.getInstance().addTexture(textureName, iconTexture);
                icon.setTexture(textureName);
                icon.rotateX((float) Math.PI);
                // TODO: Each item should have 3 states: Unselected, selected & pressed (last is optional)
                obj.addChild(icon);
            }
        }

        // This parent rotation should apply to all of them
        plane.rotateX((float) Math.PI);

        list.add(obj);
    }

    private int getTextureResource(int i) {
        switch (i) {
            case 0:
                return R.drawable.light_full;
            case 1:
                return R.drawable.read;
            case 2:
                return R.drawable.fog_sun;
            case 3:
                return R.drawable.candle;
            case 4:
                return R.drawable.icon_sleep;
            case 5:
                return R.drawable.light_off;
            default:
                return R.drawable.light_bulb;
        }
    }

    @Override
    public void configureWorld(World world) {
        world.setAmbientLight(255, 255, 255);
    }

    /**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);    	
	}

	@Override
	protected void onDestroy() {
		PHBridge bridge = phHueSDK.getSelectedBridge();
		if (bridge != null) {

			if (phHueSDK.isHeartbeatEnabled(bridge)) {
				phHueSDK.disableHeartbeat(bridge);
			}

			phHueSDK.disconnect(bridge);
		}
        super.onDestroy();
    }

	public void showOptions() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.optionsLayout).setVisibility(View.VISIBLE);
            }
        });
	}

	public void hideOptions() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.optionsLayout).setVisibility(View.GONE);
            }
        });
	}

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.full_bright) {
            setLightIntensity(100);
        }
        if (view.getId() == R.id.half_bright) {
            setLightIntensity(50);
        }
        if (view.getId() == R.id.quarter_bright) {
            setLightIntensity(25);
        }
        if (view.getId() == R.id.dim_bright) {
            setLightIntensity(10);
        }
        if (view.getId() == R.id.low_bright) {
            setLightIntensity(1);
        }
    }

    private void setLightIntensity(int percentage) {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            // Normal lights have brightness but not hue
            lightState.setBrightness(percentage * 254 / 100);
//            lightState.setHue(percentage * MAX_HUE / 100);
            bridge.updateLightState(light, lightState, mListener);
        }
    }
}