package com.skichrome.radio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    // =======================================
    //                  Fields
    // =======================================

    companion object
    {
        const val RADIO_RTL2 = 91.5

        const val PORT = 1234

        const val DRIVER_RESULT_ID = 1234
        const val COMMANDS_ID = "supportedTcpCommands"

        const val TAG = "MainActivity"
    }

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCmd.setOnClickListener { configureIntent() }
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
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("iqsrc://-a 127.0.0.1 -p $PORT"))
        startActivityForResult(intent, DRIVER_RESULT_ID)
    }
}