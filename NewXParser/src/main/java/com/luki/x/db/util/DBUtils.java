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
package com.luki.x.db.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.luki.x.db.DBHelper;
import com.luki.x.XLog;
import com.luki.x.db.DBSelection;
import com.luki.x.db.Table;
import com.luki.x.db.TableInfo;
import com.luki.x.db.annotation.Encrypt;
import com.luki.x.db.annotation.Expose;
import com.luki.x.db.annotation.NotNull;
import com.luki.x.db.annotation.OrderBy;
import com.luki.x.db.annotation.OrderBy.SortAs;
import com.luki.x.db.annotation.Rename;
import com.luki.x.db.annotation.TableVersion;
import com.luki.x.db.annotation.Unique;
import com.luki.x.util.DESUtil;
import com.luki.x.util.ReflectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {

	private final String TAG = "XDBHelper";
	@SuppressWarnings("unused")
	public static final String TABLE_INFO = "table_info";
	public static final String PRIMARY_KEY_COLUMN = "_id";
	public static final String TIME_COLUMN = "_modify_time";

	public static Map<String, Table> tableMap = new HashMap<>();
	private static final Map<String, DBUtils> instanceMap = new HashMap<>();
	private SQLiteDatabase db;
	private String dbName;
	private DBHelper helper;

	private DBUtils(SQLiteDatabase db, String dbName, DBHelper helper) {
		this.db = db;
		this.dbName = dbName;
		this.helper = helper;
	}

	public static DBUtils getInstance(SQLiteDatabase db, String dbName, DBHelper helper) {
		DBUtils dbUtils;
		if ((dbUtils = instanceMap.get(dbName)) == null) {
			dbUtils = new DBUtils(db, dbName, helper);
			instanceMap.put(dbName, dbUtils);
		}
		return dbUtils;
	}

	/**
	 * Unique Selection
	 * 
	 * @param clazz class
	 * @return DBSelection
	 */
	public <T extends Serializable> DBSelection<T> getUniqueSelection(Class<T> clazz) {
		DBSelection<T> dbSelection = new DBSelection<>();
		StringBuilder selection = new StringBuilder();
		Field[] fields = clazz.getDeclaredFields();
		List<Field> l = new ArrayList<>();
		for (Field f : fields) {
			f.setAccessible(true);
			if (isNotExcept(f) && f.getAnnotation(Unique.class) != null) {
				String name = f.getName();
				selection.append("`");
				selection.append(name);
				selection.append("`");
				selection.append("=?");
				selection.append(" AND");
				l.add(f);
			}
		}
		if (selection.length() > 4) {
			selection.delete(selection.length() - 4, selection.length());
		}
		dbSelection.selection = selection.toString();
		dbSelection.setUniqueSelections(l);
		return dbSelection;
	}

	/**
	 * ContentValues
	 * 
	 * @param bean bean
	 * @return DBSelection
	 */
	public <T extends Serializable> DBSelection<T> getSelection(T bean) {
		DBSelection<T> dbSelection = new DBSelection<>();
		StringBuilder selection = new StringBuilder();
		StringBuilder orderBy = new StringBuilder();
		List<Field> orderByList = new ArrayList<>();
		List<String> values = new ArrayList<>();
		Field[] fields = bean.getClass().getDeclaredFields();
		boolean isSort = false;
		for (Field f : fields) {
			f.setAccessible(true);
			if (isNotExcept(f)) {
				Object value = null;
				try {
					value = f.get(bean);
				} catch (Exception e) {
					XLog.w(TAG, e);
				}
				String name = f.getName();
				if (((value instanceof Number && ((Number) value).intValue() != 0) || value instanceof String || value instanceof Byte[])) {
					selection.append("`");
					selection.append(name);
					selection.append("`");
					selection.append("=?");
					selection.append(" AND ");
					values.add(String.valueOf(value));
				} else if (value != null) {
					//
				}
				OrderBy ob = f.getAnnotation(OrderBy.class);
				if (ob != null) {
					orderByList.add(f);
					if (ob.orderPosition() != 0) {
						isSort = true;
					}
				}
			}
		}
		if (selection.length() > 4) {
			selection.delete(selection.length() - 4, selection.length());
		}
		String[] selectionArgs = new String[values.size()];
		values.toArray(selectionArgs);
		if (orderByList.size() > 0) {
			Field[] fieldArray = new Field[orderByList.size()];
			orderByList.toArray(fieldArray);
			for (int i = 0; i < fieldArray.length - 1 && isSort; i++) { //
				for (int j = 0; j < fieldArray.length - i - 1; j++) { //
					int p1 = fieldArray[j].getAnnotation(OrderBy.class).orderPosition();
					int p2 = fieldArray[j + 1].getAnnotation(OrderBy.class).orderPosition();
					if (p1 > p2) {
						Field temp = fieldArray[j];
						fieldArray[j] = fieldArray[j + 1];
						fieldArray[j + 1] = temp;
					}
				}
			}
			for (Field field : fieldArray) {
				orderBy.append("`");
				orderBy.append(field.getName());
				orderBy.append("`");
				orderBy.append(field.getAnnotation(OrderBy.class).value() == SortAs.ASC ? " ASC" : " DESC");
				orderBy.append(",");
			}
		}
		if (orderBy.length() > 1) {
			orderBy.deleteCharAt(orderBy.length() - 1);
		}
		dbSelection.selection = selection.length() > 0 ? selection.toString() : null;
		dbSelection.selectionArgs = selection.length() > 0 ? selectionArgs : null;
		dbSelection.orderBy = orderBy.length() > 0 ? orderBy.toString() : null;
		return dbSelection;
	}

	private boolean isNotExcept(Field f) {
		return !Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()) && f.getAnnotation(Expose.class) == null;
	}

	/**
	 * ContentValues
	 * 
	 * @param bean bean
	 * @return ContentValues
	 * @throws Exception
	 */
	public <T extends Serializable> ContentValues getContentValues(T bean) throws Exception {
		ContentValues values = new ContentValues();
		boolean hasUnique = false;
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			if (isNotExcept(f)) {
				if (f.getAnnotation(Unique.class) != null) {
					hasUnique = true;
				}
				Object value = null;
				try {
					value = f.get(bean);
				} catch (Exception e) {
					XLog.w(TAG, e);
				}
				String name = f.getName();
				if (f.getAnnotation(Encrypt.class) != null && value != null && ReflectUtils.isNormalGenericType(value.getClass())) {
					values.put(name, DESUtil.encrypt(value.toString(), name));
				} else if (value instanceof Long) {
					values.put(name, (Long) value);
				} else if (value instanceof String) {
					values.put(name, (String) value);
				} else if (value instanceof Double) {
					values.put(name, (Double) value);
				} else if (value instanceof Integer) {
					values.put(name, (Integer) value);
				} else if (value instanceof Float) {
					values.put(name, (Float) value);
				} else if (value instanceof Short) {
					values.put(name, (Short) value);
				} else if (value instanceof Byte) {
					values.put(name, (Byte) value);
				} else if (value instanceof Boolean) {
					values.put(name, (Boolean) value);
				} else if (value instanceof byte[]) {
					values.put(name, (byte[]) value);
				} else {
					//
				}
			}
		}
		if (!hasUnique) {
			values.put(PRIMARY_KEY_COLUMN, (String) null);
		}
		values.put(TIME_COLUMN, System.currentTimeMillis());
		return values;
	}

	/**
	 * create the table with the class.
	 * 
	 * @param clazz clazz
	 */
	public <T extends Serializable> void createTable(Class<T> clazz) {
		db.execSQL(getCreateTableSQL(clazz, null));
	}

	/**
	 * get create table sql
	 * 
	 * @param clazz clazz
	 * @param tableName tableName
	 * @return SQL String
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> String getCreateTableSQL(Class<T> clazz, String tableName) {
		if (TextUtils.isEmpty(tableName)) {
			tableName = clazz.getSimpleName();
		}
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
		Field[] fields = clazz.getDeclaredFields();
		sql.append("`");
		sql.append(PRIMARY_KEY_COLUMN);
		sql.append("`");
		sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT ");
		sql.append(",");
		for (Field field : fields) {
			field.setAccessible(true);
			if (isNotExcept(field)) {
				NotNull c = field.getAnnotation(NotNull.class);
				Type type = field.getGenericType();
				if (!ReflectUtils.isNormalGenericType(type)) {
					sql.append("`");
					sql.append(field.getName());
					sql.append("`");
					Class<T> clazz1 = null;
					if ((field.getType() == List.class || field.getType() == ArrayList.class) && type instanceof ParameterizedType) {
						clazz1 = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
					} else if (type instanceof Class) {
						clazz1 = (Class<T>) type;
					}
					if (clazz1 != null && !ReflectUtils.isNormalGenericType(clazz1)) {
						checkTable(clazz1);
					}
				} else {
					sql.append("`");
					sql.append(field.getName());
					sql.append("`");
				}
				sql.append(" TEXT");
				if (c != null) {
					sql.append(" NOT NULL ");
				}
				sql.append(",");
			}
		}
		sql.append("`");
		sql.append(TIME_COLUMN);
		sql.append("`");
		sql.append(" TEXT");
		sql.append(");");
		XLog.v(TAG, sql.toString());
		return sql.toString();
	}

	/**
	 * set object
	 * 
	 * @param bean bean
	 * @param c cursor
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T extends Serializable> void setObject(T bean, Cursor c) throws Exception {
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			String columnName = f.getName();
			int columnIndex = c.getColumnIndex(columnName);
			if (columnIndex == -1) {
				continue;
			}
			if (ReflectUtils.isNormalGenericType(f.getGenericType())) {
				String columnValue = c.getString(columnIndex);
				Object v = null;
				if (f.getAnnotation(Encrypt.class) != null) {
					columnValue = DESUtil.decrypt(columnValue, columnName);
				}
				if (f.getGenericType() == Long.class || f.getGenericType() == long.class) {
					v = Long.valueOf(columnValue);
				} else if (f.getGenericType() == String.class) {
					v = String.valueOf(columnValue);
				} else if (f.getGenericType() == Double.class || f.getGenericType() == double.class) {
					v = Double.valueOf(columnValue);
				} else if (f.getGenericType() == Integer.class || f.getGenericType() == int.class) {
					v = Integer.valueOf(columnValue);
				} else if (f.getGenericType() == Float.class || f.getGenericType() == float.class) {
					v = Float.valueOf(columnValue);
				} else if (f.getGenericType() == Short.class || f.getGenericType() == short.class) {
					v = Short.valueOf(columnValue);
				} else if (f.getGenericType() == Boolean.class || f.getGenericType() == boolean.class) {
					v = Boolean.valueOf(columnValue);
				} else if (f.getGenericType() == Byte[].class || f.getGenericType() == byte[].class) {
					v = c.getBlob(columnIndex);
				}
				f.set(bean, v);
			} else {

			}
		}
	}

	/**
	 * Creates a new instance of the class represented base on the {@link Cursor} by this Class object. The class is
	 * instantiated as if by a new
	 * expression with an empty argument list. The class is initialized if it has not already been initialized.
	 * 
	 * @param clazz clazz
	 * @param c cursor
	 * @return T
	 */
	public <T extends Serializable> T getObject(Class<T> clazz, Cursor c) throws Exception {
		T t = clazz.newInstance();
		setObject(t, c);
		return t;
	}

	/**
	 * check table is exist.
	 * 
	 * @param clazz class
	 * @return the table instance.
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends Serializable> Table<T> checkTable(Class<T> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("XDBHelper when create or check table , the class can't be null.");
		}
		String tableName = clazz.getSimpleName();
		String key = dbName + "_" + tableName;
		Table<T> table = tableMap.get(key);
		if (table == null) {
			table = new Table<>();
			table.tableName = tableName;
			table.tableClass = clazz;
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (!ReflectUtils.isNormalGenericType(field.getGenericType()) && isNotExcept(field)) {
					table.otherTypeField.add(field);
				}
			}
			tableMap.put(key, table);
		} else
			return table;
		Table<TableInfo> tableInfo = checkTable(TableInfo.class);
		Cursor cursor = null;
		try {
			table.uniqueSelection = getUniqueSelection(clazz);
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table.tableName + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					table.isExist = true;
				}
			}
			TableInfo info = new TableInfo();
			info.tableClass = clazz.getName();
			info.tableName = clazz.getSimpleName();
			if (!table.isExist) {
				createTable(clazz);
				db.insert(tableInfo.tableName, null, getContentValues(info));
				table.isExist = true;
			} else {
				TableVersion tableVersion = clazz.getAnnotation(TableVersion.class);
				int version = tableVersion == null ? 1 : tableVersion.value();
				int dbVersion = 1;
				TableInfo result = helper.findByBean(info);
				if (result != null) {
					dbVersion = result.tableVersion;
				}
				if (version > dbVersion) {
					XLog.start(TAG, "MODIFY TABLE" + info.tableName);
					db.beginTransaction();
					try {
						checkFieldInTable(clazz);
						db.execSQL("UPDATE " + tableInfo.tableName + " SET tableVersion = " + version + " WHERE tableName = '"
								+ table.tableName + "'");
						db.setTransactionSuccessful();
					} catch (Exception e) {
						XLog.w(TAG, e);
					}
					db.endTransaction();
					XLog.end(TAG, "MODIFY TABLE" + info.tableName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) cursor.close();
		}
		return table;
	}

	public <T extends Serializable> void checkFieldInTable(Class<T> clazz) {
		String tableName = clazz.getSimpleName();
		String sql = "SELECT * FROM " + tableName + " WHERE 1 != 1";
		Cursor c = db.rawQuery(sql, null);
		if (c == null) {
			return;
		}
		Field[] fields = clazz.getDeclaredFields();
		List<String> addColumns = new ArrayList<>();
		Map<String, List<String>> renameColumns = new HashMap<>();
		StringBuilder newColumns = new StringBuilder(PRIMARY_KEY_COLUMN + "," + TIME_COLUMN);
		StringBuilder originalColumns = new StringBuilder(PRIMARY_KEY_COLUMN + "," + TIME_COLUMN);
		for (Field f : fields) {
			f.setAccessible(true);
			if (!isNotExcept(f)) {
				continue;
			}
			String columnName = f.getName();
			Rename r = f.getAnnotation(Rename.class);
			String[] names = r == null ? new String[] {} : dealEmptyValue(r.value());
			if (names.length > 0) {
				renameColumns.put(columnName, Arrays.asList(names));
			}
			boolean isExist = c.getColumnIndex(columnName) > 0;
			if (isExist) {// exist column. no change
				addColumn(newColumns, originalColumns, columnName, columnName);
			} else { // not exist. delete or rename or add
				if (r != null && names.length > 0) { // rename.
					for (String originalColumn : names) {
						isExist = c.getColumnIndex(originalColumn) > 0;
						if (isExist) {
							addColumn(newColumns, originalColumns, columnName, originalColumn);
							break;
						}
					}
				} else {// add
					addColumns.add(columnName);
					XLog.v(TAG, "add " + columnName);
				}
			}
		}

		if (!newColumns.toString().equals(originalColumns.toString()) || !renameColumns.isEmpty()) {
			String tempTableName = "temp_" + tableName;
			db.execSQL(getCreateTableSQL(clazz, tempTableName));

			sql = "INSERT INTO " + tempTableName + "(" + newColumns.toString() + ") SELECT " + originalColumns.toString() + " FROM "
					+ tableName;
			XLog.v(TAG, sql);
			db.execSQL(sql);
			sql = "DROP TABLE " + tableName;
			XLog.v(TAG, sql);
			db.execSQL(sql);
			createTable(clazz);
			sql = "INSERT INTO " + tableName + "(" + newColumns.toString() + ") SELECT " + newColumns.toString() + " FROM " + tempTableName;
			XLog.v(TAG, sql);
			db.execSQL(sql);
			sql = "DROP TABLE " + tempTableName;
			XLog.v(TAG, sql);
			db.execSQL(sql);
		}
		if (!addColumns.isEmpty()) {
			for (String column : addColumns) {
				sql = "ALTER TABLE " + tableName + " ADD COLUMN " + column + " TEXT DEFAULT '';";
				db.execSQL(sql);
				XLog.v(TAG, sql);
			}
		}

		c.close();
	}

	private void addColumn(StringBuilder newColumns, StringBuilder originalColumns, String columnName, String originalColumn) {
		newColumns.append(",");
		newColumns.append("`");
		newColumns.append(columnName);
		newColumns.append("`");
		originalColumns.append(",");
		originalColumns.append("`");
		originalColumns.append(originalColumn);
		originalColumns.append("`");
	}

	private String[] dealEmptyValue(String[] args) {
		List<String> list = new ArrayList<>();
		for (String string : args) {
			if (!TextUtils.isEmpty(string)) {
				list.add(string);
			}
		}
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}

}
