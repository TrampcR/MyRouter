package com.trampcr.myrouter

import android.app.Application
import com.trampcr.gradle.router.runtime.Router

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Router.init()
    }
}

