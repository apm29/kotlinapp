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
package com.luki.x.simple;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import com.luki.x.inject.content.InjectAdapter;

import java.io.Serializable;

/**
 * Simple of InjectAdapter
 * 
 * @author Luki
 */
public class SimpleAdapter<T extends Serializable> extends InjectAdapter<T> {
	protected Context mContext;

	public SimpleAdapter(Context context) {
		super();
		mContext = context;
	}

	protected int getColumnCount() {
		return 1;
	}

	public final boolean onLongClick(AdapterView<?> parent, View view, int position, long id) {
		return onLongClick(position);
	}

	protected boolean onLongClick(final int position) {
		if (enableLongClick()) {
			new AlertDialog.Builder(mContext).setTitle("delete").setMessage("Do you want to delete this item?")
					.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							onDelete(getItem(position));
							dialog.cancel();
						}
					}).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					}).show();
		}
		return true;
	}

	protected boolean enableLongClick() {
		return false;
	}

	protected void onDelete(T item) {

	}

	public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClick(position);
	}

}
