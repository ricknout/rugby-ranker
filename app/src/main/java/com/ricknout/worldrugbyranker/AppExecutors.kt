package com.ricknout.worldrugbyranker

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class AppExecutors(
        val diskIO: Executor,
        val networkIO: Executor,
        val mainThread: Executor
)

class MainThreadExecutor : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }
}