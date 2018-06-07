package com.telegroup.nezavisnetvapp.gallery;

import android.app.Activity;
import android.os.Bundle;

import com.telegroup.nezavisnetvapp.R;

/**
 * {@link GalleryGridActivity} loads {@link GalleryGridFragment}
 */
public class GalleryGridActivity extends Activity {

    private static final String TAG = GalleryGridActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_grid);
    }
}
