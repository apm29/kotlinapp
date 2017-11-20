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

import com.luki.x.XConfig;

import java.util.HashMap;

/**
 * View Holder <BR>
 * An <B>empty</B> class extends of HashMap&lt;String, View&gt;<BR>
 * key equal the view's content description.<BR>
 * value equal the view.
 * 
 * @author Luki
 */
public class InjectHolder extends HashMap<String, View> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8618468737567183991L;
	public int position = -1;

	public <T> T findViewByString(int resId) {
		return findViewByString(XConfig.sContext.getString(resId));
	}

	@SuppressWarnings("unchecked")
	public <T> T findViewByString(String string) {
		return (T) get(string);
	}

}