package com.lixiang.car.decodecohttp.decodecohttp.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.Notifications
import com.intellij.openapi.ui.MessageType

object NotifyUtil {
    fun notifyMessage(text: String) {
        val notificationGroup = NotificationGroup("notify", NotificationDisplayType.BALLOON, false);
        val notification = notificationGroup.createNotification(text, MessageType.INFO);
        Notifications.Bus.notify(notification);
    }
}

fun Any.notifyText(text: String) {
    NotifyUtil.notifyMessage(text)
}