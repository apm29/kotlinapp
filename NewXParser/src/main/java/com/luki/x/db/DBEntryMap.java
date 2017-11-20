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
package com.luki.x.db;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Database entry map.
 * 
 * @author Luki
 */
public class DBEntryMap {

	private static Map<String, DBHelper> helperMap = new HashMap<>();
	private static final String DEFAULT_DATABASE_NAME = "xdb";

	/**
	 * create a DBHelper.
	 * 
	 * @param context context
	 * @param dbName dbName
	 * @return DBHelper
	 */
	public static DBHelper getDBHelper(Context context, String dbName) {
		if (TextUtils.isEmpty(dbName) || TextUtils.isEmpty(dbName.trim())) {
			dbName = DEFAULT_DATABASE_NAME;
		}
		DBHelper dbHelper;
		if ((dbHelper = helperMap.get(dbName)) == null) {
			synchronized (DBEntryMap.class) {
				if (helperMap.get(dbName) == null) {
					dbHelper = new XDBHelper(dbName, context);
					helperMap.put(dbName, dbHelper);
				}
			}
		}
		return dbHelper;
	}

	/**
	 * destroy the DBHelper
	 * 
	 * @param dbName dbName
	 */
	public static void destroy(String dbName) {
		DBHelper dbHelper = helperMap.remove(dbName);
		if (dbHelper != null && dbHelper.isOpen()) {
			dbHelper.close();
		}
	}

	public static void destroy() {
		String[] keySet = new String[helperMap.keySet().size()];
		helperMap.keySet().toArray(keySet);
		for (String dbName : keySet) {
			destroy(dbName);
		}
	}
}
