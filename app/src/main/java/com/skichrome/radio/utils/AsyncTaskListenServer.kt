package com.skichrome.radio.utils

import android.os.AsyncTask

class AsyncTaskListenServer(private val mCallback: OnProgressUpdateListener) :
    AsyncTask<String, String, Any>()
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var mTcpClient: TcpClient

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun doInBackground(vararg params: String?)
    {
        mTcpClient = TcpClient(object : TcpClient.OnMessageReceived
        {
            override fun newMsgAvailable(msg: String)
            {
                publishProgress(msg)
            }
        })
        mTcpClient.runClient()
    }

    override fun onProgressUpdate(vararg values: String?)
    {
        super.onProgressUpdate(*values)
        if (values[0] != null)
            mCallback.onUpdate(values[0] as String)
    }

    fun getTcpClient(): TcpClient?
    {
        return if (this::mTcpClient.isInitialized)
            mTcpClient
        else
            null
    }

    interface OnProgressUpdateListener
    {
        fun onUpdate(value: String)
    }
}