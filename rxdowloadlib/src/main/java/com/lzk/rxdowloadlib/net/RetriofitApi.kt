package com.lzk.rxdowloadlib.net

import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.http.*

interface RetriofitApi {

    /*断点续传下载接口*/
    /*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @Streaming
    @GET
    fun download(@Header("Range") start: String, @Url url: String): Flowable<ResponseBody>
}




