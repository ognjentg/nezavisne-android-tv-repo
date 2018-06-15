package com.telegroup.nezavisnetvapp.gallery;

import android.app.Activity;
import android.os.Bundle;

import com.telegroup.nezavisnetvapp.R;


/**
 * {@link GalleryActivity} loads {@link GalleryViewFragment}
 */

public class GalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }
}
