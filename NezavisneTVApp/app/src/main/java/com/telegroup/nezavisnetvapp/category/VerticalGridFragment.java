package com.telegroup.nezavisnetvapp.category;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.AppSingleton;
import com.telegroup.nezavisnetvapp.DetailsActivity;
import com.telegroup.nezavisnetvapp.ErrorActivity;
import com.telegroup.nezavisnetvapp.MainFragment;
import com.telegroup.nezavisnetvapp.R;
import com.telegroup.nezavisnetvapp.legacy.CardPresenter;
import com.telegroup.nezavisnetvapp.model.Category;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;
import com.telegroup.nezavisnetvapp.presenter.StringCardPresenter;
import com.telegroup.nezavisnetvapp.util.ImageProcess;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * VerticalGridFragment shows contents with vertical alignment
 */
public class VerticalGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final String TAG = VerticalGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 4;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private ArrayObjectAdapter mAdapter;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private final Handler mHandler = new Handler();
    private BackgroundManager mBackgroundManager;
    private Context context;
    List<Category> categories;
    private DisplayMetrics mMetrics;
    private String uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
       setTitle((String) getActivity().getIntent().getSerializableExtra(VerticalGridActivity.Title));
        //setBadgeDrawable(getResources().getDrawable(R.drawable.app_icon_your_company));

        setupFragment();
        setupEventListeners();

        // it will move current focus to specified position. Comment out it to see the behavior.
        // setSelectedPosition(5);
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
        mAdapter = new ArrayObjectAdapter(new NewsCardPresenter());
        prepareBackgroundManager();
        loadRows();
        setAdapter(mAdapter);
    }
    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof NewsCard) {
                NewsCard movie = (NewsCard) item;

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.Article, movie);
                intent.putExtra(DetailsActivity.CategoryId, (String) getActivity().getIntent().getSerializableExtra(VerticalGridActivity.CategoryId));
                getActivity().startActivity(intent);
            }

        }
    }
    private void loadRows() {
        final String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonArrayRequest";
                                final Gson gson=new Gson();


                                /////////////////////////////
                                //30 nije dijeljivo sa 4 :)//
                                //JsonArrayRequest newsRequest = new JsonArrayRequest("http://dtp.nezavisne.com/app/rubrika/" + getActivity().getIntent().getSerializableExtra(VerticalGridActivity.CategoryId) + "/1/30",
                                JsonArrayRequest newsRequest = new JsonArrayRequest("http://dtp.nezavisne.com/app/rubrika/" + getActivity().getIntent().getSerializableExtra(VerticalGridActivity.CategoryId) + "/1/32",
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray newsJSONArray) {
                                                if (newsJSONArray.length() > 0) {
                                                    List<NewsCard> newsCards = Arrays.asList(gson.fromJson(newsJSONArray.toString(), NewsCard[].class));
                                                    for (NewsCard newsCard : newsCards) {
                                                        newsCard.setColor((String) getActivity().getIntent().getSerializableExtra(VerticalGridActivity.Color));
                                                        mAdapter.add(newsCard);
                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                                        Intent intent=new Intent(getActivity(),ErrorActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(newsRequest, REQUEST_TAG);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new VerticalGridFragment.UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if(item instanceof NewsCard){
                String preparedUri = ((NewsCard)item).getImageUrl().replaceAll("/[0-9]*x[0-9]*/", "/750x450/");
                mBackgroundUri = preparedUri;
                ((NewsCard)item).setImageUrl(preparedUri);
                startBackgroundTimer();
            }
            //getView().setBackgroundColor(Color.parseColor(categories.get((int)row.getId()).getBoja()));
          //  setBrandColor(Color.parseColor(categories.get((int)row.getId()).getColor()));
        }
    }
    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackground(getResources().getDrawable(R.drawable.default_background1));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private void updateBackground(String uri) {
        this.uri = uri;
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        /*Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        resource.setColorFilter(0xFF5F5F5F, PorterDuff.Mode.MULTIPLY);
                        mBackgroundManager.setDrawable(resource);
                    }
                });*/
        Glide.with(getActivity())
                .load(uri)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop()
                .error(R.drawable.default_background1)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        final RenderScript rs = RenderScript.create( context );
                        final Allocation input = Allocation.createFromBitmap( rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
                        final Allocation output = Allocation.createTyped( rs, input.getType() );
                        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
                        script.setRadius( 5.f /* e.g. 3.f */ );
                        script.setInput( input );
                        script.forEach( output );
                        output.copyTo( bitmap );
                        Bitmap darkenedBitmap = ImageProcess.darken(bitmap);
                        mBackgroundManager.setBitmap(darkenedBitmap);
                        //mDetailsBackground.setCoverBitmap(ImageProcess.darken(bitmap));
                        //mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
        mBackgroundTimer.cancel();
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }
}