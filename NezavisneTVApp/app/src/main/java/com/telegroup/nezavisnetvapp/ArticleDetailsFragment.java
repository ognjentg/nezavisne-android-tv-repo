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

package com.telegroup.nezavisnetvapp;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import android.support.media.tv.Channel;
import android.support.media.tv.TvContractCompat;
import android.support.media.tv.ChannelLogoUtils;
import android.support.media.tv.PreviewProgram;
import android.support.media.tv.WatchNextProgram;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.gallery.GalleryGridActivity;
import com.telegroup.nezavisnetvapp.legacy.CardPresenter;
import com.telegroup.nezavisnetvapp.model.Category;
import com.telegroup.nezavisnetvapp.model.NewsCard;
import com.telegroup.nezavisnetvapp.presenter.NewsCardPresenter;
import com.telegroup.nezavisnetvapp.recommendations.RecommendationEngine;
import com.telegroup.nezavisnetvapp.recommendations.UserLogs;
import com.telegroup.nezavisnetvapp.util.ImageProcess;
import com.telegroup.nezavisnetvapp.video.VideoGridActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.support.media.tv.ChannelLogoUtils.storeChannelLogo;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class ArticleDetailsFragment extends DetailsFragment {
    private static final String TAG = "ArticleDetailsFragment";
    private static final int GALLERY_ID=10;
    private static final int VIDEO_ID=20;


    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;


    public static UserLogs userLogs;
    public static RecommendationEngine recommendationEngine;


    private NewsCard mSelectedArticle;
    private Article mRealArticle=new Article();
    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private String categoryId;
    private Context context;
    private ArrayList<NewsCard> toReturn;

    private DetailsFragmentBackgroundController mDetailsBackground;

    private void getRecommendations(){
        final HashMap<String, Integer> categoriesToGet = recommendationEngine.getAmountsToGet();
        final HashMap<String, String> newsLog = ArticleDetailsFragment.userLogs.getNewsLog();
        final Gson gson = new Gson();
        for(final String category : categoriesToGet.keySet()){
            final String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonArrayRequest";
            JsonArrayRequest newsRequest = new JsonArrayRequest("http://dtp.nezavisne.com/app/rubrika/" + category + "/1/15",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray newsJSONArray) {
                            toReturn = new ArrayList<>();
                            if (newsJSONArray.length() > 0) {
                                List<NewsCard> newsCards = Arrays.asList(gson.fromJson(newsJSONArray.toString(), NewsCard[].class));
                                ArrayList<NewsCard> newsCardsArrayList = new ArrayList<>();
                                newsCardsArrayList.addAll(newsCards);
                                for(int i = 0; i < newsCardsArrayList.size(); i++){
                                    if(newsLog.values().contains(newsCardsArrayList.get(i).getNewsId())){
                                        newsCardsArrayList.remove(i--);
                                    }
                                }
                                for(int i = 0; i < categoriesToGet.get(category); i++){
                                    toReturn.add(newsCardsArrayList.get(i));
                                }
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Channel.Builder builder = new Channel.Builder();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                                            .setDisplayName("Čitajte još")
                                            .setAppLinkIntent(intent);
                                    Uri channelUri = context.getContentResolver().insert(
                                            TvContractCompat.Channels.CONTENT_URI, builder.build().toContentValues());
                                    long channelId = ContentUris.parseId(channelUri);
                                    storeChannelLogo(context, channelId, BitmapFactory.decodeResource(context.getResources(), R.drawable.nezavisne));
                                    TvContractCompat.requestChannelBrowsable(context, channelId);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("NO", "NO");
                }
            });
            AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(newsRequest, REQUEST_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();

        if(userLogs == null){
            userLogs = new UserLogs(context);
        }

        if(recommendationEngine == null){
            recommendationEngine = new RecommendationEngine(context);
        }

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        mSelectedArticle =
                (NewsCard) getActivity().getIntent().getSerializableExtra(DetailsActivity.Article);
        categoryId =
                (String) getActivity().getIntent().getSerializableExtra(DetailsActivity.CategoryId);
        initializeBackground(mSelectedArticle.getImageUrl().replaceAll("/[0-9]*x[0-9]*/", "/750x450/"));
            //zakomentarisao sam da bi mogao da citam vjesti
      /*  userLogs.userOpenedNews(mSelectedArticle, categoryId);
        getRecommendations();*/

        if (mSelectedArticle != null) {
            String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest("http://dtp.nezavisne.com/app/v2/vijesti/" + mSelectedArticle.getNewsId(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                                Gson gson=new Gson();
                                mRealArticle=gson.fromJson(response.toString(),Article.class);
                                mRealArticle.setBody(parseNewsContent(mRealArticle.getBody()));
                           /*     mRealArticle.setId(Integer.parseInt(response.getString("vijestID")));
                                mRealArticle.setTitle(response.getString("Naslov"));
                                mRealArticle.setDescription("Autor: "+response.getString("Autor")+"     Datum: "+response.getString("Datum")+"\n\n"+response.getString("Lid"));
                                mRealArticle.setImageUrl(response.getJSONArray("Slika").getJSONObject(0).getString("slikaURL").replace("555x333","750x450"));
                                mRealArticle.setCategoryId(response.getString("meniRoditelj"));
                                mRealArticle.setBody(parseNewsContent(response.getString("Tjelo")));*/
                                mPresenterSelector = new ClassPresenterSelector();
                                mAdapter = new ArrayObjectAdapter(mPresenterSelector);
                                System.out.println(mRealArticle.getTitle());
                                System.out.println(mRealArticle.getBody());
                              //  System.out.println("VIDEOS"+mRealArticle.getVideos().length);
                                setupDetailsOverviewRow();
                                setupDetailsOverviewRowPresenter();
                                setupGallery();
                                setupRelatedArticleListRow();
                                setAdapter(mAdapter);
                                //initializeBackground(mRealArticle);
                                setOnItemViewClickedListener(new ItemViewClickedListener());

                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Intent intent=new Intent(getActivity(),ErrorActivity.class);
                    startActivity(intent);
                }
            });

            // Adding JsonObject request to request queue
            AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

        }
    }

    private void setupGallery() {


    }

    private void initializeBackground(Article data) {
        mDetailsBackground.enableParallax();

        Glide.with(getActivity())
                .load((data.getImages()[0].getUrl()))
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
                        mDetailsBackground.setCoverBitmap(ImageProcess.darken(bitmap));
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }

    private void initializeBackground(String data) {
        mDetailsBackground.enableParallax();

        Glide.with(getActivity())
                .load(data)
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
                        mDetailsBackground.setCoverBitmap(ImageProcess.darken(bitmap));
                        //mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }

    public int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mRealArticle.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mRealArticle);
        /*row.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.red));
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);*/

      //  Bitmap blurredBitmap = BlurBuilder.blur( getActivity(),);
       // Picasso.with(getActivity()).load(mRealArticle.getImageUrl()).transform(BlurTransform.
         //       getInstance(this.getActivity())).fit().into();

        /*Glide.with(getActivity())

                .load(mRealArticle.getImageUrl()).transform(new BlurTransformation(getActivity()))

                .centerCrop()
                .error(R.drawable.default_background1)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });*/
        if (mRealArticle.getImages().length>1) {
            ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
            Action action = new Action(
                    GALLERY_ID,
                    getResources().getString(R.string.gallery), "",
                    getResources().getDrawable(R.drawable.photo_camera));
            ;
            actionAdapter.add(
                    action);
            row.setActionsAdapter(actionAdapter);
        }
      /*  if (mRealArticle.getVideos().length>0) {
            ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
            Action action = new Action(
                    VIDEO_ID,
                    getResources().getString(R.string.video_gallery), "");
            actionAdapter.add(
                    action);
            row.setActionsAdapter(actionAdapter);
        }*/
        mAdapter.add(row);
    }



    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.default_background));

        // Hook up transition element.
        /*FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        //detailsPresenter.setListener(sharedElementHelper);*/
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {

                if(action.getId()==GALLERY_ID) {
                   Intent intent = new Intent(getActivity(), GalleryGridActivity.class);
                    intent.putExtra("images", mRealArticle.getImages());
                    getActivity().startActivity(intent);
                }else if(action.getId()==VIDEO_ID){
                   Intent intent = new Intent(getActivity(), VideoGridActivity.class);
               //     intent.putExtra("videos", mRealArticle.getVideos());
                    getActivity().startActivity(intent);
                }

            }
        });

        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedArticleListRow() {


//
        final JsonArrayRequest jsonArray = new JsonArrayRequest("http://dtp.nezavisne.com/app/rubrika/" + mRealArticle.getCategoryId() + "/10/20",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(getActivity() != null){
                        final Gson gson = new Gson();
                        final String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonArrayRequest";
                        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new NewsCardPresenter());
                        JsonArrayRequest newsRequest = new JsonArrayRequest(getResources().getString(R.string.get_news_for_category_url) + categoryId + getResources().getString(R.string.get_news_for_category_range_extended),
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray newsJSONArray) {
                                        if (newsJSONArray.length() > 0 && getActivity() != null) {
                                            List<NewsCard> newsCards = Arrays.asList(gson.fromJson(newsJSONArray.toString(), NewsCard[].class));
                                            for (NewsCard newsCard : newsCards) {
                                                if (!newsCard.getNewsId().equals(mSelectedArticle.getNewsId())) {
                                                    newsCard.setColor(mSelectedArticle.getColor());
                                                    listRowAdapter.add(newsCard);
                                                }
                                            }
                                            HeaderItem header = new HeaderItem(0, "Povezane vijesti");
                                            mAdapter.add(new ListRow(header, listRowAdapter));
                                            mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (getActivity() != null) {
                                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                                    Intent intent = new Intent(getActivity(), ErrorActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(newsRequest, REQUEST_TAG);
                    }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArray,  "com.androidtutorialpoint.volleyJsonArrayRequest");

    }



    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof NewsCard) {
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.Article,(NewsCard) item);
                intent.putExtra(DetailsActivity.CategoryId, categoryId);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }}

    private String parseNewsContent(String content) {
        Document doc = Jsoup.parse(content);

        doc.select("blockquote").remove();

        String whiteListElements = "p";  //blockquote
        String[] whiteListArray = whiteListElements.split(",");

        Whitelist whitelist = new Whitelist();
        for (String tag : whiteListArray)
            whitelist.addTags(tag);

        return Jsoup.clean(doc.toString(), whitelist).replace("&nbsp;"," ").replace("<p>","\n").replace("</p>","");
    }
}
