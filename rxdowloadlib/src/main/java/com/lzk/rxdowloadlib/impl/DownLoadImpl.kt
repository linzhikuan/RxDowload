package com.lzk.rxdowloadlib.impl

import android.content.Context
import android.util.Log
import com.lzk.rxdowloadlib.IDownLoad
import com.lzk.rxdowloadlib.IDownLoadListner
import com.lzk.rxdowloadlib.bean.DowloadBuild
import com.lzk.rxdowloadlib.net.RetriofitApi
import com.lzk.rxdowloadlib.net.createDowload
import com.lzk.rxdowloadlib.net.switchThread
import com.lzk.rxdowloadlib.room.DbDataBase
import com.lzk.rxdowloadlib.bean.DownLoadDb
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class DownLoadImpl(var context: Context) : IDownLoad.Stub() {
    private val tag: String = "RxDowloadService>>>"
    private var subMap = HashMap<String?, DownLoadDb>()
    private var iDownLoadListner: IDownLoadListner? = null
    private var downLoadDao = DbDataBase.getInstance(context)?.downLoadDao()
    private var cancleDisposable: Disposable? = null
    private var cancleAllDispose: Disposable? = null
    private var observableDbDispose: Disposable? = null
    private fun myLog(msg: String) {
        Log.d(tag, msg)
    }

    override fun stopDowload(url: String?) {
        subMap[url]?.disposable?.dispose()
        subMap[url]?.disposable = null
    }

    override fun cancleDowload(url: String?) {
        cancleDisposable = Flowable.just(url).map {
            stopDowload(url)
            subMap.remove(url)
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
            subMap.forEach {
                stopDowload(it.key)
            }
            subMap.clear()
            downLoadDao?.delete()
            true
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
        observarbleDb()
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

    private fun download(mdb: DownLoadDb): Disposable? {
        if (mdb.downLoadFile == null || !mdb.downLoadFile!!.exists()) {
            var file: File
            if (mdb.savePath.isEmpty()) {
                file = File(context.externalCacheDir, mdb.fileName)
            } else {
                file =
                    File(
                        mdb.savePath,
                        mdb.fileName
                    )
            }
            mdb.absolutePath = file.path
            mdb.downLoadFile = file
        }

        val readLenth = mdb.downLoadFile!!.length()
        mdb.readLength = readLenth
        val rangeStr = "bytes=" + readLenth + "-"
        return Flowable.just(mdb).map { db ->
            val local = downLoadDao?.getDownLoadDb(db.downLoadUrl)
            myLog("local__" + local?.totalLength + "__" + db.readLength)
            if (db.readLength == local?.totalLength ?: 1) {
                throw Exception("已经下载完成")
            }
            db
        }.flatMap { db ->
            createDowload(RetriofitApi::class.java)
                .download(rangeStr, db.downLoadUrl)
                .map { response ->
                    db.totalLength = response.contentLength() + readLenth
                    response.byteStream()
                }
                .map { inputstream ->
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
                    while (db.disposable != null && !db.disposable!!.isDisposed) {
                        try {
                            len = inputstream.read(buff)
                            if (len == -1)
                                break
                            out.write(buff, 0, len)
                            db.state = 1
                            downLoadDao?.insertOrUpdate(db)
                            myLog("saving__" + db.absolutePath)
                        } catch (e: Exception) {
                            db.disposable?.dispose()
                            iDownLoadListner?.error("下载失败")
                        }

                    }
                    out.close()
                    db.state = 0
                    downLoadDao?.insertOrUpdate(db)
                    true
                }
        }.compose(switchThread()).subscribe({
            myLog("__" + it)
        }, {
            iDownLoadListner?.error("下载失败")
            myLog("error__" + it.message + "__" + (iDownLoadListner == null))
        })
    }

    @Synchronized
    private fun observarbleDb() {
        if (iDownLoadListner == null) {
            observableDbDispose?.dispose()
            return
        }
        observableDbDispose?.dispose()
        observableDbDispose =
            downLoadDao
                ?.getAllDownLoadDbFlowable()
                ?.compose(switchThread())
                ?.subscribe({ list ->
                    list?.forEach { db ->
                        val file = File(db.absolutePath)
                        db.readLength = file.length()
                        myLog("observarbleDb__" + db.absolutePath + "__" + file.length())
                        iDownLoadListner?.update(
                            db
                        )
                    }
                }, {
                    iDownLoadListner?.error("获取信息失败")
                    myLog(it.message ?: "")
                })
    }

}