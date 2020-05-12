// IDownLoadListner.aidl
package com.lzk.rxdowloadlib;

// Declare any non-default types here with import statements
import com.lzk.rxdowloadlib.bean.DownLoadDb;

interface IDownLoadListner {
//        void update(long readLenth,long totalLenth,String dowloadUrl,boolean isbegin);
        void update(in DownLoadDb db);
        void error(String error);
}
