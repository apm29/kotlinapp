package com.apm29.guideview;


import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by apm29 on 2017/9/12.
 */

class GuideViews {
    private static Activity activity;
    private Builder builder;
    public static Builder with(Activity activity){
        GuideViews.activity = activity;
        return new Builder();
    }
    private GuideViews(Builder builder){
        init(builder);
    }

    private void init(Builder builder) {

    }

    private static class Builder{

        ArrayList<HighLight> highLights;

        Builder() {
            init();
        }
        private void init() {
            highLights=new ArrayList<>();
        }

        /**
         * @param highLight 高亮显示
         * @return
         */
        public Builder addLayer(HighLight highLight){
            highLights.add(highLight);
            return  this;
        }
        public GuideViews build(){
            return new GuideViews(this);
        }
    }
}

