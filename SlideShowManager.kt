package com.shaadi.android.ui.profile.card.v2

import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/***
 * @author Owais Idris
 */


class SlideShowManager{
    private val INTERVAL = 3000L
    private var hostViewPager: ViewPager? = null
    private var running: Boolean=false
    private var isPresentationEnabled=true
    private var mStopAfterCount=0

    fun setupWithViewPager(viewPager: ViewPager, exitOnInteraction: Boolean=false, stopAfter: Int=0) {
        hostViewPager = viewPager
        mStopAfterCount=stopAfter
        hostViewPager?.setScrollDurationFactor(3.5)
        if (exitOnInteraction) {
            detectGestures()
        }
    }

    private fun setUpPresentationCountDown() {
        if (countDownTimer != null) {
            return
        }

        val count=if(mStopAfterCount>0)mStopAfterCount else hostViewPager?.adapter?.count
        val currentPosition=hostViewPager?.currentItem ?: 0

        count?.let {
            val totalTime=INTERVAL*(count-currentPosition-1)
            initializeCountDown(totalTime)
        }
    }

    private fun initializeCountDown(totalTime: Long) {
        countDownTimer = object : CountDownTimer(totalTime, INTERVAL) {
            override fun onFinish() {
                first()
                running = false
                isPresentationEnabled=false
            }

            override fun onTick(millisUntilFinished: Long) {
                nextItem()
            }

        }
    }

    private fun nextItem() {
        hostViewPager?.let {viewPager ->
            viewPager.adapter?.let {adapter->
                val hasNext=viewPager.currentItem+1< (adapter.count)
                if(hasNext){
                    viewPager.setCurrentItem(viewPager.currentItem+1, true)
                }

            }
        }
    }

    private fun first() {
        hostViewPager?.let {viewPager ->
            viewPager.adapter?.let {adapter->
               if (adapter.count >= 1) {
                    viewPager.setCurrentItem(0, false)
                }
            }
        }
    }

    private fun detectGestures(){
        if (hostViewPager == null) {
            return
        }

        val gestureDetector= GestureDetector(hostViewPager?.context, object : GestureDetector.OnGestureListener {
            override fun onShowPress(e: MotionEvent?) {}

            override fun onSingleTapUp(e: MotionEvent?): Boolean = false

            override fun onDown(e: MotionEvent?): Boolean = false

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                Log.d("scroll", "distanceX ${Math.abs(distanceX)}")
                if (Math.abs(distanceX) > 5) {
                    stopPresentation()
                    isPresentationEnabled = false
                    if (!isPresentationEnabled) {
                        hostViewPager?.setScrollDurationFactor(0.5.toDouble())
                    }
                }
                return false
            }

            override fun onLongPress(e: MotionEvent?) {}
        })

        hostViewPager?.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                gestureDetector.onTouchEvent(event)
                return false
            }
        })
    }

    private var countDownTimer: CountDownTimer? = null

    fun startPresentation() {
        if (!isPresentationEnabled) {
            return
        }

        if (!running) {
            setUpPresentationCountDown()
            countDownTimer?.start()
            running=true
        }
    }

    fun stopPresentation() {
        countDownTimer?.cancel()
        running=false
    }

    fun destroy(){
        countDownTimer=null
        running=false
        isPresentationEnabled=true
    }





    interface ICanSlideShow{
        fun startPresentation()
    }
}


/***
 * To manage speed of slide animation of view pager
 */
 fun ViewPager.setScrollDurationFactor(scrollDurationFactor: Double=1.0){
    val scroller = ViewPager::class.java.getDeclaredField("mScroller")
    scroller.isAccessible = true
    val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
    interpolator.isAccessible = true
     val customScroller=object : Scroller(this.context, interpolator.get(null) as Interpolator) {

         var mScrollFactor: Double = 1.0
         init {
             mScrollFactor=scrollDurationFactor
         }

         override fun startScroll(startX: Int,startY: Int, dx: Int, dy: Int, duration: Int) {
             super.startScroll(startX, startY, dx, dy, (duration * mScrollFactor).toInt())
         }
     }
     scroller.set(this, customScroller);

}


