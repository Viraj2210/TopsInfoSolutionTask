package com.topsinfosolutiontask.task

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topsinfosolutiontask.task.databinding.AdapterAlertListBinding
import java.sql.Time
import java.util.concurrent.TimeUnit

class AlertListAdapter(var context: Context,
                       var list: List<AlertModel>,
                       var onClick: OnClick,
                        var onDelete: OnDelete) : RecyclerView.Adapter<AlertListAdapter.ViewHolder>() {

    class ViewHolder(var adapterAlertListBinding: AdapterAlertListBinding) :
        RecyclerView.ViewHolder(adapterAlertListBinding.root) {
        fun bind(position: Int,alertModel: AlertModel,
                 onClick: OnClick,onDelete: OnDelete){
            adapterAlertListBinding.tvDesc.text = "Hello"
            object : CountDownTimer(alertModel.millis, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    val hour = ((millisUntilFinished / (1000*60*60)) % 24)
                    val min =  ((millisUntilFinished / (1000*60)) % 60)
                    val sec = (millisUntilFinished / 1000) % 60//millisUntilFinished / 1000 % 60
                    adapterAlertListBinding.appCompatTextView2.text = ""+hour+":"+min+":"+sec
                }

                override fun onFinish() {
                    adapterAlertListBinding.appCompatTextView2.text = "done!"
                }
            }.start()

            adapterAlertListBinding.tvDelete.setOnClickListener {
                onDelete.onDelete(position)
            }

            adapterAlertListBinding.tvEdit.setOnClickListener {
                onClick.onClick(position,alertModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterAlertListBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position,list.get(position), onClick = onClick,onDelete)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface OnClick{
        fun onClick(position: Int,alertModel : AlertModel)
    }

    interface OnDelete{
        fun onDelete(position: Int)
    }
}