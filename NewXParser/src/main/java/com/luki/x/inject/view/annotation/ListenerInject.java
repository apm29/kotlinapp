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
package com.luki.x.inject.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject the method with it's annotation, and this annotation only can used by the method.<BR>
 * e.g. <pre>
 * ＠ViewListener(ids = {R.id.button1, R.id.button2 }, type = ListenerType.CLICK)
 * public void onClick(View v) {
 * 		...
 * }</pre>
 * the frame will auto to inject with the OnClickListener of
 * the method onClick to the ids of R.id.button1 and R.id.button2.
 * also add the OnLongClickListener with the method to the ids of views, and other listener like it.
 * 
 * @author Luki
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerInject {
	/**
	 * id collection note：{1,2,3}
	 * 
	 * @return ids
	 */
	int[] value() default 0;

	/**
	 * Enum ListenerType
	 * 
	 * @return listener type
	 */
	ListenerType type() default ListenerType.CLICK;

	/**
	 * @author Luki
	 */
	enum ListenerType {
		/**
		 * {@link android.view.View.OnClickListener}<br>
		 * public void onClick(View v) {<BR>
		 * }
		 */
		CLICK,
		/**
		 * {@link android.view.View.OnLongClickListener}<BR>
		 * public boolean onLongClick(View v) {<BR>
		 * }
		 */
		LONG_CLICK,
		/**
		 * {@link android.widget.AdapterView.OnItemClickListener}<BR>
		 * public void onItemClick(AdapterView<?> parent, View view, int position, long id) {<BR>
		 * }
		 */
		ITEM_CLICK,
		/**
		 * {@link android.widget.AdapterView.OnItemLongClickListener}<BR>
		 * public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {<BR>
		 * }
		 */
		ITEM_LONG_CLICK,
		/**
		 * {@link android.view.View.OnTouchListener}<br>
		 * public boolean onTouch(View v, MotionEvent event) {<BR>
		 * }
		 */
		TOUCH,
		/**
		 * {@link android.widget.CompoundButton.OnCheckedChangeListener}<BR>
		 * public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {<BR>
		 * }
		 */
		CHECKED,
		/**
		 * {@link android.widget.AdapterView.OnItemSelectedListener}<BR>
		 * public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {<BR>
		 * }
		 */
		SELECTED,
		/**
		 * {@link android.widget.AdapterView.OnItemSelectedListener}<BR>
		 * public void onNothingSelected(AdapterView<?> parent) {<BR>
		 * }
		 */
		NO_SELECT
	}
}
