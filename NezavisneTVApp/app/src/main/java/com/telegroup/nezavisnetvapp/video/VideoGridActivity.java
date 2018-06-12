package com.telegroup.nezavisnetvapp.video;

import android.app.Activity;
import android.os.Bundle;

import com.telegroup.nezavisnetvapp.R;

/**
 * {@link VideoGridActivity} loads {@link VideoGridFragment}
 */
public class VideoGridActivity extends Activity {

    private static final String TAG = VideoGridActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_grid);
    }
}
