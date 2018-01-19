package com.apm29.kotlinapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apm29.beanmodule.beans.ding.ProjectBean;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * 带header的RecyclerView的Adapter
 * Created by apm29 on 2018/01/19.
 */

public abstract class HeaderRVAdapter<T> extends RecyclerView.Adapter<HeaderRVAdapter.BaseHolder> {

    private static final int VIEW_TYPE_HEADER = 100;
    private static final int VIEW_TYPE_ITEM = 200;
    //原始数据
    private String[] headerTitles;
    private List<T>[] mainData = null;
    private int[] headerIndex;
    //处理后数据
    private SparseArray<String> headerMap;
    private List<Object> data;

    public HeaderRVAdapter(String[] headerTitles, List<T>[] mainData) {
        this.headerTitles = headerTitles;
        this.mainData = mainData;
        processMainData(mainData);
    }

    private void processMainData(List<T>[] mainData) {
        if (mainData == null || mainData.length == 0) {
            return;
        }
        //header索引号赋值
        headerIndex = new int[mainData.length];
        int idx = 0;
        for (int i = 0; i < mainData.length; i++) {
            headerIndex[i] = idx;
            idx++;
            idx += mainData[i] == null ? 0 : mainData[i].size();
        }
        //headerMap
        if (headerMap == null) headerMap = new SparseArray<>();
        else headerMap.clear();
        headerMap = new SparseArray<>(headerTitles.length);
        for (int i = 0; i < headerTitles.length; i++) {
            headerMap.put(headerIndex[i], headerTitles[i]);
        }
        if (data == null) data = new ArrayList<>();
        else data.clear();
        //data
        int index = 0;
        for (List<T> subData : mainData) {
            data.add(headerTitles[index]);
            if (subData != null && data.addAll(subData)) {
                index += 1;
            }
        }
    }

    /**
     * 更新adapter
     *
     * @param headerTitles 标题s
     * @param mainData     数据array,应该包含多个ArrayList
     */
    public void update(String[] headerTitles, List<T>[] mainData) {
        this.headerTitles = headerTitles;
        this.mainData = mainData;
        processMainData(mainData);
        notifyDataSetChanged();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder;
        if (viewType == VIEW_TYPE_HEADER) {
            holder = getHeaderHolder(parent);
        } else {
            holder = getItemHolder(parent);
        }
        holder.itemView.getLayoutParams().height=ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.itemView.getLayoutParams().width=ViewGroup.LayoutParams.MATCH_PARENT;
        return holder;
    }

    public abstract BaseHolder getHeaderHolder(ViewGroup parent);

    public abstract BaseHolder getItemHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            holder.configViews(holder.itemView, position, data);
        } else {
            holder.configViews(holder.itemView, position, data);
        }
    }


    @Override
    public int getItemViewType(int position) {
        for (int aHeaderIndex : headerIndex) {
            if (position == aHeaderIndex)
                return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static abstract class BaseHolder extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
        }

        public abstract void configViews(View itemView, int position, List<Object> data);
    }

    public static class DefaultHeaderHolder extends BaseHolder {

        private TextView tvTitle;

        public DefaultHeaderHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
        }


        @Override
        public void configViews(View itemView, int position, List<Object> data) {
            tvTitle.setText(String.format("Title: %s", data.get(position)));
            tvTitle.setBackgroundColor(Color.GRAY);
        }
    }

    public static class DefaultItemHolder extends BaseHolder {
        private TextView tvTitle;

        public DefaultItemHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView;
        }


        @Override
        public void configViews(View itemView, int position, List<Object> data) {
            Object bean = data.get(position);
            if (bean instanceof ProjectBean) {
                tvTitle.setText(((ProjectBean) bean).getTitle());
            } else
                tvTitle.setText((CharSequence) bean);
        }
    }
}
