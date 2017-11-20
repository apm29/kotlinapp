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
import java.util.List;

/**
 * 数据库表结构
 * 
 * @author Luki
 * @param <T>
 */
public class Table<T extends Serializable> {

	public String tableName;
	public Class<T> tableClass;
	public boolean isExist;
	public List<Field> otherTypeField = new ArrayList<>();

	public DBSelection<T> uniqueSelection;

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", tableClass=" + tableClass + ", isExist=" + isExist + ", uniqueSelection="
				+ uniqueSelection + "]";
	}

}
