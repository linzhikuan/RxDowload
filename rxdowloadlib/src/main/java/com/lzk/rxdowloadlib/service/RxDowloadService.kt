package com.lzk.rxdowloadlib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.lzk.rxdowloadlib.impl.DownLoadImpl
import com.lzk.rxdowloadlib.impl.DownLoadImpl2

class RxDowloadService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return DownLoadImpl(applicationContext)
    }
}