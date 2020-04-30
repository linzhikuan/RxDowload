
<h3>
描述：
</h3>
<h5>
rxdowload是一个下载器,使用简单，用到aidl ipc通信模式，rxjava okhttp retrofit 目前流行的网络请求和异步操作。
kotlin语法
</h5>

<h3>
基本使用：
</h3>
<body>
<h4>
如果是kotlin直接使用iDownLoad?.操作  如果是java DownLoadManager.Companion.getInstance().getIDowload().操作
</h4>
<div>
iDownLoad?.startDownLoad(
                    DowloadBuild
                        .Build()
                        .setDowloadBean(
                            DownloadBean(
                                "http://dldir1.qq.com/weixin/android/weixin6330android920.apk"
                            )
                        ).create()
                )
</div>

<p>
iDownLoad?.stopDowload("http://dldir1.qq.com/weixin/android/weixin6330android920.apk")
</p>

<p>
iDownLoad?.queryProgress(object : IDownLoadListner.Stub() {

                override fun update(
                    readLenth: Long,
                    totalLenth: Long,
                    dowloadUrl: String?,
                    isbegin: Boolean
                ) {
                        val progressValue = ((readLenth * 1.0 / totalLenth * 100).toInt())
                }
                
                override fun error(error: String?) {
                }
            })
</p>
</body>











<h3>
aidl接口：
</h3>
<h5>
interface IDownLoad {
void startDownLoad(in DowloadBuild build);

void queryProgress(IDownLoadListner iDownLoadListner);

void stopDowload(String url);

void cancleDowload(String url);

void cancleAllDowload();

void stopAllDowload();
}
</h5>



