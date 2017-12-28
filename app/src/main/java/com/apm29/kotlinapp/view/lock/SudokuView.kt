package com.apm29.kotlinapp.view.lock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.apm29.kotlinapp.R
import java.io.Serializable

/**
 * Created by apm29 on 2017/12/26.
 */
/**
 * 数独解锁
 */
class SudokuView : View {
    private val TAG = "SudokuView"


    // spacing between two points
    private var spacing = 0

    private var startX = 0
    private var startY = 0

    // current x-Location when move
    private var moveX = 0

    // current y-Location when move
    private var moveY = 0

    // check whether the any point is selected
    private var hasSelected = false

    // outer paint for the line
    //	private Paint outerPaint = null;

    // inner paint for the line
    private var innerPaint: Paint? = null


    // the String of lock
    private val lockString = StringBuilder()

    // default bitmap for the point
    private val defaultBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_lock_normal)

    // selected bitmap for the point
    private val selectedBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_lock_selected)
    // selected bitmap for the point
    private val errorBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_lock_error)

    // the radius for the default bitmap
    private val defaultRadius = defaultBitmap.width / 2

    // the radius for the selected bitmap
    private val selectedRadius = selectedBitmap.width / 2

    // start point for touching;
    // this will only appears when the ACTION_DOWN in the area of nine points.
    private var startPoint: PointInfo? = null

    // nine point for this SudokuView
    private val ninePoints = arrayOfNulls<PointInfo>(9)

    // 记录所选取的点
    //	private List<PointInfo> selectedPoints = new ArrayList<PointInfo>();

    private var mOnLockFinishListener: OnLockFinishListener? = null

    private var isError: Boolean = false

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onMeasure(wMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(wMeasureSpec, wMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = width
        spacing = (width - selectedRadius * 2 * 3) / 4
        initPoints(true)
        initPaint()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        if (isError) {
            innerPaint?.color = -0x7773d8d6
        } else {
            innerPaint?.color = -0x77daa672
        }
        if (startX > 0 && startY > 0 && moveX > 0 && moveY > 0) {
            canvas.drawLine(startX.toFloat(), startY.toFloat(), moveX.toFloat(), moveY.toFloat(), innerPaint)
        }
        drawNinePoint(canvas)
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (hasSelected) {// if the points has been selected previously, clear all
                    initPoints(true)
                    startPoint = null
                    hasSelected = false
                    lockString.delete(0, lockString.length)
                    invalidate()
                    return false// must return false to end touch
                }

                val downx = event.x.toInt()
                val downy = event.y.toInt()
                for (pointInfo in ninePoints) {
                    if (pointInfo?.isInPoint(downx, downy)==true) {
                        pointInfo.isSelected = true
                        startPoint = pointInfo
                        startX = pointInfo.centerX
                        startY = pointInfo.centerY
                        lockString.append(pointInfo.number)
                        hasSelected = true
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                Log.v(TAG, "ACTION_MOVE")
                moveX = event.x.toInt()
                moveY = event.y.toInt()
                for (pointInfo in ninePoints) {
                    if (pointInfo != null && ! pointInfo.isSelected && pointInfo.isInPoint(moveX, moveY)) {
                        startX = pointInfo.centerX
                        startY = pointInfo.centerY
                        val length = lockString.length
                        if (length > 0) {
                            val previousNumber = lockString[length - 1].toInt() - 48
                            ninePoints[previousNumber]?.nextNumber = pointInfo.number
                        } else {
                            startPoint = pointInfo
                        }
                        pointInfo.isSelected = true
                        hasSelected = true
                        lockString.append(pointInfo.number)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (lockString.isNotEmpty()) {
                    mOnLockFinishListener?.finish(lockString)
                }

                Log.e(TAG, "ACTION_UP")
            }
        }
        invalidate()
        return true
    }

    /**
     * draw the nine points for this SudokuView including default bitmap and selected bitmap
     *
     * @param canvas canvas
     */
    private fun drawNinePoint(canvas: Canvas) {
        if ( startPoint!=null) {
            drawLine(canvas, startPoint)
        }
        for (mPointInfo in ninePoints) {
            // Firstly, if the point is selected, draw the selected bitmap on this point
            if (mPointInfo?.isSelected==true) {
                if (!isError) {
                    canvas.drawBitmap(selectedBitmap, mPointInfo.selectedX.toFloat(), mPointInfo.selectedY.toFloat(), null)
                } else {
                    canvas.drawBitmap(errorBitmap, mPointInfo.selectedX.toFloat(), mPointInfo.selectedY.toFloat(), null)
                }
            } else {
                // Then, draw the default bitmap on this point
                canvas.drawBitmap(defaultBitmap, mPointInfo?.defaultX?.toFloat()?:0F, mPointInfo?.defaultY?.toFloat()?:0F, null)
            }
        }
        isError = false
    }

    /**
     * draw a line between two points
     *
     * @param canvas     canvas
     * @param mPointInfo pointInfo
     */
    private fun drawLine(canvas: Canvas, mPointInfo: PointInfo?) {
        var mPointInfo = mPointInfo
        while (mPointInfo!=null&&mPointInfo.isHasNextPoint) {
            val index = mPointInfo.nextNumber
            canvas.drawLine(mPointInfo.centerX.toFloat(), mPointInfo.centerY.toFloat(), ninePoints[index]?.centerX?.toFloat()?:0F, ninePoints[index]?.centerY?.toFloat()?:0F, innerPaint)
            mPointInfo = ninePoints[index]
        }
    }

    /**
     * initialize the paint
     */
    private fun initPaint() {


        innerPaint = Paint()
        innerPaint?.color = -0xcc33cd
        innerPaint?.strokeWidth = 12f
        innerPaint?.isAntiAlias = true
        innerPaint?.strokeCap = Paint.Cap.ROUND

        //		errorPaint = new Paint();
        //		errorPaint.setColor(Color.RED);
        //		errorPaint.setStrokeWidth(defaultBitmap.getWidth() / 6);
        //		errorPaint.setAntiAlias(true);
        //		errorPaint.setStrokeCap(Cap.ROUND);
    }

    /**
     * initialize basic nine points
     */
    private fun initPoints(isError: Boolean) {
        if (isError && ninePoints[0] != null) {
            return
        }
        var selectedX = spacing
        var selectedY = spacing
        var defaultX = spacing + selectedRadius - defaultRadius
        var defaultY = spacing + selectedRadius - defaultRadius
        var mPointInfo: PointInfo
        for (index in 0..8) {
            if (index == 3 || index == 6) {
                selectedX = spacing
                selectedY += selectedRadius * 2 + spacing
                defaultX = spacing + selectedRadius - defaultRadius
                defaultY += selectedRadius * 2 + spacing
            } else {
                if (index != 0) {
                    selectedX += selectedRadius * 2 + spacing
                    // selectedY = selectedY;
                    defaultX += selectedRadius * 2 + spacing
                    // defaultY = defaultY;
                }
            }

            mPointInfo = PointInfo(defaultX, defaultY, selectedX, selectedY, defaultRadius, selectedRadius, index, index)
            mPointInfo.isSelected = false
            mPointInfo.number = index
            mPointInfo.nextNumber = index
            ninePoints[index] = mPointInfo

        }
    }

    /**
     * Listener when the locking is finishing
     */
    interface OnLockFinishListener {
        fun finish(lockString: StringBuilder)
    }

    /**
     * set the Listener for the callback
     */
    fun setOnLockFinishListener(mOnLockFinishListener: OnLockFinishListener) {
        this.mOnLockFinishListener = mOnLockFinishListener
    }

    /**
     * 取消所有效果
     */
    fun mInvalidate() {
        startX = 0
        startY = 0
        moveX = 0
        moveY = 0
        startPoint = null
        hasSelected = false
        lockString.delete(0, lockString.length)

        initPoints(false)
        invalidate()
    }

    fun error() {
        isError = true
        invalidate()
        postDelayed({ mInvalidate() }, 1000)
    }

    //	/**
    //	 * 赋值手势密码
    //	 *
    //	 */
    //	public void setLockString(String lockStr) {
    //		rememberLock = lockStr;
    //	}
    inner class PointInfo(//default x-Location
            var defaultX: Int, //default y-Location
            var defaultY: Int, //selected x-Location
            var selectedX: Int, //selected y-Location
            var selectedY: Int, //the radius for the default bitmap
            var defaultRadius: Int, //the radius for the selected bitmap
            var selectedRadius: Int, //the number about this point
            var number: Int, nextNumber: Int) : Serializable {

        //the next point connection with this point
        var nextNumber: Int = 0

        //whether the point is selected
        var isSelected: Boolean = false

        /**
         * whether current point the next connecting point
         *
         * @return boolean
         */
        //if equals, return false, else return true
        val isHasNextPoint: Boolean
            get() = number != nextNumber

        /**
         * get the center x-Location of this point
         *
         * @return Integer
         */
        val centerX: Int
            get() = selectedX + selectedRadius

        /**
         * get the center y-Location of this point
         *
         * @return center y
         */
        val centerY: Int
            get() = selectedY + selectedRadius

        init {
            this.nextNumber = number//
        }

        /*
		 * whether current point which touch is in the area of this point
		 * @return boolean
		 */
        fun isInPoint(x: Int, y: Int): Boolean {
            return selectedX <= x && x <= selectedX + 2 * selectedRadius && selectedY <= y && y <= selectedY + 2 * selectedRadius
        }

        override fun toString(): String {
            return "PointInfo [defaultX=$defaultX, defaultY=$defaultY, selectedX=$selectedX, selectedY=$selectedY, defaultRadius=$defaultRadius, selectedRadius=$selectedRadius, number=$number, nextNumber=$nextNumber, isSelected=$isSelected]"
        }


    }


}