package com.apm29.kotlinapp.ui.adapter;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 带header的RecyclerView的Adapter
 * Created by apm29 on 2018/01/19.
 */

public  class HeaderRVAdapter<T> extends RecyclerView.Adapter<HeaderRVAdapter.BaseHolder> {

    private static final int VIEW_TYPE_HEADER = 100;
    private String[] headerTitles;
    private List<T>[] mainData = null;
    private int[] headerIndex;
    private SparseArray<String> headerMap;

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
        headerIndex[0] = 0;
        for (int i = 0; i < mainData.length - 1; i++) {
            headerIndex[i + 1] = mainData[i] == null ? 0 : mainData[i].size();
        }
        //headerMap
        headerMap= new SparseArray<>(headerTitles.length);
        for (int i = 0; i < headerTitles.length; i++) {
            headerMap.put(headerIndex[i],headerTitles[i]);
        }
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);
        if (viewType == VIEW_TYPE_HEADER) {
            view.setBackgroundColor(Color.DKGRAY);
            return new HeaderHolder(view);
        }
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ((TextView) holder.itemView).setText(headerMap.get(position));
        } else
            ((TextView) holder.itemView).setText(String.valueOf(position));
    }


    @Override
    public int getItemViewType(int position) {
        for (int aHeaderIndex : headerIndex) {
            if (position == aHeaderIndex)
                return VIEW_TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mainData == null) return 0;
        int count = mainData.length;
        for (List<T> aMainData : mainData) {
            count += aMainData == null ? 0 : aMainData.size();
        }
        return count;
    }

    public static class BaseHolder extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
        }
    }

    public static class HeaderHolder extends BaseHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemHolder extends BaseHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
