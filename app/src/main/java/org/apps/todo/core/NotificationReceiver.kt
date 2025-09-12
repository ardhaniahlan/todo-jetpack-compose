package org.apps.todo.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.apps.todo.core.Constants.showNotification

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "To-Do Reminder"
        val desc = intent.getStringExtra("desc")
        showNotification(context, title, desc)
    }
}
