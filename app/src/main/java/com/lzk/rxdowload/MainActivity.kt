package com.lzk.rxdowload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lzk.rxdowloadlib.IDownLoadListner
import com.lzk.rxdowloadlib.bean.DownloadBean
import com.lzk.rxdowloadlib.bean.DowloadBuild
import com.lzk.rxdowloadlib.manager.iDownLoad
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isStart = false
    val mHandler = Handler(Looper.getMainLooper())
    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler.postDelayed({
            iDownLoad?.queryProgress(object : IDownLoadListner.Stub() {
                override fun update(
                    readLenth: Long,
                    totalLenth: Long,
                    dowloadUrl: String?,
                    isbegin: Boolean
                ) {
                    mHandler.post {
                        val progressValue = ((readLenth * 1.0 / totalLenth * 100).toInt())
                        progress.secondaryProgress = progressValue
                        text.text = progressValue.toString() + "__" + readLenth + "__" + totalLenth
                        if (progressValue == 100) {
                            dowload_btn.text = "安装"
                        } else {
                            if (isbegin) {
                                isStart = true
                                dowload_btn.text = "暂停"
                            } else {
                                isStart = false
                                dowload_btn.text = "继续"
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
        }, 1000)

        dowload_btn.setOnClickListener {
            if (isStart) {
                iDownLoad?.stopDowload("http://dldir1.qq.com/weixin/android/weixin6330android920.apk")
            } else {
                iDownLoad?.startDownLoad(
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
