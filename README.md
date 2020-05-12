

<h3>
描述：
</h3>
<h5>
rxdowload是一个下载器,使用简单，用到aidl ipc通信模式，rxjava okhttp retrofit 目前流行的网络请求和异步操作。
kotlin语法
</h5>
<h3>
基本使用：
implementation 'com.lzk.rxdownload:rxdowloadlib:1.4.0'


application{
DownLoadManager.getInstance().init(this)
}
</h3>
<body>
<p>服务注册</p>
 <service
            android:name="com.lzk.rxdowloadlib.service.RxDowloadService"
            android:process=":remote" />



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



            </body>




