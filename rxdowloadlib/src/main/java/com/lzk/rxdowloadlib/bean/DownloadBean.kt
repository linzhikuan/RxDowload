package com.lzk.rxdowloadlib.bean

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class DownloadBean : Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DownloadBean> = object : Parcelable.Creator<DownloadBean> {
            override fun createFromParcel(p0: Parcel?): DownloadBean {
                return (DownloadBean(p0))
            }

            override fun newArray(p0: Int): Array<DownloadBean?> {
                return arrayOfNulls(p0)
            }
        }
    }

    var dowloadUrl: String? = null
    var savePath: String? = null
    var fileName: String? = null

    constructor(p0: Parcel?) {
        dowloadUrl = p0?.readString()
        savePath = p0?.readString()
        fileName = p0?.readString()
    }

    constructor(dowloadUrl: String, savePath: String, fileName: String) {
        this.dowloadUrl = dowloadUrl
        this.savePath = savePath
        this.fileName = fileName
    }

    constructor(dowloadUrl: String) : this(dowloadUrl, "", "")
    constructor(dowloadUrl: String, fileName: String) : this(dowloadUrl, "", fileName)

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(dowloadUrl)
        p0?.writeString(savePath)
        p0?.writeString(fileName)
    }

    override fun describeContents(): Int {
        return 0
    }
}