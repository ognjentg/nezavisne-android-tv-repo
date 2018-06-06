package com.telegroup.nezavisnetvapp.header;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telegroup.nezavisnetvapp.R;

/**
 * Created by Admin on 05.06.2018.
 */

public class IconHeaderItemPresenter extends RowHeaderPresenter {
    private static final String TAG = IconHeaderItemPresenter.class.getSimpleName();

    private float mUnselectedAlpha;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        mUnselectedAlpha = viewGroup.getResources()
                .getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.icon_header_item, null);
        view.setAlpha(mUnselectedAlpha);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object o) {
//        IconHeaderItem iconHeaderItem = (IconHeaderItem) ((ListRow) o).getHeaderItem();
//        View rootView = viewHolder.view;
//
//        ImageView iconView = (ImageView) rootView.findViewById(R.id.header_icon);
//        int iconResId = iconHeaderItem.getIconResId();
//        if( iconResId != IconHeaderItem.ICON_NONE) { // Show icon only when it is set.
//            Drawable icon = rootView.getResources().getDrawable(iconResId, null);
//            iconView.setImageDrawable(icon);
//        }
//
//        TextView label = (TextView) rootView.findViewById(R.id.header_label);
//        label.setText(iconHeaderItem.getName());


        IconHeaderItem headerItem = (IconHeaderItem)((ListRow) o).getHeaderItem();
        View rootView = viewHolder.view;
        rootView.setFocusable(true);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.header_icon);
        Drawable icon = rootView.getResources().getDrawable(headerItem.getIconResId());
        iconView.setImageDrawable(icon);

        TextView label = (TextView) rootView.findViewById(R.id.header_label);
        label.setTextSize(16);
        label.setText(headerItem.getName());
    }

    public void setIconDrawable(String name, View rootView) {
        Resources res = rootView.getResources();
        //String[] categories = res.getStringArray(R.array.categories);
        //TypedArray resources = res.obtainTypedArray(R.array.icons);
        int drawableResource = 0;

//        if (name.equals(res.getString(R.string.header_text_options))) {
//            drawableResource = R.drawable.ic_settings_white_24dp;
//        } else {
//            for (int i = 0; i < categories.length; i++) {
//                if (categories[i].equals(name)) {
//                    drawableResource = resources.getResourceId(i, 0);
//                    break;
//                }
//            }
//        }
        drawableResource = R.drawable.ic_cat;
        //resources.recycle();

        if (drawableResource != 0) {
            ImageView iconView = (ImageView) rootView.findViewById(R.id.header_icon);
            Drawable icon = res.getDrawable(drawableResource, null);
            iconView.setImageDrawable(icon);
        }
    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        // no op
    }

    // TODO: TEMP - remove me when leanback onCreateViewHolder no longer sets the mUnselectAlpha,AND
    // also assumes the xml inflation will return a RowHeaderView
    @Override
    protected void onSelectLevelChanged(RowHeaderPresenter.ViewHolder holder) {
        // this is a temporary fix
        holder.view.setAlpha(mUnselectedAlpha + holder.getSelectLevel() *
                (1.0f - mUnselectedAlpha));
    }
}
