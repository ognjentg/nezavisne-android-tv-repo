package com.telegroup.nezavisnetvapp.category;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.util.Log;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.AppSingleton;
import com.telegroup.nezavisnetvapp.DetailsActivity;
import com.telegroup.nezavisnetvapp.ErrorActivity;
import com.telegroup.nezavisnetvapp.R;
import com.telegroup.nezavisnetvapp.legacy.CardPresenter;
import com.telegroup.nezavisnetvapp.model.Category;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;
import com.telegroup.nezavisnetvapp.presenter.StringCardPresenter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * VerticalGridFragment shows contents with vertical alignment
 */
public class VerticalGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final String TAG = VerticalGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 4;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setTitle("VerticalGridFragment");
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
        loadRows();
        setAdapter(mAdapter);
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
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
           /*if( item != null ){
                picassoBackgroundManager.updateBackgroundWithDelay(((Movie) item).getBackgroundImageUrl());
            } else {
                Log.w(TAG, "item is null");
            }*/

        }
    }
}