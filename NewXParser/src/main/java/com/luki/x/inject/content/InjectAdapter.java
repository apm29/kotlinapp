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

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.luki.x.XParser;
import com.luki.x.util.InjectUtils;
import com.luki.x.util.WidgetUtils;
import com.luki.x.util.WidgetUtils.ResType;

import java.util.ArrayList;
import java.util.List;

import static com.luki.x.XConfig.SCREEN_WIDTH;

/**
 * Auto parser the contentDescription link method or set value and so no.<BR>
 * See more<BR>
 * {@link #nameGenerator(int)}<BR>
 * resource name generate rule. {@link #defaultLayoutResId()}<BR>
 * default layout resource id {@link #failedInjectView(String, View, int)}<BR>
 * parsed failed view callback {@link #configViews(InjectHolder, int)}<BR>
 * other
 *
 * @author Luki
 * @param <T>
 */
public class InjectAdapter<T> extends BaseAdapter implements XAdapter<T> {
	/** first page index default = 1 */
	protected static int FIRST_PAGE = 1;
	protected String TAG = InjectAdapter.class.getSimpleName();
	/** data source */
	protected List<T> mData = new ArrayList<>();
	/** screen width */
	private int mPageIndex = FIRST_PAGE;
	private int mRes;
	private boolean hasClearDividerHeight;

	public InjectAdapter() {
		this(0);
	}

	public InjectAdapter(int res) {
		this(null);
		this.mRes = res;
	}

	public InjectAdapter(List<? extends T> list) {
		TAG = getClass().getSimpleName();
		addAll(list);
	}

	/**
	 * replace the index of data and refresh Adapter
	 *
	 */
	public void refresh(int index, T t) {
		if (mData.size() > index && replaceable(index)) {
			mData.set(index, t);
			notifyDataSetChanged();
		}
	}

	/**
	 * whether the data which in the index can be replace.
	 *
	 * @param index index
	 * @return is be replaced
	 */
	public boolean replaceable(int index) {
		return true;
	}

	/**
	 * remove the index data and refresh.
	 *
	 * @param index index
	 */
	public T remove(int index) {
		T t = null;
		if (mData.size() > index) {
			t = mData.remove(index);
			notifyDataSetChanged();
		}
		return t;
	}

	/**
	 * add data and refresh Adapter
	 *
	 * @param list data
	 */
	public void addAll(List<? extends T> list) {
		if (list == null || list.size() == 0) {
			// L.w(TAG, "Adapter dataSource is null");
			return;
		}
		addAll(list, 0, list.size());
	}

	/**
	 * add data and refresh Adapter
	 *
	 * @param list data
	 * @param start index of start
	 * @param end index of end
	 */
	public void addAll(List<? extends T> list, int start, int end) {
		int size;
		if (list == null || (size = list.size()) == 0 || start < 0 || end > size) {
			// L.w(TAG,
			// "Adapter dataSource is null! Or start < 0! Or end > list.size()!");
			return;
		}
		mPageIndex++;
		mData.addAll(start == 0 && end == size ? list : list.subList(start, end));
		notifyDataSetChanged();
	}

	/**
	 * Current page index
	 *
	 * @return page index
	 */
	public int getPageIndex() {
		return mPageIndex;
	}

