package com4510.thebestphotogallery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com4510.thebestphotogallery.Listeners.OnScrollChangedListener;

/**
 * Created by George on 06-Jan-18.
 */

public class ShowImageScrollView extends ScrollView {

    private OnScrollChangedListener onScrollChangedListener;

    public ShowImageScrollView(final Context context, final AttributeSet attributes, final int defStyle) {
        super(context, attributes, defStyle);
    }

    public ShowImageScrollView(final Context context, final AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public ShowImageScrollView(final Context context) {
        this(context, null);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(x - oldX, y - oldY);
        }
    }

    public void setOnScrollChangedListener(final OnScrollChangedListener listener) {
        onScrollChangedListener = listener;
    }
}
