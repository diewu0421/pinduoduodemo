package com.example.accessbilityservicedemo

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi

/**
 * bytedance
 * @author zenglw
 * @date   2024/2/17 14:24
 */
class MyAccessibilityService : AccessibilityService(){

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 监控事件，检测新消息的UI变化
        // 根据应用的UI结构，检测新消息的通知、文本等
        Log.e(TAG, "onAccessibilityEvent:eventType: ${event.eventType},${event.source?.text}")

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                // 获取通知内容
            val notification: Any? = event.parcelableData
//            val title: String = notification.getTitle()
//            val text: String = notification.getText()

            // 进行操作
//            Log.d("TAG", "收到通知：$title - $text")
            Log.e(TAG, "onAccessibilityEvent: $notification")
        }
    }

    override fun onInterrupt() {
        // 中断服务
    }

    override fun onServiceConnected() {
        // 在服务连接时执行一些初始化操作

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        Log.e(TAG, "onGesture: ${gestureEvent.displayId}", )
        return super.onGesture(gestureEvent)
    }
    
    companion object {
        val TAG = MyAccessibilityService::class.java.simpleName
    }
}