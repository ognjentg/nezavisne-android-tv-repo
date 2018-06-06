/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.telegroup.nezavisnetvapp;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.telegroup.nezavisnetvapp.category.VerticalGridActivity;
import com.telegroup.nezavisnetvapp.header.IconHeaderItem;
import com.telegroup.nezavisnetvapp.header.IconHeaderItemPresenter;
import com.telegroup.nezavisnetvapp.model.Category;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;
import com.telegroup.nezavisnetvapp.presenter.StringCardPresenter;
import com.telegroup.nezavisnetvapp.util.ImageProcess;
import com.telegroup.nezavisnetvapp.widget.NewsCardView;
import com.telegroup.nezavisnetvapp.widget.SearchActivity;

import org.json.JSONArray;
import org.jsoup.helper.StringUtil;

import static com.telegroup.nezavisnetvapp.R.*;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private Context context;
    List<Category> categories;
    private String uri;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        context = getActivity().getApplicationContext();

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDefaultBackground = getResources().getDrawable(drawable.default_background1);
        mBackgroundManager.setDrawable(mDefaultBackground);

        if (uri != null && !uri.isEmpty()) {
            updateBackground(uri);
        }
    }

    private String getLowerCaseNoDashesNoLatinCharsCategoryName(String name){
        String s = "icon_" + name.toLowerCase().replaceAll("\\s|-", "");

        return s.replace("š", "s").replace("đ", "d").replace("č", "c").replace("ć", "c").replace("ž", "z");
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        final String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonArrayRequest";
        JsonArrayRequest categoriesRequest = new JsonArrayRequest(getResources().getString(string.get_categories),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray categoriesJSONArray) {
                        final Gson gson = new Gson();
                        if (categoriesJSONArray.length() > 0) {
                            categories = Arrays.asList(gson.fromJson(categoriesJSONArray.toString(), Category[].class));
                            int i = 0;
                            for (final Category category : categories) {
                                IconHeaderItem header = new IconHeaderItem(i++, category.getTitle());

                                String lowerCaseNoDashesNoLatinCharsCategoryName = getLowerCaseNoDashesNoLatinCharsCategoryName(category.getTitle());
                                int iconResId = context.getResources().getIdentifier(lowerCaseNoDashesNoLatinCharsCategoryName, "drawable", context.getPackageName());
                                header.setIconResId(iconResId);

                                ClassPresenterSelector selector = new ClassPresenterSelector();
                                StringCardPresenter stringPresenter = new StringCardPresenter();
                                NewsCardPresenter cardPresenter = new NewsCardPresenter();
                                selector.addClassPresenter(NewsCard.class, cardPresenter);
                                selector.addClassPresenter(String.class, stringPresenter);
                                final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(selector);
                                JsonArrayRequest newsRequest = new JsonArrayRequest(getResources().getString(string.get_news_for_category_url) + category.getId() + getResources().getString(string.get_news_for_category_range),
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray newsJSONArray) {
                                                if (newsJSONArray.length() > 0) {
                                                    List<NewsCard> newsCards = Arrays.asList(gson.fromJson(newsJSONArray.toString(), NewsCard[].class));
                                                    for (NewsCard newsCard : newsCards) {
                                                        newsCard.setColor(category.getColor());
                                                        //System.out.println(newsCard);
                                                        listRowAdapter.add(newsCard);
                                                    }
                                                    listRowAdapter.add(category.getColor());
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
                                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                onDetach();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Intent intent=new Intent(getActivity(),ErrorActivity.class);
                startActivity(intent);

            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(categoriesRequest, REQUEST_TAG);
        setAdapter(mRowsAdapter);
    }


    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(drawable.default_background1);
        mBackgroundManager.setDrawable(mDefaultBackground);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(
                drawable.nezavisne));

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(color.search_opaque));

        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });

    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {


            if (item instanceof NewsCard) {
                NewsCard card = (NewsCard) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.Article, card);
                intent.putExtra(DetailsActivity.CategoryId, categories.get((int)row.getId()).getId());


                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((NewsCardView) itemViewHolder.view).getNewsImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                /*CategoryFragment fragment=new CategoryFragment();
                getActivity().getFragmentManager().beginTransaction()
                        .replace(id.main_browse_fragment, fragment)
                        .addToBackStack(null)
                        .commit();*/
                Intent intent=new Intent(getActivity(), VerticalGridActivity.class);
                intent.putExtra(VerticalGridActivity.CategoryId, categories.get((int)row.getId()).getId());
                intent.putExtra(VerticalGridActivity.Color, "#0074A2");  //TODO find a way to transfer category color
                System.out.println("Vertical grid");
                getActivity().startActivity(intent);
            }
        }
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
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
            setBrandColor(Color.parseColor(categories.get((int)row.getId()).getColor()));
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackground(getResources().getDrawable(drawable.default_background1));
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
