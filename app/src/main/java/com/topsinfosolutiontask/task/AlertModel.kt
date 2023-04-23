package com.topsinfosolutiontask.task

import android.os.Parcel
import android.os.Parcelable

data class AlertModel(
    var title: String?,
    var desc: String,
    var millis: Long
) : java.io.Serializable

