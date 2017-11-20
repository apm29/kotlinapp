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
package com.luki.x.inject.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;

import com.luki.x.XConfig;
import com.luki.x.XParser;
import com.luki.x.XLog;
import com.luki.x.inject.content.ParserCallBack;
import com.luki.x.inject.content.annotation.XCDParser;
import com.luki.x.inject.content.annotation.XCDString;
import com.luki.x.inject.view.annotation.ListenerInject;
import com.luki.x.inject.view.annotation.ListenerInject.ListenerType;
import com.luki.x.inject.view.annotation.Select;
import com.luki.x.inject.view.annotation.ViewInject;
import com.luki.x.util.ReflectUtils;
import com.luki.x.util.WidgetUtils;
import com.luki.x.util.WidgetUtils.ResType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Luki
 */
public class InjectEventControl implements OnClickListener, OnLongClickListener, OnItemClickListener, OnItemSelectedListener,
		OnItemLongClickListener, OnTouchListener, OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener {

	private static final String TAG = "Inject";

	private Object handler;

	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemSelectMethod;
	private String nothingSelectedMethod;
	private String itemLongClickMehtod;
	private String touchMethod;
	private String checkedMethod;

	public InjectEventControl(Object handler) {
		this.handler = handler;
	}

	public InjectEventControl click(String method) {
		this.clickMethod = method;
		return this;
	}

	public InjectEventControl longClick(String method) {
		this.longClickMethod = method;
		return this;
	}

	public InjectEventControl itemLongClick(String method) {
		this.itemLongClickMehtod = method;
		return this;
	}

	public InjectEventControl itemClick(String method) {
		this.itemClickMethod = method;
		return this;
	}

	public InjectEventControl select(String method) {
		this.itemSelectMethod = method;
		return this;
	}

	public InjectEventControl noSelect(String method) {
		this.nothingSelectedMethod = method;
		return this;
	}

	public InjectEventControl touch(String method) {
		this.touchMethod = method;
		return this;
	}

	public InjectEventControl checked(String method) {
		this.checkedMethod = method;
		return this;
	}

	public boolean onLongClick(View v) {
		return invokeMethod(longClickMethod, new Class[] { View.class }, v);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return invokeMethod(itemLongClickMehtod, new Class[] {
				AdapterView.class,
				View.class,
				int.class,
				long.class }, parent, view, position, id);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		invokeMethod(itemSelectMethod, new Class[] {
				AdapterView.class,
				View.class,
				int.class,
				long.class }, parent, view, position, id);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		invokeMethod(nothingSelectedMethod, new Class[] { AdapterView.class }, parent);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		invokeMethod(itemClickMethod, new Class[] {
				AdapterView.class,
				View.class,
				int.class,
				long.class }, parent, view, position, id);
	}

	public void onClick(View v) {
		invokeMethod(clickMethod, new Class[] { View.class }, v);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		invokeMethod(checkedMethod, new Class[] {
				CompoundButton.class,
				boolean.class }, buttonView, isChecked);
	}

	public boolean onTouch(View v, MotionEvent event) {
		return invokeMethod(touchMethod, new Class[] {
				View.class,
				MotionEvent.class }, v, event);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		invokeMethod(checkedMethod, new Class[] {
				RadioGroup.class,
				int.class }, group, checkedId);
	}

	private boolean invokeMethod(String methodName, Class<?>[] clazz, Object... params) {
		Method method;
		try {
			Class<?> cls = handler.getClass();
			method = cls.getDeclaredMethod(methodName, clazz);
			if (method == null) method = cls.getDeclaredMethod(methodName);

			if (method != null) {
				method.setAccessible(true);
				Object obj = method.invoke(handler, params);
				return obj == null ? false : Boolean.valueOf(obj.toString());
			} else
				throw new NoSuchMethodException("no such method:" + methodName + " in " + cls.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void linkedToMethod(Object target, ListenerType type, View view, String methodName) {
		if (methodName != null) {
			InjectEventControl listener = new InjectEventControl(target);
			switch (type) {
			case CLICK:
				view.setOnClickListener(listener.click(methodName));
				break;
			case LONG_CLICK:
				view.setOnLongClickListener(listener.longClick(methodName));
				break;
			case ITEM_CLICK:
				if (view instanceof AbsListView) ((AbsListView) view).setOnItemClickListener(listener.itemClick(methodName));
				break;
			case ITEM_LONG_CLICK:
				if (view instanceof AbsListView) ((AbsListView) view).setOnItemLongClickListener(listener.itemLongClick(methodName));
				break;
			case TOUCH:
				view.setOnTouchListener(listener.touch(methodName));
				break;
			case CHECKED:
				if (view instanceof CompoundButton) {
					((CompoundButton) view).setOnCheckedChangeListener(listener.checked(methodName));
				} else if (view instanceof RadioGroup) {
					((RadioGroup) view).setOnCheckedChangeListener(listener.checked(methodName));
				}
				break;
			case SELECTED:
				if (view instanceof AbsListView) {
					((AbsListView) view).setOnItemSelectedListener(listener.select(methodName));
				}
				break;
			case NO_SELECT:
				if (view instanceof AbsListView) {
					((AbsListView) view).setOnItemSelectedListener(listener.noSelect(methodName));
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * @param target target
	 * @param view view
	 */
	public static void initListener(Object target, View view) {
		Method[] methods = target.getClass().getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				ListenerInject viewListener = method.getAnnotation(ListenerInject.class);
				if (viewListener != null) {
					int[] ids = viewListener.value();
					if (ids != null && ids.length > 0) {
						for (int id : ids) {
							View v = view.findViewById(id);
							if (v != null) {
								ListenerType type = viewListener.type();
								linkedToMethod(target, type, v, method.getName());
							}
						}
					}
				}
			}
		}
	}

	public static void initView(Object activity, View view) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					try {
						field.setAccessible(true);

						int viewId = viewInject.value();
						if (viewId == 0) {
							viewId = WidgetUtils.getRes(XConfig.sContext, field.getName(), ResType.ID);
						}
						field.set(activity, view.findViewById(viewId));
					} catch (Exception ignored) {}
					String method = viewInject.click();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.CLICK, field, method);

					method = viewInject.longClick();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.LONG_CLICK, field, method);

					method = viewInject.itemClick();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.ITEM_CLICK, field, method);

					method = viewInject.itemLongClick();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.ITEM_LONG_CLICK, field, method);

					method = viewInject.touch();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.TOUCH, field, method);

					method = viewInject.checked();
					if (!TextUtils.isEmpty(method)) setViewListener(activity, ListenerType.CHECKED, field, method);

					Select select = viewInject.select();
					if (!TextUtils.isEmpty(select.selected())) {
						setViewListener(activity, ListenerType.SELECTED, field, select.selected());
						setViewListener(activity, ListenerType.NO_SELECT, field, select.noSelected());
					}
				}
			}
		}
	}

	private static void setViewListener(Object target, ListenerType type, Field field, String methodName) {
		try {
			Object obj = field.get(target);
			if (obj instanceof View) {
				linkedToMethod(target, type, (View) obj, methodName);
			}
		} catch (Exception e) {
			XLog.w(TAG, e);
		}
	}

	/**
	 * Use annotation parse
	 * 
	 * @param target target
	 * @param view view
	 */
	public static void parseWithCDAnnotation(Object target, View view) {
		if (target == null) return;
		XCDParser p = target.getClass().getAnnotation(XCDParser.class);
		if (p != null) {
			View v = null;
			Object data = null;
			ParserCallBack listener = null;
			try {
				String viewMethod = p.view();
				if (TextUtils.isEmpty(viewMethod)) {
					if (view != null) {
						v = view;
					} else if(target instanceof Activity){
						v = ((Activity) target).getWindow().getDecorView();
					}
				} else
					v = (View) ReflectUtils.getMethodValue(target, viewMethod);
				data = ReflectUtils.getMethodValue(target, p.dataSource());
				listener = (ParserCallBack) ReflectUtils.getMethodValue(target, p.listener());
			} catch (Exception ignored) {}
			XParser.INSTANCE.parse(target, data, v, listener);
		}
		if (view == null) return;
		Field[] fields = target.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			XCDString cds = field.getAnnotation(XCDString.class);
			if (cds != null) {
				int resId = cds.value();
				if (resId == 0) {
					resId = WidgetUtils.getRes(view.getContext(), field.getName(), ResType.STRING);
				}
				try {
					field.set(target, XParser.INSTANCE.parseView(view).get(view.getContext().getString(resId)));
				} catch (Exception e) {
					XLog.w(TAG, e);
				}
			}
		}
	}
}
