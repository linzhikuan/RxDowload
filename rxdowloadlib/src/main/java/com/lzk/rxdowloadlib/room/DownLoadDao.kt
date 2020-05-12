package com.lzk.rxdowloadlib.room


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lzk.rxdowloadlib.bean.DownLoadDb
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by empty on 2018/11/23.
 */

@Dao
interface DownLoadDao {
    @Query("SELECT * FROM DownLoadDb")
    fun getAllDownLoadDbFlowable(): Flowable<List<DownLoadDb>>

    @Query("SELECT * FROM DownLoadDb")
    fun getAllDownLoadDb(): List<DownLoadDb>

    @Query("SELECT * FROM DownLoadDb WHERE downLoadUrl == :url")
    fun getDownLoadDbFlowable(url: String): Flowable<DownLoadDb>

    @Query("SELECT * FROM DownLoadDb WHERE downLoadUrl == :url")
    fun getDownLoadDb(url: String): DownLoadDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateCompletable(data: DownLoadDb?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(data: DownLoadDb?)


    @Query("DELETE FROM DownLoadDb WHERE downLoadUrl == :url")
    fun delete(url: String)

    @Query("DELETE FROM DownLoadDb")
    fun delete()
}
