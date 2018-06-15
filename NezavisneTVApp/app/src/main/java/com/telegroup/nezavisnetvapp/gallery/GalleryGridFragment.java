package com.telegroup.nezavisnetvapp.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.AppSingleton;
import com.telegroup.nezavisnetvapp.DetailsActivity;
import com.telegroup.nezavisnetvapp.ErrorActivity;
import com.telegroup.nezavisnetvapp.legacy.CardPresenter;
import com.telegroup.nezavisnetvapp.model.Image;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * VerticalGridFragment shows contents with vertical alignment
 */
public class GalleryGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final String TAG = "GalleryGridFragment";
    private static final int NUM_COLUMNS = 4;
    private ArrayObjectAdapter mAdapter;
    private Image[] images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setTitle("Galerija");
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
        System.out.println("SETUPGRRR");
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

            if (item instanceof  Image){
                Image image=(Image) item;
                int position=findPosition(image.getUrl());
                Intent intent=new Intent(getActivity(),GalleryActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("images",images);
                getActivity().startActivity(intent);
            }
          /*  if (item instanceof NewsCard) {
                NewsCard movie = (NewsCard) item;

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.Article, movie);
                intent.putExtra(DetailsActivity.CategoryId, (String) getActivity().getIntent().getSerializableExtra(VerticalGridActivity.CategoryId));
                getActivity().startActivity(intent);
            }*/

        }
    }
    private void loadRows() {
            images=(Image[])getActivity().getIntent().getSerializableExtra("images");
            for(Image image:images){
                mAdapter.add(image);
            }
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

    private int findPosition(String url){
        for(int i=0;i<images.length;i++){
            if (images[i].getUrl().equals(url))
                return i;
        }
        return -1;

    }
}