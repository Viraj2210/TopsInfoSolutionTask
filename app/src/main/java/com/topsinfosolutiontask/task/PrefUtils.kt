package com.topsinfosolutiontask.task

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@SuppressLint("CommitPrefEdits")
class PrefUtils(val context: Context) {

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var _context: Context? = null

    var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = context.applicationInfo.loadLabel(context.packageManager).toString()

    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    init {
        _context = context
        pref = _context!!.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
        editor = pref!!.edit()

    }

    // shared pref mode


    fun setBoolean(PREF_NAME: String?, `val`: Boolean?) {
        editor!!.putBoolean(PREF_NAME, `val`!!)
        editor!!.commit()
    }

    fun setString(PREF_NAME: String?, VAL: String?) {
        editor!!.putString(PREF_NAME, VAL)
        editor!!.commit()
    }

    fun setInt(PREF_NAME: String?, VAL: Int) {
        editor!!.putInt(PREF_NAME, VAL)
        editor!!.commit()
    }

    fun getBoolean(PREF_NAME: String?): Boolean {
        return pref!!.getBoolean(PREF_NAME, false)
    }

    fun remove(PREF_NAME: String?) {
        if (pref!!.contains(PREF_NAME)) {
            editor!!.remove(PREF_NAME)
            editor!!.commit()
        }
    }

    fun getString(PREF_NAME: String): String? {
        if (pref!!.contains(PREF_NAME)) {
            return pref!!.getString(PREF_NAME, null)
        } else {
            return null
        }
    }

    fun getInt(key: String?): Int {
        return pref!!.getInt(key, 0)
    }

//    fun isUserLoggedIn() = pref?.getBoolean(Constants.PREF_IS_USER_LOGGED_IN, false)

/*
    fun getUserData(): UserRegisterationModel.User? {
        if (isUserLoggedIn()!!){
            val user = pref?.getString(Constants.PREF_USER,null)
            val userModel = Gson().fromJson(user, UserRegisterationModel.User::class.java)
            return userModel
        }else{
            return null
        }

    }*/

    fun logout() = pref?.edit()?.clear()?.apply()

    fun <T> setList(key: String?, list: List<T>?) {
        val gson = Gson()
        val json: String = gson.toJson(list)
       // set(key, json)
        editor!!.putString(key, json)
        editor!!.apply()
    }

    fun getList(KEY_PREFS: String?) : List<AlertModel> {
        var list : MutableList<AlertModel> = mutableListOf()
        var gson = Gson()
        val json: String? = pref?.getString(KEY_PREFS, null)
        val type: Type = object : TypeToken<ArrayList<AlertModel?>?>() {}.type
        if (json!=null) {
            list = gson.fromJson(json, type)
            return list
        }else{
            return list
        }
    }
}