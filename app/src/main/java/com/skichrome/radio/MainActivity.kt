package com.skichrome.radio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skichrome.radio.utils.AsyncTaskListenServer
import com.skichrome.radio.utils.ByteConvert
import com.skichrome.radio.utils.RtlCmdList
import com.skichrome.radio.utils.TcpClient
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), AsyncTaskListenServer.OnProgressUpdateListener
{
    // =======================================
    //                  Fields
    // =======================================

    companion object
    {
//        const val RADIO_RTL2 = 91.5
//        val buffer = arrayOf(0x01, 0x05, 0x07)
//        val bufferSampleRate = byteArrayOf(0x02, 0x00, 0x24, 0x9f.toByte(), 0x00)

        const val IP = "127.0.0.1"
        const val PORT = 1234
        const val DRIVER_RESULT_ID = 1234
        const val COMMANDS_ID = "supportedTcpCommands"
        const val TAG = "MainActivity"
    }

    private lateinit var mAsyncTask: AsyncTaskListenServer

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartListen.setOnClickListener { configureAsyncTask() }
        btnStart.setOnClickListener { configureIntent() }
        btnStop.setOnClickListener { stopTcpClient() }
        btnSend.setOnClickListener { sendCommandToRtl() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode != DRIVER_RESULT_ID)
        {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        if (resultCode == RESULT_OK)
        {
            Log.e(TAG, "RESULT_OK")
            val supportedCommands = data?.getIntArrayExtra(COMMANDS_ID)
            var result = "Commands : \n"
            supportedCommands?.forEach { result += it.toString() + "\n" }
            textView.text = result
        }
    }

    // =======================================
    //                 Methods
    // =======================================

    private fun configureIntent()
    {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("iqsrc://-a $IP -p $PORT"))
//        val test = "iqsrc://-a 0.0.0.0 -p $PORT  -s $bufferSampleRate"
        startActivityForResult(intent, DRIVER_RESULT_ID)
    }

    private fun configureAsyncTask()
    {
        mAsyncTask = AsyncTaskListenServer(this)
        mAsyncTask.execute()
    }

    override fun onPause()
    {
        if (this::mAsyncTask.isInitialized)
            mAsyncTask.cancel(true)
        super.onPause()
    }

    override fun onUpdate(value: String)
    {
        handleMsg(value)
    }

    private fun handleMsg(msg: String)
    {
        Log.e(TAG, "New msg available : $msg")
    }

    // Send data
    private fun sendCommandToRtl()
    {
        mAsyncTask.getTcpClient()?.sendMessage(ByteConvert.decodeStr("05742DE0"))
    }

    private fun stopTcpClient()
    {
        mAsyncTask.getTcpClient()?.stopClient()
    }
}