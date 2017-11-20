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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * DBSelection 
 * 
 * @author Luki
 * @param <T>
 *
 */
public class DBSelection<T extends Serializable> {
	public String selection;
	private List<Field> uniqueSelections = new ArrayList<Field>();
	public String[] selectionArgs;
	public String orderBy;

	public DBSelection<T> fillIn(T t) {
		selectionArgs = new String[uniqueSelections.size()];
		for (int i = 0; i < selectionArgs.length; i++) {
			try {
				selectionArgs[i] = uniqueSelections.get(i).get(t).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public void setUniqueSelections(List<Field> uniqueSelections) {
		this.uniqueSelections = uniqueSelections;
	}

	@Override
	public String toString() {
		return "DBSelection [selection=" + selection + ", selectionArgs=" + Arrays.toString(selectionArgs) + "]";
	}
}