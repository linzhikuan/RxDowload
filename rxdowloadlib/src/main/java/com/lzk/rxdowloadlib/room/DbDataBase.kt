/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.lzk.rxdowloadlib.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DownLoadDb::class],
    version = 1
)
abstract class DbDataBase : RoomDatabase() {

    abstract fun downLoadDao(): DownLoadDao

    companion object {
        private var INSTANCE: DbDataBase? = null

        fun getInstance(context: Context): DbDataBase? {
            if (INSTANCE == null) {
                synchronized(DbDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        DbDataBase::class.java,
                        "rxdowload.db"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}