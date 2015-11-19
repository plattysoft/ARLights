package com.plattysoft.arlights;

import android.content.res.Resources;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

/**
 * Created by Raul Portales on 16/11/15.
 */
public class ClickableObject3D extends Object3D {

    private boolean mSelected;

    public ClickableObject3D(Resources r, String name, int textureResource, int texturePressedResource, int textureSelectedResource) {
        super(Primitives.getPlane(1, 80f));
        
        loadTexture(r, "normal_" + name, textureResource);
        loadTexture(r, "pressed_" + name, texturePressedResource);
        loadTexture(r, "selected_" + name, textureSelectedResource);

        setTexture("normal_" + name);
        setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        setName(name);
        rotateX((float) Math.PI);
    }

    private void loadTexture(Resources r, String textureName, int textureResource) {
        TextureManager tm = TextureManager.getInstance();
        // Only add the texture once
        if (!tm.containsTexture(textureName)) {
            Texture iconTexture = new Texture(r.getDrawable(textureResource), false);
            TextureManager.getInstance().addTexture(textureName, iconTexture);
        }
    }

    public void onTouchOut() {
        // get back to the original state
        updateTexture();
    }

    public void setSelected(boolean b) {
        mSelected = b;
        updateTexture();
    }

    private void updateTexture() {
        if (mSelected) {
            setTexture("selected_" + getName());
        }
        else {
            setTexture("normal_" + getName());
        }
    }

    public void onPressed() {
        setTexture("pressed_" + getName());
    }
}
