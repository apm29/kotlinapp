/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luki.x.inject.content;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.luki.x.XParser;

import java.util.ArrayList;
import java.util.List;

public abstract class InjectExpandableAdapter<Group extends XExpandableAdapter<Child>, Child> extends BaseExpandableListAdapter implements XAdapter<Group> {

	private List<Group> mData = new ArrayList<Group>();
	private int mPageIndex = 1;

	@Override
	public int getGroupCount() {
		return mData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<Child> child = mData.get(groupPosition).getChild();
		return child == null ? 0 : child.size();
	}

	@Override
	public Group getGroup(int groupPosition) {
		return mData.get(groupPosition);
	}

	@Override
	public Child getChild(int groupPosition, int childPosition) {
		List<Child> child = mData.get(groupPosition).getChild();
		return child.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(groupLayoutResId(isExpanded), parent, false);
		}

		restoreGroupItem(groupPosition, convertView);

		XParser.INSTANCE.parse(this, getGroup(groupPosition), convertView, new XParserCallBack(){

			@Override
			public void failedInjectView(String key, View view) {
				failedInjectGroupView(key, view, groupPosition, isExpanded);
			}

			@Override
			public void configViews(InjectHolder holder) {
				configGroupViews(holder, groupPosition, isExpanded);
			}

			@Override
			public void ignoreView(String key, View view) {
				ignoreGroupView(key, view, groupPosition, isExpanded);
			}
		});
		return convertView;
	}

	protected void ignoreGroupView(String key, View view, int position, boolean isExpanded) {}

	protected void configGroupViews(InjectHolder holder, int position, boolean isExpanded) {}

	protected void failedInjectGroupView(String key, View view, int position, boolean isExpanded) {}

	protected void restoreGroupItem(int groupPosition, View convertView) {}

	protected abstract int groupLayoutResId(boolean isExpanded);

	protected String nameGenerator() {
		return null;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(childLayoutResId(isLastChild), parent, false);
		}

		restoreChildItem(groupPosition, childPosition, convertView);

		XParser.INSTANCE.parse(this, getChild(groupPosition, childPosition), convertView, new XParserCallBack(){

			@Override
			public void failedInjectView(String key, View view) {
				failedInjectChildView(key, view, groupPosition, childPosition, isLastChild);
			}

			@Override
			public void configViews(InjectHolder holder) {
				configChildViews(holder, groupPosition, childPosition, isLastChild);
			}

			@Override
			public void ignoreView(String key, View view) {
				ignoreChildView(key, view, groupPosition, childPosition, isLastChild);
			}
		});
		return convertView;
	}

	protected void restoreChildItem(int groupPosition, int childPosition, View convertView) {}

	protected abstract int childLayoutResId(boolean isLastChild);

	protected void ignoreChildView(String key, View view, int groupPosition, int childPosition, boolean isLastChild) {}

	protected void configChildViews(InjectHolder holder, int groupPosition, int childPosition, boolean isLastChild) {}

	protected void failedInjectChildView(String key, View view, int groupPosition, int childPosition, boolean isLastChild) {}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void addAll(List<? extends Group> groupData) {
		mData.addAll(groupData);
		mPageIndex++;
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		mData.clear();
		mPageIndex = -1;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return getGroupCount();
	}

	@Override
	public Group getItem(int position) {
		return getGroup(position);
	}

	@Override
	public long getItemId(int position) {
		return getGroupId(position);
	}

	@Override
	public int getItemViewType(int position) {
		return getGroupType(position);
	}

	@Override
	public final int getPageIndex() {
		return mPageIndex;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		return getGroupView(position, false, convertView, parent);
	}

	@Override
	public int getViewTypeCount() {
		return getGroupTypeCount();
	}

	@Override
	public final void refresh(int index, Group t) {
		if (replaceable(index)) {
			mData.set(index, t);
			notifyDataSetChanged();
		}
	}

	@Override
	public final Group remove(int index) {
		Group group = null;
		if (mData.size() > index) {
			group = mData.remove(index);
			notifyDataSetChanged();
		}
		return group;
	}
}
