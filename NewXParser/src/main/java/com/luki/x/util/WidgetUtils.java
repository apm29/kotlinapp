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
package com.luki.x.util;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ListView;

import com.luki.x.XLog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Luki
 */
public class WidgetUtils {
	private static Map<ResType, Integer> mResDefaultMap = new HashMap<ResType, Integer>();

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link android.view.View#setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	/**
	 * Get the data position in the AdapterView.
	 * 
	 * @param parent parent
	 * @param position position
	 * @return position
	 */
	@SuppressWarnings("rawtypes")
	public static int getDataPosition(AdapterView parent, int position) {
		if (parent instanceof ListView) {
			ListView listView = (ListView) parent;
			int headerCount = listView.getHeaderViewsCount();
			position -= headerCount;
			if (position < 0 || position >= listView.getCount()) {
				XLog.v("", "Invalid position:" + position + " headerCount:" + headerCount);
				return -1;
			}
		}
		return position;
	}

	/**
	 * get the resource id<BR>
	 * if there is no resource match with the given name, it will be return the ResType's default res.
	 * Otherwise it will return 0
	 * 
	 * @param context context
	 * @param name name
	 * @param resType resource type
	 * @return resource id
	 * @see #setResDefault(ResType, int)
	 */
	public static int getRes(Context context, String name, ResType resType) {
		Integer defRes = mResDefaultMap.get(resType);
		return getRes(context, defRes == null ? 0 : defRes, name, resType);
	}

	/**
	 * get the resource id<BR>
	 * if there is no resource match with the given name, it will be return the <b>resDef</B>.
	 * 
	 * @param context context
	 * @param resDef the default res id. if there is no resource match with the given name, it will be returned.
	 * @param name the resource name. such as the resource name is 'icon.png' , it can be 'icon'.
	 * @param resType resource type
	 * @return resource id
	 */
	public static int getRes(Context context, int resDef, String name, ResType resType) {
		int resId = context.getResources().getIdentifier(name, resType.name().toLowerCase(Locale.getDefault()), context.getPackageName());

		if (resId == 0) {
			resId = resDef;
		}
		return resId;
	}

	/**
	 * set the ResType's default res
	 * 
	 * @param resType resource type
	 * @param resDef resource default
	 */
	public static void setResDefault(ResType resType, int resDef) {
		mResDefaultMap.put(resType, resDef);
	}

	/**
	 * resource type
	 * 
	 * @author Luki
	 */
	public enum ResType {
		STRING,
		LAYOUT,
		DRAWABLE,
		COLOR,
		RAW,
		INTEGER,
		ID,
		ANIM,
		DIMEN
	}

}
