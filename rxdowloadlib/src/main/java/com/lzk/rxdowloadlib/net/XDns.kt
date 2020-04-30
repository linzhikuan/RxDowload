package com.lzk.rxdowloadlib.net

import okhttp3.Dns

import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

class XDns(private val timeout: Long) : Dns {

    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String?): List<InetAddress> {
        return if (hostname == null) {
            throw UnknownHostException("hostname == null")
        } else {
            try {
                val task = FutureTask(
                    Callable { Arrays.asList(*InetAddress.getAllByName(hostname)) })
                Thread(task).start()
                task.get(timeout, TimeUnit.SECONDS)
            } catch (var4: Exception) {
                val unknownHostException = UnknownHostException("Broken system behaviour for dns lookup of $hostname")
                unknownHostException.initCause(var4)
                throw unknownHostException
            }

        }
    }
}