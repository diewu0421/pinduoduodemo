package com.example.accessbilityservicedemo

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding

/**
 * bytedance
 * @author zenglw
 * @date   2024/2/17 14:24
 */
class MyAccessibilityService : AccessibilityService() {

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 监控事件，检测新消息的UI变化
        // 根据应用的UI结构，检测新消息的通知、文本等
        Log.e(TAG, "onAccessibilityEvent:eventType: ${AccessibilityEvent.eventTypeToString(event.eventType)},${event.source?.text}")

        if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

//            Log.e(TAG, "onAccessibilityEvent: findById ${rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.kye.kuasheng:id/et_account")}", )
//            Log.e(TAG, "onAccessibilityEvent: findByText ${rootInActiveWindow.findAccessibilityNodeInfosByText("请输入您的")}", )

            Log.e(TAG, "onAccessibilityEvent: ${rootInActiveWindow.findAccessibilityNodeInfosByText("下一步")}", )

//            if (rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)?.text?.contains("您的工号") == true) {
//                rootInActiveWindow.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)?.input("608511")
//            }
        }
    }


    private fun findNodes(node: AccessibilityNodeInfo?, filterCondition: (AccessibilityNodeInfo) -> Boolean) {
        if (node == null) {
            return
        }

        // 处理当前节点，例如输出节点的文本内容
        if (filterCondition(node)) {
            return
        }

        // 递归遍历子节点
        for (i in 0 until node.childCount) {
            findNodes(node.getChild(i), filterCondition)
        }
    }

    override fun onInterrupt() {
        // 中断服务
        showMsg("onInterrupt")
    }

    override fun onServiceConnected() {
        // 在服务连接时执行一些初始化操作
        Log.e(TAG, "onServiceConnected: ${serviceInfo.flags}")
//        serviceInfo.flags = serviceInfo.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        Log.e(TAG, "onServiceConnected after: ${serviceInfo.flags}")
        acquireWakeLock()

    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var windowManager: WindowManager? = null

    @SuppressLint("InvalidWakeLockTag")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            // 获取PowerManager实例
//            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
//
//            // 创建WakeLock
//            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyWakeLock")
//
//            // 获取屏幕常量，确保屏幕保持唤醒状态
//            wakeLock?.acquire()

            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.CENTER
            windowManager?.addView(TextView(this).apply {
                text = ""
                isClickable = false
                setBackgroundColor(Color.TRANSPARENT)
                requestFocus()

                setOnClickListener {
                    Log.e(TAG, "acquireWakeLock: 1111", )
//                    showMsg("hhhh")
                    text = System.currentTimeMillis().toString()
                    startActivity(Intent(this@MyAccessibilityService, SettingActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }, params)
            showMsg("connect")
            // 降低屏幕亮度
//            decreaseBrightness()

        }
    }

    private var originalBrightness: Int = 0
    private lateinit var contentResolver: ContentResolver
    private fun decreaseBrightness() {
        // 获取当前屏幕亮度
        contentResolver = applicationContext.contentResolver
        originalBrightness =
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)
        Log.e(TAG, "decreaseBrightness: origin:$originalBrightness")

        // 设置降低后的屏幕亮度（例如：降低到50%）
        setScreenBrightness(0.5f)
    }

    private fun restoreBrightness() {
        // 恢复原始屏幕亮度
        setScreenBrightness(1.0f)
    }

    private fun setScreenBrightness(value: Float) {
        val brightnessValue = (value * 255).toInt()

        Log.e(TAG, "setScreenBrightness: brightnessValue=$brightnessValue")
        // 修改系统亮度设置
        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)


    }

    override fun onUnbind(intent: Intent?): Boolean {
        releaseWakeLock()
        showMsg("unbind")
        return super.onUnbind(intent)
    }

    private fun showMsg(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "showMsg: $msg", )
    }

    private fun releaseWakeLock() {
        // 释放WakeLock，允许屏幕关闭
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
                wakeLock = null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        Log.e(TAG, "onGesture: ${gestureEvent.displayId}")
        return super.onGesture(gestureEvent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ", )
    }



    companion object {
        val TAG = MyAccessibilityService::class.java.simpleName
    }
}

// 点击
fun AccessibilityNodeInfo.click() = performAction(AccessibilityNodeInfo.ACTION_CLICK)

// 长按
fun AccessibilityNodeInfo.longClick() =
    performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)

// 向下滑动一下
fun AccessibilityNodeInfo.scrollForward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)

// 向上滑动一下
fun AccessibilityNodeInfo.scrollBackward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

// 填充文本
fun AccessibilityNodeInfo.input(content: String) = performAction(
    AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
    }
)