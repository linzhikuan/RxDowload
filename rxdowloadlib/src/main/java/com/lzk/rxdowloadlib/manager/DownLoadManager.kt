package com.lzk.rxdowloadlib.manager

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.lzk.rxdowloadlib.IDownLoad
import com.lzk.rxdowloadlib.IDownLoadListner
import com.lzk.rxdowloadlib.bean.DowloadBuild
import com.lzk.rxdowloadlib.bean.DownLoadDb
import com.lzk.rxdowloadlib.service.RxDowloadService
import com.lzk.rxdowloadlib.utils.SingletonHolderNoParms


class DownLoadManager {
    companion object : SingletonHolderNoParms<DownLoadManager>(::DownLoadManager)

    private var iDowload: IDownLoad? = null
    private lateinit var deathRecipient: IBinder.DeathRecipient
    private lateinit var application: Application

    private var callBack: DownLoadCallBack? = null
    private var listner: IDownLoadListner? = object : IDownLoadListner.Stub() {
        override fun update(db: DownLoadDb?) {
            callBack?.update(db)
        }

        override fun error(error: String?) {
            callBack?.error(error)
        }
    }

    fun init(application: Application) {
        this.application = application
        deathRecipient = IBinder.DeathRecipient {
            iDowload?.asBinder()?.unlinkToDeath(deathRecipient, 0)
            iDowload = null
            bindAppWidget()
        }
        bindAppWidget()
    }


    fun startDownLoad(downloadbuild: DowloadBuild) {
        iDowload?.startDownLoad(downloadbuild)
    }

    fun stopDownLoad(url: String) {
        iDowload?.stopDowload(url)
    }

    fun addCalBack(callBack: DownLoadCallBack?) {
        this.callBack = callBack
        iDowload?.queryProgress(listner)
    }

    fun removeCallBack() {
        callBack = null
        iDowload?.queryProgress(null)
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

    interface DownLoadCallBack {
        fun update(db: DownLoadDb?)
        fun error(error: String?)
    }
}