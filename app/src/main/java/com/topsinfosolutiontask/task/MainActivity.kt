package com.topsinfosolutiontask.task

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.topsinfosolutiontask.task.databinding.ActivityMainBinding
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ClipData.newIntent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder

class MainActivity : AppCompatActivity(),AlertListAdapter.OnClick,AlertListAdapter.OnDelete {

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var prefUtils : PrefUtils
    lateinit var list : MutableList<AlertModel>
    lateinit var alertAdapter : AlertListAdapter
    lateinit var serviceIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)



        init()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add->{
               startActivity(Intent(this,AddTask::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        if (prefUtils.getList("alertList").toMutableList().size>0){
            list = prefUtils.getList("alertList").toMutableList()
        }
        if (list.size>0){
            alertAdapter = AlertListAdapter(this,list,this,this)
            activityMainBinding.rcAlertsList.adapter = alertAdapter
        }
    }

    override fun onPause() {
        super.onPause()

        if (list.size>0) {
            if (Build.VERSION.SDK_INT >= 26) {
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (list.size>0) {
            if (Build.VERSION.SDK_INT >= 26) {
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(){
        activityMainBinding.rcAlertsList.setHasFixedSize(true)
        activityMainBinding.rcAlertsList.layoutManager = LinearLayoutManager(this)
        prefUtils = PrefUtils(this)
        list = mutableListOf()

        serviceIntent = Intent(this, ForeGroundService::class.java)
        serviceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        createNotificationChannel()
        //getNotificationPermission()
    }

    override fun onClick(position: Int, alertModel: AlertModel) {
        var intent = Intent(this,AddTask::class.java)
            .putExtra("Edit",alertModel)
            .putExtra("Position",position)
        startActivity(intent)
    }

    var list1 : MutableList<AlertModel> = mutableListOf()
    override fun onDelete(position: Int) {
        val serviceIntent = Intent(this, ForeGroundService::class.java)
        serviceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        stopService(serviceIntent)


        list.removeAt(position)
        prefUtils.remove("alertList")
        prefUtils.setList("alertList",list1)
        alertAdapter.notifyDataSetChanged()


        if (Build.VERSION.SDK_INT >= 26) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(){
        val name = "Notify Channel"
        val desc = "A description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId,name,importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


