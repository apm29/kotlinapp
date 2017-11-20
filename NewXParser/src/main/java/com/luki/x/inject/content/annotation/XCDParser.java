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
package com.luki.x.inject.content.annotation;

import android.view.View;

import com.luki.x.inject.content.XParserCallBack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XCDParser {
	/**
	 * data source.
	 * 
	 * @return {@link List} or {@link Array}
	 */
	public String dataSource();

	/**
	 * View instance.
	 * 
	 * @return {@link View}
	 */
	public String view() default "";

	/**
	 * listener.
	 * 
	 * @return {@link XParserCallBack}
	 */
	public String listener() default "";
}
