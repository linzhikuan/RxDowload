package com.lzk.rxdowload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lzk.rxdowloadlib.bean.DownloadBean
import com.lzk.rxdowloadlib.bean.DowloadBuild
import com.lzk.rxdowloadlib.bean.DownLoadDb
import com.lzk.rxdowloadlib.manager.DownLoadManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val tag: String = "RxDowloadService>>>"
    private var isStart = false
    val mHandler = Handler(Looper.getMainLooper())

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        DownLoadManager.getInstance().removeCallBack()
    }


    private fun myLog(msg: String) {
        Log.d(tag, msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler.postDelayed({
            DownLoadManager.getInstance().addCalBack(object : DownLoadManager.DownLoadCallBack {
                override fun update(it: DownLoadDb?) {
                    it?.let { db ->
                        myLog("path__" + db.absolutePath)
                        mHandler.post {
                            val progressValue =
                                ((db.readLength * 1.0 / db.totalLength * 100).toInt())
                            progress.secondaryProgress = progressValue
                            text.text =
                                progressValue.toString() + "__" + db.readLength + "__" + db.totalLength

                            myLog(text.text.toString())
                            if (progressValue == 100) {
                                dowload_btn.text = "安装"
                            } else {
                                if (db.state == 1) {
                                    isStart = true
                                    dowload_btn.text = "暂停"
                                } else {
                                    isStart = false
                                    dowload_btn.text = "继续"
                                }
                            }
                        }
                    }
                }

                override fun error(error: String?) {
                    text.text = error
                    isStart = false
                    dowload_btn.text = "继续"
                }
            })
        }, 2000)

        dowload_btn.setOnClickListener {
            if (isStart) {
                DownLoadManager.getInstance()
                    .stopDownLoad("http://dldir1.qq.com/weixin/android/weixin6330android920.apk")
            } else {
                DownLoadManager.getInstance().startDownLoad(
                    DowloadBuild
                        .Build()
                        .setDowloadBean(
                            DownloadBean(
                                "http://dldir1.qq.com/weixin/android/weixin6330android920.apk"
                            )
                        ).create()
                )
            }
            isStart = !isStart

        }

    }

}
