package com.lzk.rxdowload

import android.app.Application
import com.lzk.rxdowloadlib.manager.DownLoadManager

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DownLoadManager.getInstance().init(this)
    }
}