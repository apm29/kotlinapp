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

import android.view.View;
import android.widget.ListAdapter;

/**
 * auto parse the android:contentDescription with it's value. such as onClick, onLongClick, set value and so on.
 * Each attribute can be together to used. <BR>
 * e.g.<BR>
 * 
 * <pre>
 * public class Bean {
 * 	private String name = &quot;Luki&quot;;
 * 	private String tel = &quot;13666666666&quot;;
 * 	private List&lt;String&gt; strs = new ArrayList&lt;String&gt;();
 * 	private A a = new A();
 * 
 * 	public Bean() {
 * 		strs.add(&quot;string1&quot;);
 * 		strs.add(&quot;string2&quot;);
 * 		strs.add(&quot;string3&quot;);
 * 		list.add(new A());
 * 	}
 * 
 * 	String testMethod() {
 * 		return &quot;testResult&quot;;
 * 	}
 * 
 * 	static class A {
 * 		private String aName = &quot;A's Field&quot;;
 * 		private String aSample = &quot;field&quot;;
 * 
 * 		private String aSample() {
 * 			return &quot;method&quot;;
 * 		}
 * 
 * 		private String aTestMethod() {
 * 			return &quot;A's Method&quot;;
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * <B>android:contentDescription="value:name"</B><BR>
 * result -> Luki <BR>
 * <B>android:contentDescription="value:tel"</B><BR>
 * result -> 13666666666 <BR>
 * <B>android:contentDescription="value:strs-1"</B><BR>
 * result -> string2 <BR>
 * <B>android:contentDescription="value:testMethod"</B><BR>
 * result -> testResult <BR>
 * <B>android:contentDescription="value:a.aName"</B><BR>
 * result -> A's Field <BR>
 * <B>android:contentDescription="value:a.aTestMethod"</B><BR>
 * result -> A's Method <BR>
 * <B>android:contentDescription="value:a.aSample"</B><BR>
 * result -> field <BR>
 * <B>android:contentDescription="value:name|format:name:%s"</B><BR>
 * result -> name:Luki <BR>
 * <B>android:contentDescription="value:name|click:onClick"</B><BR>
 * result -> Luki . And when someone click this view , the method onClick which in the Adapter will be invoked<BR>
 * <B>android:contentDescription="value:name|longclick:onLongClick"</B><BR>
 * result -> Luki . And when someone long click this view , the method onLongClick which in the Adapter will be invoked<BR>
 * <BR>
 * And as also only write it's click method <BR>
 * ps: <B>android:contentDescription="click:onClick"</B> <BR>
 * 
 * @author Luki
 */
public interface IParser{

	/**
	 * e.g.<BR>
	 * Write as <B>android:contentDescription="value:name"</B><BR>
	 * It will auto get the value of the field or method 'name' in the data, and set the value to the remote view . Also
	 * can use
	 * '-'
	 * to get List value as 'lists-0-1' or 'lists-0-1.name'(lists-0-1's lists is the field or method in data).<BR>
	 * as also see{@link #TAG_FORMAT} <BR>
	 * <B>If the field or method's value is Boolean, it will be parsed to Visibility of the remote view.</B><BR>
	 * if you won't want to parsed the value, you can write as 'value:this'
	 * 
	 */
	String TAG_VALUE = "value";
	/**
	 * e.g.<BR>
	 * String tel = "13666666666";<BR>
	 * write as <B>android:contentDescription="value:tel|format:tel:%s"</B> <BR>
	 * result -> tel:13666666666 <BR>
	 * <BR>
	 * int pri = 138;<BR>
	 * write as <B>android:contentDescription="value:pri|format:price:%.2d"</B> <BR>
	 * result -> price:138.00
	 */
	String TAG_FORMAT = "format";
	/**
	 * e.g.<BR>
	 * Write as <B>android:contentDescription="click:onClick"</B> <BR>
	 * <BR>
	 * {@link android.view.View.OnClickListener}<br>
	 * if the method in <B>ListAdapter</B> ({@link ListAdapter}), you should write as<BR>
	 * public void onClick(View v, int position) {<BR>
	 * &nbsp;do Something&nbsp;...<BR>
	 * } <BR>
	 * otherwise<BR>
	 * public void onClick(View v) {<BR>
	 * &nbsp;do Something&nbsp;...<BR>
	 * } <BR>
	 */
	String TAG_CLICK = "click";
	/**
	 * e.g.<BR>
	 * Write as <B>android:contentDescription="longclick:onLongClick"</B> <BR>
	 * <BR>
	 * {@link android.view.View.OnLongClickListener}<BR>
	 * if the method in <B>ListAdapter</B> ({@link ListAdapter}), you should write as<BR>
	 * public boolean onLongClick(View v, int position) {<BR>
	 * &nbsp;do Something&nbsp;...<BR>
	 * } <BR>
	 * otherwise<BR>
	 * public boolean onLongClick(View v) {<BR>
	 * &nbsp;do Something&nbsp;...<BR>
	 * } <BR>
	 */
	String TAG_LONG_CLICK = "longclick";
	/**
	 * e.g.<BR>
	 * write as <B>android:contentDescription="value:this|adapter:com.sample.TestAdapter"</B> <BR>
	 * <BR>
	 * The TestAdapter should extends {@link InjectAdapter},and {@link #TAG_VALUE}(List) is the adapter's
	 * dataSource. TestAdapter must be have a parameterless constructor.
	 */
	String TAG_ADAPTER = "adapter";
	/**
	 * e.g.<BR>
	 * write as <B>android:contentDescription="value:name|ignore"</B> <BR>
	 * <BR>
	 * When occur this tag, the parser will ignore any of it's other tag. <BR>
	 * <B>Default value is FALSE.</B>
	 */
	String TAG_IGNORE = "ignore";
	/**
	 * e.g.<BR>
	 * write as <B>android:contentDescription="skip"</B> <BR>
	 * <BR>
	 * When occur this tag, the parser will skip this view and her child view. <BR>
	 * <B>Default value is FALSE.</B>
	 */
	String TAG_SKIP = "skip";

	String KEY_THIS = "this";
	String KEY_VISIBLE = "visible";
	String KEY_INVISIBLE = "invisible";
	String KEY_GONE = "gone";

	/**
	 * Traversal the value, format, click and longClick from the InjectHolder for each position.<BR>
	 * see more <BR>
	 * {@link #onParseView(View)}
	 * 
	 * @param target The target is an object who contains the click or longClick method. If you doesn't need click or
	 *            longClick method, it can be null.
	 * @param data dataSource
	 * @param holder view holder
	 * @param callBack callBack
	 */
	void onParse(Object target, Object data, InjectHolder holder, ParserCallBack callBack);

	/**
	 * Parse the view contentDescription for each child, and add the each child to InjectHolder.
	 * 
	 * @param view view
	 * @return views who contains contentDescription collection.
	 */
	InjectHolder onParseView(View view);

	/**
	 * attach the value to view.
	 * 
	 * @param v v
	 * @param ph ph
	 * @param value value
	 */
	void onAttachData(View v, ParseHolder ph, Object value);
}
