package com.apm29.kotlinapp.view.drag

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.apm29.kotlinapp.view.pager.DragLayout

/**
 * Created by dingzhu on 2017/12/29.
 */
class DragHelperLayout(context: Context, attributes: AttributeSet) : ViewGroup(context, attributes) {

    lateinit var dragHelper: ViewDragHelper
    lateinit var gestureDetector: GestureDetectorCompat

    init {
        dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return true
            }
        })
        gestureDetector = GestureDetectorCompat(this.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent,distanceX: Float, distanceY: Float): Boolean {
                return Math.abs(distanceY) > Math.abs(distanceX)
            }
        })
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    var nextPageListener: ShowNextPageNotifier? = null
    var prePageListener: ShowPrePageNotifier? = null
    fun setNextPageListener(nextPageListener: ShowNextPageNotifier): DragHelperLayout {
        this.nextPageListener = nextPageListener
        return this
    }

    fun setPrePageListener(prePageListener: ShowPrePageNotifier): DragHelperLayout {
        this.prePageListener = prePageListener
        return this
    }

    interface ShowNextPageNotifier {
        fun onDragNext()
    }

    interface ShowPrePageNotifier {
        fun onDragPre()
    }

    lateinit var topView: View
    lateinit var bottomView: View
    var topHeight: Int = 0
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        topView = getChildAt(0)
        bottomView = getChildAt(1)

        if (topView.top == 0) {
            topView.layout(l, 0, r, b - t)
            bottomView.layout(l, 0, r, b - t)
            topHeight = topView.measuredHeight
            bottomView.offsetTopAndBottom(topHeight)
        } else {
            topView.layout(l, topView.top, r, topView.bottom)
            bottomView.layout(l, topView.bottom, r, bottomView.bottom)
        }
    }
}