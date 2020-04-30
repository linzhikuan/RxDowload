package com.lzk.rxdowloadlib.bean

import android.os.Parcel
import android.os.Parcelable
import java.lang.Exception
import java.lang.StringBuilder

class DowloadBuild : Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DowloadBuild> = object : Parcelable.Creator<DowloadBuild> {
            override fun createFromParcel(p0: Parcel?): DowloadBuild {
                return DowloadBuild(p0)
            }

            override fun newArray(p0: Int): Array<DowloadBuild?> {
                return arrayOfNulls(p0)
            }
        }
    }

    var dowloadBean = ArrayList<DownloadBean>()

    constructor()
    constructor(p0: Parcel?) {
        p0?.readTypedList(dowloadBean, DownloadBean.CREATOR)
    }


    class Build {
        val dowloadTask = DowloadBuild()

        fun setDowloadBean(vararg downloadBean: DownloadBean): Build {
            dowloadTask.dowloadBean.addAll(downloadBean)
            return this
        }

        fun create(): DowloadBuild {
            dowloadTask.dowloadBean.forEach { dowloadBean ->
                if (dowloadBean.dowloadUrl.isNullOrEmpty())
                    throw Exception("下载地址为空")
                else
                    if (!dowloadBean.dowloadUrl!!.startsWith("http://")
                        && !dowloadBean.dowloadUrl!!.startsWith("https://")
                    ) {
                        throw Exception("下载地址有错误")
                    }
                if (dowloadBean.fileName.isNullOrEmpty()) {
                    val lastIndex1 = dowloadBean.dowloadUrl!!.lastIndexOf("/")
                    val lastIndex2 = dowloadBean.dowloadUrl!!.lastIndexOf(".")
                    val fileName =
                        dowloadBean.dowloadUrl!!.subSequence(lastIndex1 + 1, lastIndex2)
                    val fileType =
                        dowloadBean.dowloadUrl!!.subSequence(
                            lastIndex2,
                            dowloadBean.dowloadUrl!!.length
                        )
                    val sb = StringBuilder()
                    sb.append(fileName)
                    sb.append(fileType)
                    dowloadBean.fileName = sb.toString()
                }
            }
            return dowloadTask
        }
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeTypedList(dowloadBean)
    }

    override fun describeContents(): Int {
        return 0
    }

}