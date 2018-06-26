package com.telegroup.nezavisnetvapp.video;

import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.Log;

import com.telegroup.nezavisnetvapp.model.Image;
import com.telegroup.nezavisnetvapp.model.Playlist;
import com.telegroup.nezavisnetvapp.model.Video;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;

/**
 * VerticalGridFragment shows contents with vertical alignment
 */
public class VideoGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final String TAG = "VideoGridFragment";
    private static final int NUM_COLUMNS = 4;
    private ArrayObjectAdapter mAdapter;
    private Playlist playlist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setTitle("Video galerija");
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
        System.out.println("SETUP Videooo");
        playlist=new Playlist();
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

          //ovde ce ici poziv plejera

        }
    }
    //ucitavanje video
    private void loadRows() {
            Video[] videos=(Video[])getActivity().getIntent().getSerializableExtra("videos");
        System.out.println(videos.length+" ucitano je videa");
            for(Video video:videos){
                mAdapter.add(video);
                playlist.add(video);
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
}