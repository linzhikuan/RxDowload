package com.lzk.rxdowloadlib.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.Serializable

@Entity(tableName = "DownLoadDb")
class DownLoadDb : Serializable {
    @PrimaryKey
    @ColumnInfo(name = "downLoadUrl")
    var downLoadUrl: String = ""
    @ColumnInfo(name = "savePath")
    var savePath: String = ""
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
}