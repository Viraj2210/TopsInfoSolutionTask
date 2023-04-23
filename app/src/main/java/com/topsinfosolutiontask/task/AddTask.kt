package com.topsinfosolutiontask.task

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.topsinfosolutiontask.task.databinding.ActivityAddTaskBinding
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min


class AddTask : AppCompatActivity() {
    lateinit var activityAddTaskBinding: ActivityAddTaskBinding
    lateinit var list : MutableList<AlertModel>
    var millis : Long =0L
    var isEdit = false
    var position = 0
    lateinit var prefUtils : PrefUtils
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddTaskBinding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(activityAddTaskBinding.root)

        list = mutableListOf()

        prefUtils = PrefUtils(this)

        if (prefUtils.getList("alertList")!=null){
            list = prefUtils.getList("alertList").toMutableList()
        }


        activityAddTaskBinding.etTime.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val second : Int = mcurrentTime.get(Calendar.SECOND)
            val timePickerDialog = TimePickerDialog(this,
                { view, hourOfDay, minute ->
                    val f: NumberFormat = DecimalFormat("00")
                    activityAddTaskBinding.etTime.setText(f.format(hourOfDay) + ":" + f.format(minute))
                    val time1str = ""+hourOfDay+":"+minute

                    val simpleDateFormat = SimpleDateFormat("hh:mm")
                    val currentTimeStr = simpleDateFormat.format(Date())

                    val choosenTime = simpleDateFormat.parse(time1str)
                    val currTime = simpleDateFormat.parse(currentTimeStr)

                    millis = choosenTime.time - currTime.time

                }, hour, minute, true
            )
            timePickerDialog.setTitle("Select Time")
            timePickerDialog.show()

        }

        activityAddTaskBinding.tvSubmit.setOnClickListener {
           /* if (isEdit) {
                list.get(position).title = activityAddTaskBinding.etTitle.text.toString()
                list.get(position).desc = activityAddTaskBinding.etDesc.text.toString()
                list.get(position).millis = millis

            }else{*/

                var alertModel = AlertModel(
                    activityAddTaskBinding.etTitle.text.toString(),
                    activityAddTaskBinding.etDesc.text.toString(),
                    millis
                )
                list.add(alertModel)
                Log.i("Size",list.size.toString())
                prefUtils.setList("alertList",list)
                finish()
            }
           //
       // }



        var alertModel: AlertModel ?=null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               alertModel= intent.getSerializableExtra("Edit",AlertModel::class.java)
            } else {
                alertModel = intent.getSerializableExtra("Edit") as AlertModel?
            }
            position = intent.getIntExtra("Position",0)
           // if (position>0) {
            if (alertModel!=null){
               // var alertModel = intent.getSerializableExtra("Edit",AlertModel::class.java)
                val hour = ((alertModel.millis / (1000 * 60 * 60)) % 24)
                val min = ((alertModel.millis / (1000 * 60)) % 60)
                val sec = (alertModel.millis / 1000) % 60//millisUntilFinished / 1000 % 60
                activityAddTaskBinding.etTime.setText("" + hour + ":" + min + ":" + sec)
                activityAddTaskBinding.etDesc.setText(alertModel.desc)
                activityAddTaskBinding.etTitle.setText(alertModel.title)
                isEdit = true

        }

    }
}