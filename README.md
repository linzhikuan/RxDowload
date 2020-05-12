
描述：
rxdowload是一个下载器,使用简单，支持断点续传，用到aidl ipc通信模式，rxjava okhttp retrofit 目前流行的网络请求和异步操作。
kotlin语法

基本使用：
implementation 'com.lzk.rxdownload:rxdowloadlib:1.4.0'//添加到gradle
application{
DownLoadManager.getInstance().init(this)//初始化在你的aplication
}
服务注册:RxDowloadService这个service注册到你的manifest


          //开始下载
                DownLoadManager.getInstance().startDownLoad(
                    DowloadBuild
                        .Build()
                        .setDowloadBean(
                            DownloadBean(
                                "http://dldir1.qq.com/weixin/android/weixin6330android920.apk"
                            )
                        ).create()
                )

          //停止下载
                DownLoadManager.getInstance()
                    .stopDownLoad("http://dldir1.qq.com/weixin/android/weixin6330android920.apk")


          //进度监听
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








