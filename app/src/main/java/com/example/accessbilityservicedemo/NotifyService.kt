package com.example.accessbilityservicedemo

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * bytedance
 *
 * @author zenglw
 * @date 2024/2/18 17:21
 */
 class NotifyService : NotificationListenerService()  {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.e(TAG, "onNotificationPosted: ${sbn?.packageName}， content:${sbn?.notification?.extras?.getString("android.text")}", )
        
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