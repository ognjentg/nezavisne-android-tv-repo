package com.telegroup.nezavisnetvapp.gallery;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BaseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.AppSingleton;
import com.telegroup.nezavisnetvapp.DetailsActivity;
import com.telegroup.nezavisnetvapp.ErrorActivity;
import com.telegroup.nezavisnetvapp.R;
import com.telegroup.nezavisnetvapp.legacy.CardPresenter;
import com.telegroup.nezavisnetvapp.model.Image;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;


public class GalleryViewFragment extends Fragment {


    private ImageView mainImage;
    private Integer position;
    private Image[] images;
    private View view;

    public GalleryViewFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.activity_gallery_view,container,false);
        images=(Image[])getActivity().getIntent().getSerializableExtra("images");
        position=(Integer) getActivity().getIntent().getSerializableExtra("position");
        mainImage=(ImageView)view.findViewById(R.id.gallery_image);
        Glide.with(getActivity())
                .load(images[position].getUrl())
                .centerCrop()
                .into(mainImage);
        mainImage.requestFocus();
        mainImage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                System.out.println("PROSLO");
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch(keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (--position==-1)
                                position=images.length-1;
                            Glide.with(getActivity())
                                    .load(images[position].getUrl())
                                    .centerCrop()
                                    .into(mainImage);
                            break;

                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            Glide.with(getActivity())
                                    .load(images[position=(position+1)%images.length].getUrl())
                                    .centerCrop()
                                    .into(mainImage);
                            break;
                    }
                }

                return false;
            }
        });
        return view;
    }



    }







