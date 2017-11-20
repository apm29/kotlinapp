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

package com.luki.x.db;

import java.io.Closeable;
import java.io.Serializable;
import java.util.List;

/**
 * DBHelper who can convenient and unified to manage the data.<BR>
 * see more {@link #save(List)}<BR>
 * {@link #save(List)}<BR>
 * {@link #insert(Serializable)}<BR>
 * {@link #update(Serializable)}<BR>
 * {@link #delete(List)}<BR>
 * {@link #deleteBySelection(Class, DBSelection)}<BR>
 * {@link #findByBean(Serializable)}<BR>
 * {@link #findBySelection(Class, DBSelection)}<BR>
 * {@link #isOpen()}<BR>
 *
 * @author Luki
 */
public interface DBHelper extends Closeable {

	String TAG = "DBHelper";

	/**
	 * Convenience method for inserting a row into the database.
	 *
	 * @param t save data fro inserting
	 * @return the row ID of the newly inserted row, or -1 if an error occurred or exist
	 */
	<T extends Serializable> long insert(T t);

	/**
	 * Convenience method for updating rows in the database.
	 *
	 * @param t data list for updating
	 * @return the number of rows affected
	 */
	<T extends Serializable> int update(T t);

	/**
	 * Convenience method for updating or inserting rows in the database.
	 *
	 * @param t updating or inserting data
	 * @return the number of rows affected
	 */
	<T extends Serializable> int save(T t);

	/**
	 * Convenience method for updating or inserting rows in the database.
	 *
	 * @param list save data list for updating or inserting
	 * @return the number of rows affected
	 */
	<T extends Serializable> int save(List<T> list);

	/**
	 * Convenience method for deleting rows in the database.
	 *
	 * @param list data list for deleting.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a
	 * count pass "1" as the whereClause.
	 */
	<T extends Serializable> int delete(List<T> list);

	/**
	 * Convenience method for deleting rows in the database.
	 *
	 * @param t data for deleting.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a
	 * count pass "1" as the whereClause.
	 */
	<T extends Serializable> int delete(T t);

	/**
	 * Convenience method for deleting rows in the database.
	 *
	 * @param clazz data for deleting.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a
	 * count pass "1" as the whereClause.
	 */
	<T extends Serializable> int deleteBySelection(Class<T> clazz, DBSelection<T> selection);

	/**
	 * find the data with bean.
	 *
	 * @param bean which contains field' value. And that can auto consist of selection.
	 * @return T
	 */
	<T extends Serializable> T findByBean(T bean);

	/**
	 * find the data with selection.
	 *
	 * @param clazz     table and bean.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE
	 *                  itself). Passing null will return all rows for the given table.
	 * @return clazz's instance
	 */
	<T extends Serializable> T findBySelection(Class<T> clazz, DBSelection<T> selection);

	/**
	 * find the data with bean.
	 *
	 * @param bean which contains field' value. And that can auto consist of selection.
	 * @return List
	 */
	<T extends Serializable> List<T> selectByBean(T bean);

	/**
	 * find the data with selection.
	 *
	 * @param clazz     table and bean.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE
	 *                  itself). Passing null will return all rows for the given table.
	 * @return List
	 */
	<T extends Serializable> List<T> selectBySelection(Class<T> clazz, DBSelection<T> selection);

	/**
	 * close the DB
	 */
	void close();

	/**
	 *
	 */
	boolean isOpen();

}
