package com.telegroup.nezavisnetvapp.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telegroup.nezavisnetvapp.R;

import java.util.logging.Logger;

public class NewsCardView extends ImageCardView {
    private TextView mNewsTitle;
    ObjectAnimator mFadeInAnimator;
    private boolean mAttachedToWindow;
    private TextView mNewsSubcategory;

    public ImageView getNewsImageView() {
        return mNewsImage;
    }

    private ImageView mNewsImage;

    public NewsCardView(Context context, int styleResId) {
        super(new ContextThemeWrapper(context, styleResId), null, 0);
        buildLoadingCardView(styleResId);
    }

    public NewsCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getStyledContext(context, attrs, defStyleAttr), attrs, defStyleAttr);
        buildLoadingCardView(getImageCardViewStyle(context, attrs, defStyleAttr));
    }

    private void buildLoadingCardView(int styleResId) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCardType(CARD_TYPE_MAIN_ONLY);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.card_layout, this);
        mNewsTitle=view.findViewById(R.id.news_title);
        mNewsTitle.setPadding(10,0,10,0);
        mNewsImage=view.findViewById(R.id.news_image);
        mNewsSubcategory=view.findViewById(R.id.news_subcategory);
        mNewsSubcategory.setPadding(5,2,5,2);
        mFadeInAnimator = ObjectAnimator.ofFloat(mNewsImage, ALPHA, 1f);
        mFadeInAnimator.setDuration(
                mNewsImage.getResources().getInteger(android.R.integer.config_shortAnimTime));
        TypedArray cardAttrs =
                getContext().obtainStyledAttributes(
                        styleResId, android.support.v17.leanback.R.styleable.lbImageCardView);
        cardAttrs.recycle();
    }


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public void setCardText(String string) {
        //mNewsTitle.setText(string.trim());
        mNewsTitle.setText(string.trim());
        mNewsTitle.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("K:"+mNewsTitle.getLineCount());
                if (mNewsTitle.getLineCount()>1) {
                    int px = (int) pxFromDp(getContext(), 20 * mNewsTitle.getLineCount());
                    System.out.println("PX:" + px);

                    mNewsTitle.getLayoutParams().height = px;
                    //mNewsTitle.setLayoutParams(params);
                    System.out.println("REAL" + mNewsTitle.getHeight());
                }
            }
        });
    }

    public void setCardSubCategory(String string){
        mNewsSubcategory.setText(string);
    }

    private static Context getStyledContext(Context context, AttributeSet attrs, int defStyleAttr) {
        int style = getImageCardViewStyle(context, attrs, defStyleAttr);
        return new ContextThemeWrapper(context, style);
    }

    private static int getImageCardViewStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        // Read style attribute defined in XML layout.
        int style = null == attrs ? 0 : attrs.getStyleAttribute();
        if (0 == style) {
            // Not found? Read global ImageCardView style from Theme attribute.
            TypedArray styledAttrs =
                    context.obtainStyledAttributes(
                            android.support.v17.leanback.R.styleable.LeanbackTheme);
            style = styledAttrs.getResourceId(
                    android.support.v17.leanback.R.styleable.LeanbackTheme_imageCardViewStyle, 0);
            styledAttrs.recycle();
        }
        return style;
    }

    public NewsCardView(Context context) {
        this(context, null);
    }

    public NewsCardView(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v17.leanback.R.attr.imageCardViewStyle);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private void fadeIn() {
        mNewsImage.setAlpha(0f);
        if (mAttachedToWindow) {
            mFadeInAnimator.start();
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        if (mNewsImage.getAlpha() == 0) {
            fadeIn();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttachedToWindow = false;
        mFadeInAnimator.cancel();
        mNewsImage.setAlpha(1f);
        super.onDetachedFromWindow();
    }
}
