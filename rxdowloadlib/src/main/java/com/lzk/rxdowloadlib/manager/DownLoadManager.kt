package com.lzk.rxdowloadlib.manager

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.lzk.rxdowloadlib.IDownLoad
import com.lzk.rxdowloadlib.service.RxDowloadService
import com.lzk.rxdowloadlib.utils.SingletonHolderNoParms

val iDownLoad = DownLoadManager.getInstance().iDowload

class DownLoadManager {
    companion object : SingletonHolderNoParms<DownLoadManager>(::DownLoadManager)

    var iDowload: IDownLoad? = null
    private lateinit var deathRecipient: IBinder.DeathRecipient
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
        deathRecipient = IBinder.DeathRecipient {
            iDowload?.asBinder()?.unlinkToDeath(deathRecipient, 0)
            iDowload = null
            bindAppWidget()
        }
        bindAppWidget()
    }


    private fun bindAppWidget() {
        application.bindService(Intent(application, RxDowloadService::class.java), object :
            ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                iDowload = IDownLoad.Stub.asInterface(p1)
                iDowload?.asBinder()?.linkToDeath(deathRecipient, 0)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
            }
        }, Context.BIND_AUTO_CREATE)
    }
}