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
package com.luki.x.util;

import android.view.View;

import com.luki.x.XParser;
import com.luki.x.XLog;
import com.luki.x.inject.content.IParser;
import com.luki.x.inject.content.ParseHolder;

import java.util.List;
import java.util.Map;

/**
 * @author Luki
 */
public class InjectUtils {

	public static final String KEY_THIS = IParser.KEY_THIS;
	public static final String KEY_VISIBLE = IParser.KEY_VISIBLE;
	public static final String KEY_INVISIBLE = IParser.KEY_INVISIBLE;
	public static final String KEY_GONE = IParser.KEY_GONE;
	private static final String TAG = InjectUtils.class.getSimpleName();

	/**
	 * @param field value in content description
	 * @param data original data
	 * @return the value in data which in the rule.
	 */
	public static Object getValueByTag(String field, Object data) {
		XLog.v(TAG, "onGetDataStart : Field-> %s, Data -> %s", field, data);
		Object value = null;
		if (KEY_THIS.equals(field))
			return data;
		String[] fields = field.split("[.]");
		try {
			for (int i = 0; i < fields.length; i++) {
				String[] split = null;
				if (fields[i].contains("-")) {
					split = fields[i].split("-");
					field = split[0];
				} else
					field = fields[i];

				try {
					if (!KEY_THIS.equals(field))
						data = ReflectUtils.getFieldValue(data, field);
				} catch (Exception e1) {
					Object obj = ReflectUtils.getMethodValue(data, field);
					if (obj != null) {
						if (!obj.getClass().getSimpleName().equals("Void")) {
							data = obj;
						} else {
							throw new IllegalArgumentException("The method '" + field + "' return value must not be void");
						}
					} else {
						if (field == fields[i]) {
							data = null;
						}
					}
				}

				try {
					for (int j = 1; split != null && j < split.length; j++) {
						if (data instanceof List) {
							List<?> list = (List<?>) data;
							if (list.size() > toInt(split[j])) {
								data = list.get(toInt(split[j]));
							} else {
								XLog.v(TAG, "List's size('" + list.size() + "') < index('" + split[j] + "') in '" + field + "'. Please check carefully !!  ");
							}
						} else if (data instanceof Object[]) {
							Object[] objs = (Object[]) data;
							if (objs.length > toInt(split[j])) {
								data = objs[toInt(split[j])];
							} else {
								XLog.v(TAG, "Object array's length('" + objs.length + "') < index('" + split[j] + "') in '" + field + "'. Please check carefully !!  ");
							}
						} else if (data instanceof Map) {
							if (((Map<?, ?>) data).containsKey(split[j])) {
								data = ((Map<?, ?>) data).get(split[j]);
							} else {
								XLog.v(TAG, "Map doesn't contains key '" + split[j] + "' in '" + field + "'. Please check carefully !!  ");
							}

						}
					}
				} catch (Exception e) {
					XLog.w(TAG, e);
					break;
				}
				if (data == null) {
					break;
				}
			}
			value = data;
		} catch (Exception e) {
			Class<?> class1 = null;
			if (data != null) {
				class1 = data.getClass();
			}
			if (e.getCause() == null) {
				XLog.w(TAG, "can't find this field or method '" + field + "' in " + class1 + " . Please check carefully !! \n " + e.toString());
			} else
				XLog.w(TAG, e.getCause());
		}
		XLog.v(TAG, "onGetDataEnd : Field-> %s, Value -> %s", field, value);
		return value;
	}

	/**
	 * 转换成int
	 *
	 * @param tag tag
	 * @return int
	 */
	private static int toInt(Object tag) {
		int i = 0;
		try {
			i = Integer.parseInt(tag.toString());
		} catch (Exception ignored) {
		}
		return i;
	}

	/**
	 * @param key key
	 * @param v v
	 * @return ParseHolder
	 */
	public static ParseHolder getParserHolder(Class<? extends ParseHolder> clazz, String key, View v) {
		if (v != null && v.getTag(XParser.INSTANCE.getXConfig().HOLDER_PARSER_KEY) != null && v.getTag(XParser.INSTANCE.getXConfig().HOLDER_PARSER_KEY) instanceof ParseHolder) {
			return (ParseHolder) v.getTag(XParser.INSTANCE.getXConfig().HOLDER_PARSER_KEY);
		}
		ParseHolder holder = createParserHolder(clazz, null);
		holder.onParse(key);
		if (v != null) {
			v.setTag(XParser.INSTANCE.getXConfig().HOLDER_PARSER_KEY, holder);
		}

		return holder;
	}

	public static <T extends ParseHolder> T createParserHolder(Class<T> clazz, ParseHolder holder) {
		T t = null;
		try {
			t = clazz.newInstance();
			if (holder != null) {
				t.click = holder.click;
				t.longclick = holder.longclick;
				t.format = holder.format;
				t.value = holder.value;
				t.adapter = holder.adapter;
				t.ignore = holder.ignore;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
}
