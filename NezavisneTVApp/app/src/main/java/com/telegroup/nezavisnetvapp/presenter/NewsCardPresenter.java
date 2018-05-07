package com.telegroup.nezavisnetvapp.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telegroup.nezavisnetvapp.R;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.widget.NewsCardView;

/*
 * Copyright (C) 2014 The Android Open Source Project
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

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class NewsCardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private static int sSelectedTitleTextColor;
    private static int sDefaultTitleTextColor;
    private Drawable mDefaultCardImage;
    private static Context mContext;
    private static void updateCardBackgroundColor(NewsCardView view, boolean selected) {
        int colorBackground = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        int colorText = selected ? sSelectedTitleTextColor : sDefaultTitleTextColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.

        TextView cardTitle = view.findViewById(R.id.news_title);

        cardTitle.setBackgroundColor(colorBackground);
        cardTitle.setTextColor(colorText);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");
        mContext=parent.getContext();
        sDefaultBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.transparent);
        sSelectedBackgroundColor =
                ContextCompat.getColor(parent.getContext(), R.color.transparent_selected);

        sDefaultTitleTextColor =
                ContextCompat.getColor(parent.getContext(), R.color.title_text);
        sSelectedTitleTextColor =
                ContextCompat.getColor(parent.getContext(), R.color.title_text_selected);

        /*
         * This template uses a default image in res/drawable, but the general case for Android TV
         * will require your resources in xhdpi. For more information, see
         * https://developer.android.com/training/tv/start/layouts.html#density-resources
         */
        mDefaultCardImage = ContextCompat.getDrawable(parent.getContext(), R.drawable.newsdefault);

        NewsCardView cardView =
                new NewsCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        super.setSelected(selected);
                    }
                };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        NewsCard card = (NewsCard) item;
        NewsCardView cardView = (NewsCardView) viewHolder.view;

        Log.d(TAG, "onBindViewHolder");
        if (card.getImageUrl() != null) {
            cardView.setCardSubCategory(card.getSubCategory());
            cardView.findViewById(R.id.news_subcategory).setBackgroundColor(Color.parseColor(card.getColor()));
            cardView.setCardText(card.getTitle());
            Glide.with(viewHolder.view.getContext())
                    .load(card.getImageUrl())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getNewsImageView());
        }
    }
    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
