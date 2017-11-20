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
 * Inject the view with it's annotation, and this annotation only can used by the field.<BR>
 * e.g. <BR>
 * ��ViewInject(id = R.id.button1, click = "onClick", longClick = "longClick")<BR>
 * Button btn1;<BR>
 * the frame will auto to inject the view with the id of R.id.button1,and add the OnClickListener of the method onClick,
 * also add the OnLongClickListener with the method to this view, and other listener like it.
 * 
 * @author Luki
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
	public int value() default 0;

	/**
	 * {@link android.view.View.OnClickListener}<br>
	 * public void onClick(View v) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String click() default "";

	/**
	 * {@link android.view.View.OnLongClickListener}<BR>
	 * public boolean onLongClick(View v) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String longClick() default "";

	/**
	 * {@link android.widget.AdapterView.OnItemClickListener}<BR>
	 * public void onItemClick(AdapterView<?> parent, View view, int position, long id) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String itemClick() default "";

	/**
	 * {@link android.widget.AdapterView.OnItemLongClickListener}<BR>
	 * public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String itemLongClick() default "";

	/**
	 * {@link android.view.View.OnTouchListener}<br>
	 * public boolean onTouch(View v, MotionEvent event) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String touch() default "";

	/**
	 * {@link android.widget.CompoundButton.OnCheckedChangeListener}<BR>
	 * public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */
	public String checked() default "";

	/**
	 * {@link android.widget.AdapterView.OnItemSelectedListener}<BR>
	 * public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }<BR>
	 * public void onNothingSelected(AdapterView<?> parent) {<BR>
	 * &nbsp;do something...&nbsp;...<BR>
	 * }
	 */

	public Select select() default @Select(selected = "");

}
