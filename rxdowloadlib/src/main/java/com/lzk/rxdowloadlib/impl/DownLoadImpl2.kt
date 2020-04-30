package com.lzk.rxdowloadlib.impl

import android.content.Context
import android.os.Parcel
import android.util.Log
import com.lzk.rxdowloadlib.IDownLoad
import com.lzk.rxdowloadlib.IDownLoadListner
import com.lzk.rxdowloadlib.bean.DowloadBuild
import com.lzk.rxdowloadlib.bean.DownloadBean
import com.lzk.rxdowloadlib.net.RetriofitApi
import com.lzk.rxdowloadlib.net.createDowload
import com.lzk.rxdowloadlib.net.switchThread
import com.lzk.rxdowloadlib.room.DbDataBase
import com.lzk.rxdowloadlib.room.DownLoadDb
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class DownLoadImpl2(var context: Context) : IDownLoad.Stub() {
    private val tag: String = "RxDowloadService>>>"
    private var subMap = HashMap<String?, DownLoadDb>()
    private var iDownLoadListner: IDownLoadListner? = null
    private var downLoadDao = DbDataBase.getInstance(context)?.downLoadDao()
    private var queryDisposable: Disposable? = null
    private var cancleDisposable: Disposable? = null
    private var cancleAllDispose: Disposable? = null
    private fun myLog(msg: String) {
        Log.d(tag, msg)
    }

    override fun stopDowload(url: String?) {
        subMap[url]?.disposable?.dispose()
        subMap[url]?.disposable = null
        subMap[url]?.let {
            updateProgress(it, false)
        }

    }

    override fun cancleDowload(url: String?) {
        cancleDisposable = Flowable.just(url).observeOn(AndroidSchedulers.mainThread()).map {
            stopDowload(url)
            subMap.remove(url)
            it
        }.observeOn(Schedulers.io()).map {
            downLoadDao?.delete(it)
            true
        }.subscribe({
            cancleAllDispose?.dispose()
        }, {
            iDownLoadListner?.error("取消失败__" + it.message)
        })
    }

    override fun cancleAllDowload() {

        cancleAllDispose = Flowable.fromCallable {
            downLoadDao?.delete()
            true
        }.observeOn(AndroidSchedulers.mainThread()).map {
            subMap.forEach {
                stopDowload(it.key)
            }
            subMap.clear()
            it
        }.compose(switchThread())
            .subscribe({
            }, {
                iDownLoadListner?.error("取消失败__" + it.message)
            })
    }

    override fun stopAllDowload() {
        subMap.forEach {
            stopDowload(it.key)
        }
    }

    override fun queryProgress(iDownLoadListner: IDownLoadListner?) {
        this.iDownLoadListner = iDownLoadListner
        queryDisposable?.dispose()
        queryDisposable = Flowable.fromCallable {
            downLoadDao?.getAllDownLoadDb()
        }.compose(switchThread()).subscribe({ list ->
            queryDisposable?.dispose()
            list?.forEach { db ->

                var file: File
                if (db.savePath.isEmpty()) {
                    file = File(context.externalCacheDir, db.fileName)
                } else {
                    file =
                        File(
                            db.savePath,
                            db.fileName
                        )
                }
                if (file.exists()) {
                    db.readLength = file.length()
                    iDownLoadListner?.update(
                        db.readLength,
                        db.totalLength,
                        db.downLoadUrl,
                        false
                    )
                }


            }
        }, {

        })
    }

    override fun startDownLoad(build: DowloadBuild?) {
        build?.let { it ->
            it.dowloadBean.forEach { bean ->
                bean.dowloadUrl?.let { url ->
                    if (subMap[url] == null) {
                        val db = DownLoadDb()
                        db.downLoadUrl = bean.dowloadUrl ?: ""
                        db.savePath = bean.savePath ?: ""
                        db.fileName = bean.fileName ?: ""
                        subMap[url] = db
                    }
                }
            }
        }
        subMap.forEach {
            myLog("开始下载__" + it.value.downLoadUrl + "__" + (it.value.disposable == null) + "__" + (it.value.disposable?.isDisposed))
            if (it.value.disposable == null || it.value.disposable!!.isDisposed) {
                it.value.disposable = download(it.value)
            }
        }
    }

    private fun download(db: DownLoadDb): Disposable {
        if (db.downLoadFile == null || !db.downLoadFile!!.exists()) {
            var file: File
            if (db.savePath.isEmpty()) {
                file = File(context.externalCacheDir, db.fileName)
            } else {
                file =
                    File(
                        db.savePath,
                        db.fileName
                    )
            }
            db.downLoadFile = file
        }

        val readLenth = db.downLoadFile!!.length()
        val rangeStr = "bytes=" + readLenth + "-"
        return createDowload(RetriofitApi::class.java)
            .download(rangeStr, db.downLoadUrl)
            .map { response ->
                db.totalLength = response.contentLength() + readLenth
                response.byteStream()
            }
            .flatMap { inputstream ->
                myLog("接收字节_____")
                Flowable.create(FlowableOnSubscribe<DownLoadDb> {
                    val out =
                        BufferedOutputStream(
                            FileOutputStream(
                                db.downLoadFile!!,
                                true
                            )
                        )
                    //开始下载
                    val buff = ByteArray(4096)
                    var len: Int
                    myLog("request__" + it.requested() + "__" + it.isCancelled)
                    while (!it.isCancelled && db.disposable != null && !db.disposable!!.isDisposed) {
                        try {
                            Thread.sleep(200)
                            len = inputstream.read(buff)
                            if (len == -1)
                                break
                            out.write(buff, 0, len)
                            downLoadDao?.insertOrUpdate(db)
                            it.onNext(db)
                        } catch (e: Exception) {
                            it.tryOnError(e)
                            myLog("sleeperror__" + e.message)
                        }
                    }
                    out.close()
                    it.onComplete()
                }, BackpressureStrategy.ERROR)
            }.observeOn(Schedulers.io()).map {
                updateProgress(db, true)
                it
            }.observeOn(Schedulers.io()).doOnComplete {
                updateProgress(db, false)
            }.compose(switchThread()).subscribe({
                myLog("next__")
            }, {
                iDownLoadListner?.error(it.message)
            }, {
                myLog("完成")
            })
    }

    private fun updateProgress(db: DownLoadDb, isbegin: Boolean) {
        db.readLength = db.downLoadFile!!.length()
        iDownLoadListner?.update(
            db.readLength,
            db.totalLength,
            db.downLoadUrl, isbegin
        )
    }
}