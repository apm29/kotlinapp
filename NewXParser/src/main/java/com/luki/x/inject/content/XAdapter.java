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

import android.widget.Adapter;

import java.util.List;

/**
 * Define an interface to easy use the {@link android.widget.BaseAdapter}.
 *
 * @author Luki
 * @param <T>
 */
public interface XAdapter<T> extends Adapter {

	/**
	 * replace the index of data and refresh Adapter
	 *
	 * @param index index
	 */
	void refresh(int index, T t);

	/**
	 * whether the data which in the index can be replace.
	 *
	 * @param index index
	 * @return true; item can be replace
	 */
	boolean replaceable(int index);

	/**
	 * remove the index data and refresh.
	 *
	 * @param index index
	 */
	T remove(int index);

	/**
	 * add data and refresh Adapter
	 *
	 * @param list data source
	 */
	void addAll(List<? extends T> list);

	/**
	 * Current page index
	 *
	 * @return page index
	 */
	int getPageIndex();

	/**
	 * clear data in the adapter and refresh adapter.
	 */
	void clear();
}
