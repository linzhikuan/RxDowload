// IDownLoadListner.aidl
package com.lzk.rxdowloadlib;

// Declare any non-default types here with import statements

interface IDownLoadListner {
        void update(long readLenth,long totalLenth,String dowloadUrl,boolean isbegin);
        void error(String error);
}
