// IDownLoad.aidl
package com.lzk.rxdowloadlib;

import com.lzk.rxdowloadlib.bean.DowloadBuild;
import com.lzk.rxdowloadlib.IDownLoadListner;
// Declare any non-default types here with import statements

interface IDownLoad {
void startDownLoad(in DowloadBuild build);

void queryProgress(IDownLoadListner iDownLoadListner);

void stopDowload(String url);

void cancleDowload(String url);

void cancleAllDowload();

void stopAllDowload();
}