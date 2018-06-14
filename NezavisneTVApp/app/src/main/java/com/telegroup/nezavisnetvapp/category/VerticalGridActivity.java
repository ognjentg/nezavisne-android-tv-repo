package com.telegroup.nezavisnetvapp.category;

import android.os.Bundle;
import android.app.Activity;
import com.telegroup.nezavisnetvapp.R;

/**
 * {@link VerticalGridActivity} loads {@link VerticalGridFragment}
 */
public class VerticalGridActivity extends Activity {

    private static final String TAG = VerticalGridActivity.class.getSimpleName();
    public static final String CategoryId = "CategoryId";
    public static final String Color = "Color";
    public static final String Title="Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_grid);
    }
}
