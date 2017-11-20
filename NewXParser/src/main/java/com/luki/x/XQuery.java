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

package com.luki.x;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.luki.x.inject.view.annotation.ListenerInject.ListenerType;

import static com.luki.x.inject.view.InjectEventControl.linkedToMethod;

/**
 * @author Luki
 */
public class XQuery {
	private static final String TAG = XQuery.class.getSimpleName();
	private View mContainer;
	private View view;
	private Object target;

	public XQuery(Activity activity) {
		if (activity != null) {
			mContainer = activity.getWindow().getDecorView();
			target = activity;
		} else
			throw new IllegalArgumentException("activity is null");
	}

	public XQuery(View view, Object target) {
		if (view != null) {
			mContainer = view;
			if (target == null) {
				target = new Object();
			}
			this.target = target;
		} else
			throw new IllegalArgumentException("view is null");
	}

	public XQuery id(int id) {
		view = mContainer.findViewById(id);
		if (view == null) {
			XLog.w(TAG, "---------------------------- id(" + id + ") is not exist in this view or activity ----------------------------");
			view = new View(mContainer.getContext());
		}
		return this;
	}

	public XQuery click(String methodName) {
		linkedToMethod(target, ListenerType.CLICK, view, methodName);
		return this;
	}

	public XQuery click(OnClickListener l) {
		view.setOnClickListener(l);
		return this;
	}

	public XQuery longClick(String methodName) {
		linkedToMethod(target, ListenerType.LONG_CLICK, view, methodName);
		return this;
	}

	public XQuery longClick(OnLongClickListener l) {
		view.setOnLongClickListener(l);
		return this;
	}

	public XQuery itemClick(String methodName) {
		linkedToMethod(target, ListenerType.ITEM_CLICK, view, methodName);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public XQuery itemClick(OnItemClickListener l) {
		if (view instanceof AdapterView) {
			((AdapterView) view).setOnItemClickListener(l);
		}
		return this;
	}

	public XQuery itemLongClick(String methodName) {
		linkedToMethod(target, ListenerType.ITEM_LONG_CLICK, view, methodName);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public XQuery itemLongClick(OnItemLongClickListener l) {
		if (view instanceof AdapterView) {
			((AdapterView) view).setOnItemLongClickListener(l);
		}
		return this;
	}

	public XQuery visibility(int visibility) {
		view.setVisibility(visibility);
		return this;
	}

	public XQuery visible() {
		view.setVisibility(View.VISIBLE);
		return this;
	}

	public XQuery image(int resId) {
		if (view instanceof ImageView) {
			((ImageView) view).setImageResource(resId);
		}
		return this;
	}

	public XQuery image(Drawable drawable) {
		if (view instanceof ImageView) {
			((ImageView) view).setImageDrawable(drawable);
		}
		return this;
	}

	public XQuery background(int resId) {
		view.setBackgroundResource(resId);
		return this;
	}

	@SuppressWarnings("deprecation")
	public XQuery background(Drawable drawable) {
		view.setBackgroundDrawable(drawable);
		return this;
	}

	public XQuery text(String text) {
		if (view instanceof TextView) {
			((TextView) view).setText(text);
		}
		return this;
	}

	public View view() {
		return view;
	}
}
