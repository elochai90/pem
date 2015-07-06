package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Simon on 04.07.2015.
 */
public class PicassoSingleton {

    Context context;
    private static PicassoSingleton picassoSingleton;

    private PicassoSingleton(Context p_context) {
        this.context = p_context;
    }

    public static PicassoSingleton getInstance(Context p_context){
        if(picassoSingleton == null) {
            picassoSingleton = new PicassoSingleton(p_context);
        }
        return picassoSingleton;
    }

    public void setImage(String p_file, int p_width, int p_height, ImageView p_targetView) {
        if (p_file != "") {
            Picasso.with(context).load(new File(p_file)).resize(p_width, p_height).centerCrop().into(p_targetView);
        }
    }
}
