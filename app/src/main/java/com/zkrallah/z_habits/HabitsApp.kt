package com.zkrallah.z_habits

import android.app.Application

class HabitsApp : Application() {

    companion object {
        lateinit var ctx: Application
    }

    override fun onCreate() {
        ctx = this
        super.onCreate()
    }
}