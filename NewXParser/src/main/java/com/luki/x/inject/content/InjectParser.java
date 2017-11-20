/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luki.x.inject.content;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luki.x.XLog;
import com.luki.x.util.InjectUtils;
import com.luki.x.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Default Parser
 *
 * @author Luki
 */
public class InjectParser implements IParser {

	private static final String TAG = InjectParser.class.getSimpleName();

	/**
	 * {@inheritDoc}
	 */
	public void onParse(Object target, Object data, InjectHolder holder, ParserCallBack callBack) {
		if (holder.size() > 0) {
			for (String key : holder.keySet()) {
				XLog.d(TAG, "\n");
				View v = holder.get(key);
				ParseHolder ph = InjectUtils.getParserHolder(InjectConfig.INSTANCE.parseClass, key, v);
				XLog.d(TAG, "onAttachStart : Key -> %s, holder -> %s, View -> %s", key, ph.toString(), v.getClass().getSimpleName());
				if (!ph.ignore) {
					if (!TextUtils.isEmpty(ph.value)) {
						if (data != null) {
							injectView(v, key, ph, data, callBack);
						} else {
							injectView(v, key, ph, target, callBack);
						}
					}
					Click click = null;
					if (!TextUtils.isEmpty(ph.click)) {
						click = new Click(target, ph.click);
						click.position = holder.position;
						v.setOnClickListener(click);
					}
					if (!TextUtils.isEmpty(ph.longclick)) {
						if (click == null) {
							click = new Click(ph.longclick, target);
							click.position = holder.position;
						} else
							click.mLongClickMethodName = ph.longclick;
						v.setOnLongClickListener(click);
					}
				} else if (callBack instanceof XParserCallBack) {
					((XParserCallBack) callBack).ignoreView(key, v);
				}
				XLog.d(TAG, "onAttachEnd : Key -> %s, holder -> %s, View -> %s\n", key, ph.toString(), v.getClass().getSimpleName());
			}
		}

		if (callBack != null) {
			callBack.configViews(holder);
		}
	}

	/**
	 * traversal the ViewGroup
	 */
	public InjectHolder onParseView(View view) {
		InjectHolder holder = new InjectHolder();
		parseContentDescription(view, holder);
		return holder;
	}

	/* (non-Javadoc)
	 * @see luki.x.base.IParser#onAttachData(android.view.View, luki.x.inject.content.ParseHolder, java.lang.Object)
	 */
	@Override
	@SuppressWarnings({
			"rawtypes",
			"unchecked"
	})
	public void onAttachData(View v, ParseHolder ph, Object value) {
		XLog.d(TAG, "onAttachValue : Key-> %s, Value -> %s", ph.toString(), value.toString());
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {
			String str = value.toString();
			try {
				if (ph.format != null) {
					str = String.format(ph.format, value);
				}
			} catch (Exception e) {
				XLog.v(TAG, e.toString());
			}
			if (str.contains("\n") || str.contains("\r")) {
				((TextView) v).setText(str);
			} else
				((TextView) v).setText(Html.fromHtml(str));
			XLog.d(TAG, "attach : View -> TextView");
		} else if (v instanceof XImage) {
			((XImage) v).loadImageByURL(value.toString());
			XLog.d(TAG, "attach : View -> XImage");
		} else if (v instanceof ImageView) {
			if (value instanceof Number) {
				int id = ((Number) value).intValue();
				((ImageView) v).setImageResource(id);
			}
			XLog.d(TAG, "attach : View -> ImageView");
		} else if (v instanceof AdapterView || v instanceof android.widget.AdapterView) {
			try {
				InjectAdapter ada = (InjectAdapter) ReflectUtils.getClassInstance(ph.adapter);
				if (value instanceof Object[]) {
					value = Arrays.asList((Object[]) value);
				}
				if (value instanceof List) {
					ada.addAll((List) value);
				}
				if (v instanceof AdapterView) {
					((AdapterView) v).setAdapter(ada);
				} else {
					((android.widget.AdapterView) v).setAdapter(ada);
				}
				XLog.d(TAG, "attach : View -> AdapterView");
			} catch (Exception e) {
				XLog.v(TAG, e.toString());
			}
		}

	}

