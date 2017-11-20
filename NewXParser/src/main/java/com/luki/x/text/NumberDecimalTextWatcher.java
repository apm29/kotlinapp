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
package com.luki.x.text;

import android.text.Editable;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * @author Luki
 */
public class NumberDecimalTextWatcher extends XTextWatcher {
	/**
	 * @param tv
	 */
	public NumberDecimalTextWatcher(EditText tv) {
		super(tv);
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		String value = s.toString().trim();
		if (!Pattern.compile("^\\d*(\\.\\d{0,2})?$").matcher(value).find()) {
			value = value.substring(0, value.length() - 1);
			mTarget.setText(value);
			setSelection();
			return;
		} else if (value.length() == 0) {
			value = "0";
		} else if (value.startsWith(".")) {
			value = "0" + value;
			mTarget.setText(value);
			setSelection();
			return;
		}
	}

}
