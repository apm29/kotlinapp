package com.apm29.kotlinapp.view.ptr;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 * Created by apm29 on 2017/9/11.
 */

public class PtrCustomLayout extends PtrFrameLayout {
    private PtrHeaderHandler mPtrHeader;
    public PtrCustomLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrCustomLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrHeader = new PtrHeaderHandler(getContext());
        setHeaderView(mPtrHeader);
        addPtrUIHandler(mPtrHeader);
    }
    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (mPtrHeader != null) {
            mPtrHeader.setLastUpdateTimeKey(key);
        }
    }
    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        if (mPtrHeader != null) {
            mPtrHeader.setLastUpdateTimeRelateObject(object);
        }
    }
}
