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

/**
 * @author Luki
 *
 */
public class MobileTextWatcher extends XTextWatcher {
	int length;

	/**
	 * @param tv
	 */
	public MobileTextWatcher(EditText tv) {
		super(tv);
	}
	
	/* (non-Javadoc)
	 * @see luki.x.text.XTextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		String mobile = wipeOffChar(s.toString(), ' ');
		int length = mobile.length();
		if (length != this.length) {
			this.length = length;
			StringBuilder sb = new StringBuilder(mobile);
			if (sb.length() > 4) {
				sb.insert(3, ' ');
				if (sb.length() > 9) {
					sb.insert(8, ' ');
				}
			}
			mTarget.setText(sb.toString());
			setSelection();
		}
	}

}
