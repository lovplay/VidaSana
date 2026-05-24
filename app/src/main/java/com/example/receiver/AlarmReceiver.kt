package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.AlarmAndNotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val title = intent.getStringExtra("TITLE") ?: "Recordatorio de Pulsefy"
        val message = intent.getStringExtra("MESSAGE") ?: "Tienes tareas y rutinas por cumplir."
        val alarmId = intent.getIntExtra("ALARM_ID", 101)

        AlarmAndNotificationHelper.sendNotification(context, title, message, alarmId)
    }
}