	/**
	 * clear data in the adapter and refresh adapter.
	 */
	public final void clear() {
		mPageIndex = FIRST_PAGE;
		mData.clear();
		notifyDataSetChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	public final int getCount() {
		return mData == null ? 0 : getCountX(getColumnCount());
	}

	private int getCountX(int x) {
		return mData.size() % x == 0 ? mData.size() / x : mData.size() / x + 1;
	}

	/**
	 * one row's data count.<BR>
	 * see more <BR>
	 * {@link #getItemHeight(int)}
	 *
	 * @return 条数
	 */
	protected int getColumnCount() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public final T getItem(int position) {
		return mData.get(position);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public final View getView(int position, View convertView, ViewGroup parent) {
		check(parent);
		int rowCount = getColumnCount();
		position *= rowCount;
		View[] views;

		if (rowCount == 1) {
			views = createViews(position, 1, new View[] { convertView }, parent);
			convertView = views[0];
		} else {
			if (convertView == null) {
				views = new View[rowCount];
				createViews(position, rowCount, views, parent);

				int width = SCREEN_WIDTH - parent.getPaddingLeft() - parent.getPaddingLeft();
				if (parent.getLayoutParams() instanceof MarginLayoutParams) {
					MarginLayoutParams mlp = (MarginLayoutParams) parent.getLayoutParams();
					width -= mlp.leftMargin + mlp.rightMargin;
				}

				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.width = (int) (width * 1f / rowCount);
				params.height = getItemHeight(params.width) == 0 ? MarginLayoutParams.WRAP_CONTENT : getItemHeight(params.width);
				ViewGroup vg = new LinearLayout(parent.getContext());
				for (View view : views) {
					vg.addView(view, params);
				}
				convertView = vg;
				convertView.setTag(views);
			} else {
				views = (View[]) convertView.getTag();
				createViews(position, rowCount, views, parent);
			}
		}

		return convertView;
	}

	@SuppressWarnings("deprecation")
	private void check(View parent) {
		if (parent == null) {
			throw new IllegalArgumentException(TAG + "#getView() parent is null");
		} else if (getColumnCount() > 1 && !hasClearDividerHeight) {
			hasClearDividerHeight = true;
			if (parent instanceof AbsListView) {
				((AbsListView) parent).setSelector(new BitmapDrawable(parent.getContext().getResources()));
			}
			if (parent instanceof ListView) {
				((ListView) parent).setDividerHeight(0);
			}
		}
	}

	/**
	 * {@link #getColumnCount()} > 1 , return the line height
	 *
	 * @param width item width
	 * @return item height
	 */
	protected int getItemHeight(int width) {
		return LayoutParams.WRAP_CONTENT;
	}

	@SuppressWarnings("deprecation")
	private View[] createViews(int position, int rowCount, View[] views, ViewGroup parent) {
		int o = position;
		for (int i = 0; i < rowCount; i++, position++) {
			if (position < mData.size()) {
				boolean needInit = views[i] == null;
				views[i] = getItemView(position, views[i], parent);
				views[i].setTag(XParser.INSTANCE.getXConfig().HOLDER_POSITION, position);
				views[i].setVisibility(View.VISIBLE);
				if (needInit) {
					views[i].setBackgroundDrawable(getSelector());
				}
				views[i].setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						int position = (Integer) v.getTag(XParser.INSTANCE.getXConfig().HOLDER_POSITION);
						onItemClick(position);
					}
				});
				views[i].setOnLongClickListener(new View.OnLongClickListener() {

					public boolean onLongClick(View v) {
						int position = (Integer) v.getTag(XParser.INSTANCE.getXConfig().HOLDER_POSITION);
						return InjectAdapter.this.onItemLongClick(position);
					}
				});

			} else {
				if (views[i] == null) {
					views[i] = getItemView(o, views[i], parent);
				}
				views[i].setVisibility(View.INVISIBLE);
			}
		}
		return views;
	}

	/**
	 * Get the listView's selector;
	 *
	 * @return list selector
	 */
	protected Drawable getSelector() {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(0xFFFEBD25));
		drawable.addState(new int[] {}, new ColorDrawable(0));
		return drawable;
	}

	/**
	 * get item view
	 * @param position position
	 * @param convertView convert view
	 * @param parent parent
	 * @return item view
	 */
	protected View getItemView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			int resId = WidgetUtils.getRes(parent.getContext(), nameGenerator(position), ResType.LAYOUT);
			if (resId == 0) {
				resId = defaultLayoutResId();
			}
			convertView = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
		}
		convertView.setTag(XParser.INSTANCE.getXConfig().HOLDER_POSITION, position);

		restoreItem(position, convertView);

		XParser.INSTANCE.parse(this, getItem(position), convertView, new ParserCallBack() {

			@Override
			public void failedInjectView(String key, View v) {
				InjectAdapter.this.failedInjectView(key, v, position);
			}

			@Override
			public void configViews(InjectHolder holder) {
				InjectAdapter.this.configViews(holder, position);
			}
		});
		return convertView;
	}

	/**
	 * restore the item.
	 *
	 * @param position position
	 * @param convertView convert view
	 */
	protected void restoreItem(int position, View convertView) {

	}

	/**
	 * if the view failed to parse,the method will be invoked.
	 *
	 * @param key ContentDescription
	 * @param v v
	 * @param position position
	 */
	public void failedInjectView(String key, View v, int position) {}

	/**
	 * Layout item name generator.
	 *
	 * @return item resource id
	 */
	protected String nameGenerator(int position) {
		return "";
	}

	/**
	 * Default layout resource id.
	 *
	 * @return default layout resource id of the item
	 */
	protected int defaultLayoutResId() {
		return mRes;
	}

	/**
	 * configuration the contentView which already injected.
	 *
	 * @param holder holder
	 * @param position position
	 */
	public void configViews(InjectHolder holder, int position) {}

	/**
	 * item long click.
	 *
	 * @param position position
	 * @return boolean
	 */
	public boolean onItemLongClick(int position) {
		return false;
	}

	/**
	 * item click.
	 *
	 * @param position position
	 */
	public void onItemClick(int position) {

	}

	/**
	 * return the value for given key
	 *
	 * @param field field
	 * @param data data
	 * @return object
	 */
	protected Object getValueByField(String field, Object data) {
		return InjectUtils.getValueByTag(field, data);
	}

	/**
	 * return the value for given key
	 *
	 * @param key key
	 * @param data data
	 * @return object
	 */
	protected Object getValueByKey(String key, Object data) {
		ParseHolder holder = InjectUtils.getParserHolder(InjectConfig.INSTANCE.parseClass, key, null);
		return getValueByField(holder.value, data);
	}
}
