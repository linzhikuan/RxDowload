package com.lzk.rxdowloadlib.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.Serializable

@Entity(tableName = "DownLoadDb")
class DownLoadDb : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "downLoadUrl")
    var downLoadUrl: String = ""
    @ColumnInfo(name = "savePath")
    var savePath: String = ""
    @ColumnInfo(name = "absolutePath")
    var absolutePath: String = ""
    @ColumnInfo(name = "fileName")
    var fileName: String = ""
    @ColumnInfo(name = "readLength")
    var readLength: Long = 0
    @ColumnInfo(name = "totalLength")
    var totalLength: Long = 0

    @ColumnInfo(name = "state")
    var state: Int = 0//0已暂停1已开始


    @Transient
    var downLoadFile: File? = null
    @Transient
    var disposable: Disposable? = null

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(downLoadUrl)
        p0?.writeString(savePath)
        p0?.writeString(fileName)
        p0?.writeLong(readLength)
        p0?.writeLong(totalLength)
        p0?.writeInt(state)
        p0?.writeString(absolutePath)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DownLoadDb> = object : Parcelable.Creator<DownLoadDb> {
            override fun createFromParcel(p0: Parcel?): DownLoadDb {
                return (DownLoadDb(p0))
            }

            override fun newArray(p0: Int): Array<DownLoadDb?> {
                return arrayOfNulls(p0)
            }
        }
    }

    constructor()

    fun readFromParcel(p0: Parcel?) {
        downLoadUrl = p0?.readString() ?: ""
        savePath = p0?.readString() ?: ""
        fileName = p0?.readString() ?: ""
        readLength = p0?.readLong() ?: 0
        totalLength = p0?.readLong() ?: 0
        state = p0?.readInt() ?: 0
        absolutePath = p0?.readString() ?: ""
    }

    constructor(p0: Parcel?) {
        downLoadUrl = p0?.readString() ?: ""
        savePath = p0?.readString() ?: ""
        fileName = p0?.readString() ?: ""
        readLength = p0?.readLong() ?: 0
        totalLength = p0?.readLong() ?: 0
        state = p0?.readInt() ?: 0
        absolutePath = p0?.readString() ?: ""
    }

}