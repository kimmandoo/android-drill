package com.kimmandoo.notificationlistenerservice

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

private const val TAG = "MyNotificationListenerS"

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 새 알림이 게시될 때 호출됨
        val packageName: String = sbn.packageName ?: "Null"
        val extras = sbn.notification?.extras

        val extraTitle: String = extras?.get(Notification.EXTRA_TITLE).toString()
        val extraText: String = extras?.get(Notification.EXTRA_TEXT).toString()
        val extraBigText: String = extras?.get(Notification.EXTRA_BIG_TEXT).toString()
        val extraInfoText: String = extras?.get(Notification.EXTRA_INFO_TEXT).toString()
        val extraSubText: String = extras?.get(Notification.EXTRA_SUB_TEXT).toString()
        val extraSummaryText: String = extras?.get(Notification.EXTRA_SUMMARY_TEXT).toString()

        Log.d(
            TAG, "알림수신:\n" +
                    "PackageName: $packageName" +
                    "Title: $extraTitle\n" +
                    "Text: $extraText\n" +
                    "BigText: $extraBigText\n" +
                    "InfoText: $extraInfoText\n" +
                    "SubText: $extraSubText\n" +
                    "SummaryText: $extraSummaryText\n"
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // 알림이 제거될 때 호출됨
        val packageName: String = sbn.packageName ?: "Null"
        val extras = sbn.notification?.extras

        val extraTitle: String = extras?.get(Notification.EXTRA_TITLE).toString()
        val extraText: String = extras?.get(Notification.EXTRA_TEXT).toString()
        val extraBigText: String = extras?.get(Notification.EXTRA_BIG_TEXT).toString()
        val extraInfoText: String = extras?.get(Notification.EXTRA_INFO_TEXT).toString()
        val extraSubText: String = extras?.get(Notification.EXTRA_SUB_TEXT).toString()
        val extraSummaryText: String = extras?.get(Notification.EXTRA_SUMMARY_TEXT).toString()

        Log.d(
            TAG, "알림제거:\n" +
                    "PackageName: $packageName" +
                    "Title: $extraTitle\n" +
                    "Text: $extraText\n" +
                    "BigText: $extraBigText\n" +
                    "InfoText: $extraInfoText\n" +
                    "SubText: $extraSubText\n" +
                    "SummaryText: $extraSummaryText\n"
        )
    }
}