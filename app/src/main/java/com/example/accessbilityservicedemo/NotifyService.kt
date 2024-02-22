package com.example.accessbilityservicedemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * bytedance
 *
 * @author zenglw
 * @date 2024/2/18 17:21
 */
 class NotifyService : NotificationListenerService()  {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val text = sbn?.notification?.extras?.getString("android.text")
        val channelId = sbn?.notification?.channelId
        if (channelId == "my_channel") {
            return
        }
        Log.e(TAG, "onNotificationPosted: ${sbn?.packageName}， content:${text}", )
        // 创建通知渠道
        val channel = NotificationChannel("my_channel",
            "My Channel", NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

// 获取通知管理器
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

// 创建通知
        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentTitle("This is a notification")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

// 发送通知
        notificationManager.notify(1, notification)
        
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.e(TAG, "onNotificationRemoved: ", )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationRemoved(sbn, rankingMap)
        Log.e(TAG, "onNotificationRemoved: 222", )
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        Log.e(TAG, "onNotificationRemoved: 333", )
    }
    
    companion object {
        val TAG = NotifyService::class.java.simpleName
    }
 }