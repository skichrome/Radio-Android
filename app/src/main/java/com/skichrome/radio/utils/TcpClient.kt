package com.skichrome.radio.utils

import android.util.Log
import java.io.*
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket

class TcpClient(private var mCallback: OnMessageReceived?)
{
    // =======================================
    //           Callback interface
    // =======================================

    interface OnMessageReceived
    {
        fun newMsgAvailable(msg: String)
    }

    // =======================================
    //                  Fields
    // =======================================

    companion object
    {
        const val TCP_IP = "127.0.0.1"
        const val TCP_PORT = 1234
        const val TAG = "TcpClient"
    }

    private var mIsClientRunning = false
    private var mServerMsg: String? = null

    private var mPrintWriter: PrintWriter? = null
    private var mBufferedReader: BufferedReader? = null

    // =======================================
    //                 Methods
    // =======================================

    fun sendMessage(msg: ByteArray)
    {
        val runnable = Runnable {
            run {
                if (mPrintWriter != null && !mPrintWriter!!.checkError())
                {
                    mPrintWriter!!.println(msg)
                    mPrintWriter!!.flush()
                }
            }
        }
        Thread(runnable).also { it.start() }

    }

    fun runClient()
    {
        mIsClientRunning = true

        val serverAddr = InetAddress.getByName(TCP_IP)
        Log.d(TAG, "Connecting...")
        val socket = Socket(serverAddr, TCP_PORT)

        try
        {
            mPrintWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
            mBufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))

            var charRead = 0
            val buffer = CharArray(8)

            while (mIsClientRunning)
            {
                charRead = mBufferedReader?.read(buffer) ?:0

                mServerMsg = String(buffer).substring(0, charRead)

                if (mServerMsg != null)
                {
                    mCallback?.newMsgAvailable(mServerMsg as String)
                    mServerMsg = null
                }
            }
            if (mServerMsg != null)
                Log.d(TAG, "Server Response : $mServerMsg")
            else
                Log.e(TAG, "Server Response is null !!")
        } catch (e: Exception)
        {
            Log.e(TAG, "An exception has been thrown in TCP client", e)
        } finally
        {
            socket.close()
        }
    }

    fun stopClient()
    {
        sendMessage(byteArrayOf(0x7E))
        mIsClientRunning = false

        if (mPrintWriter != null)
        {
            mPrintWriter!!.flush()
            mPrintWriter!!.close()
        }
        mCallback = null
        mPrintWriter = null
        mBufferedReader = null
    }
}