	/**
	 * @param v        v
	 * @param key      key
	 * @param data     data
	 * @param callBack callBack
	 */
	private void injectView(View v, String key, ParseHolder ph, Object data, ParserCallBack callBack) {
		Object value = InjectUtils.getValueByTag(ph.value, data);

		if (value != null) {
			attachValue(v, ph, value);
		} else {
			if (callBack != null) {
				callBack.failedInjectView(key, v);
			}
		}
	}

	/**
	 * attach the value to view.
	 *
	 * @param v     v
	 * @param ph    ph
	 * @param value value
	 */
	private void attachValue(View v, ParseHolder ph, Object value) {
		if (KEY_VISIBLE.equalsIgnoreCase(value.toString()) ||
				KEY_INVISIBLE.equalsIgnoreCase(value.toString()) ||
				KEY_GONE.equalsIgnoreCase(value.toString())) {
			if (KEY_VISIBLE.equalsIgnoreCase(value.toString())) {
				v.setVisibility(View.VISIBLE);
			} else if (KEY_INVISIBLE.equalsIgnoreCase(value.toString())) {
				v.setVisibility(View.INVISIBLE);
			} else {
				v.setVisibility(View.GONE);
			}
		} else {
			onAttachData(v, ph, value);
		}
	}

	/**
	 * traversal the ViewGroup
	 *
	 * @param view   view
	 * @param holder holder
	 */
	private void parseContentDescription(View view, InjectHolder holder) {
		CharSequence description = view.getContentDescription();
		if (description != null && TAG_SKIP.equals(description.toString().trim())) {
			return;
		}
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View v = vg.getChildAt(i);
				description = v.getContentDescription();
				if (description != null && TAG_SKIP.equals(description.toString().trim())) {
					continue;
				}
				putHolderIfHasContentDescription(v, holder);
				if (v instanceof ViewGroup) {
					parseContentDescription(v, holder);
				}
			}
		} else {
			putHolderIfHasContentDescription(view, holder);
		}
	}

	/**
	 * put the view into holder if it has contentDescription
	 *
	 * @param view   view
	 * @param holder holder
	 */
	private void putHolderIfHasContentDescription(View view, InjectHolder holder) {
		CharSequence description = view.getContentDescription();
		if (description != null && !"".equals(description.toString().trim())) {
			holder.put(description.toString(), view);
		}
	}

	/**
	 * LongClick, Click method.
	 *
	 * @author Luki
	 */
	private class Click implements View.OnLongClickListener, View.OnClickListener {
		private Object target;
		private String mLongClickMethodName;
		private String mClickMethodName;
		private int position = -1;

		public Click(String longClickMethodName, Object target) {
			this.target = target;
			mLongClickMethodName = longClickMethodName;
		}

		public Click(Object target, String clickMethodName) {
			this.target = target;
			mClickMethodName = clickMethodName;
		}

		public boolean onLongClick(View v) {
			return invoke(mLongClickMethodName, v);

		}

		private boolean invoke(String methodName, View v) {
			try {
				Object[] params = position >= 0 ? new Object[]{
						v,
						position
				} : new Object[]{v};

				Method method = null;
				Class<?>[] p = position >= 0 ? new Class<?>[]{
						View.class,
						int.class
				} : new Class<?>[]{View.class};
				try {
					method = target.getClass().getDeclaredMethod(methodName, p);
				} catch (Exception ignored) {
				}
				if (method == null) {
					p = position >= 0 ? new Class<?>[]{int.class} : new Class<?>[]{};
					params = position >= 0 ? new Object[]{position} : null;
					method = target.getClass().getDeclaredMethod(methodName, p);
				}
				method.setAccessible(true);
				Object obj = method.invoke(target, params);
				try {
					return Boolean.parseBoolean(obj.toString());
				} catch (Exception e) {
					return false;
				}
			} catch (Exception e) {
				XLog.w(TAG, e);
			}
			return false;
		}

		public void onClick(View v) {
			invoke(mClickMethodName, v);
		}
	}
}
