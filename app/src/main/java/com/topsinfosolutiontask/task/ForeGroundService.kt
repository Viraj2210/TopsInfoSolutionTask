package com.topsinfosolutiontask.task

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class ForeGroundService : Service() {

    lateinit var prefUtils: PrefUtils
    var list : MutableList<AlertModel> = mutableListOf()

    private val notificationBuilder: NotificationCompat.Builder? = null
    private var currentNotificationID = 0
    private val notificationTitle: String? = null
    private val notificationText: String? = null

    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "notifications"
    private val description = "Test notification"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()


        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"

            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        prefUtils = PrefUtils(this)
        list = prefUtils.getList("alertList").toMutableList()

        countTimer()

        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId)
    }


    fun countTimer() {
        if (list.size>0){
        for (i in 0 until list.size) {
            object : CountDownTimer(list.get(0).millis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (list.size>0) {
                        list.get(0).millis = millisUntilFinished
                        prefUtils.setList("alertList", list)
                        scheduleNotification(millisUntilFinished)
                    }
                }
//seild
                override fun onFinish() {
                    if (list.size>0) {
                        //prefUtils.remove("alertList")
                        list.removeAt(0)
                        prefUtils.setList("alertList", list)

                    }else{
                        stopSelf()
                    }
                }
            }.start()
        }
        }else{
            stopSelf()
        }

        if (list.size>0) {
            Looper.myLooper()
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartService = Intent(this, this.javaClass)
        restartService.setPackage(packageName)


        var restartServiceApi: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= 26) {
            restartServiceApi = PendingIntent.getForegroundService(
                applicationContext,
                1,
                restartService,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000 * 60 * 5] =
            restartServiceApi
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    fun scheduleNotification(millis : Long){
        val intent = Intent(applicationContext,BroadCastReceiver::class.java)
        val title = "Alert Manager"
        val message = "One of the task has been completed"
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            500,
            pendingIntent
        )
    }
}
