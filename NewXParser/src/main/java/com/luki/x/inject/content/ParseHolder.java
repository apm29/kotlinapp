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

import com.luki.x.XLog;
import com.luki.x.util.ReflectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author Luki
 * @version 1 Jan 6, 2015 10:49:42 AM
 * @since 1.0
 */
public class ParseHolder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9218583647471912455L;
	public String click = null;
	public String longclick = null;
	public String format = null;
	public String value = null;
	public String adapter = null;
	public boolean ignore = false;
	public boolean skip = false;

	public void onParse(String key) {
		if (!key.contains(":") && !key.contains("|")) {
			value = key;
			return;
		}
		String[] keySet = key.split("[|]");
		for (int i = 0; i < keySet.length; i++) {
			String temp = keySet[i].trim();
			try {
				String name = "";
				Object value = null;
				if (temp.contains(":")) {
					String[] keys = temp.split("[:]");
					name = keys[0];
					value = keys[1];
				} else {
					name = temp;
					value = true;
				}
				Field field = ReflectUtils.getField(getClass(), name);
				if (field != null) {
					try {
						field.set(this, value);
					} catch (Exception e) {
						field.set(this, true);
					}
				} else {
					XLog.w("InjectUtils", "can't find field '%s' in %s", name, getClass());
					for (Field f : getClass().getDeclaredFields()) {
						XLog.d("InjectUtils", "field:%s", f.getName());
					}
				}
			} catch (Exception e) {
				XLog.w(null, e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParseHolder [" + (value == null ? "" : "value=" + value) + (format == null ? "" : ", format=" + format)
				+ (click == null ? "" : ", click=" + click) + (longclick == null ? "" : ", longclick=" + longclick)
				+ (adapter == null ? "" : ", adapter=" + adapter) + (ignore == false ? "" : ", ignore=" + ignore)
				+ (skip == false ? "" : ", skip=" + skip) + "]";
	}
}
