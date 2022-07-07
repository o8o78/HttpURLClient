package com.byteroll.demo

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.byteroll.Utils
import com.byteroll.demo.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val black = R.drawable.push_logo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.setUIFlags(this)
        init()
        doTest()
    }

    private fun init() {

    }

    private fun doTest() {
        binding.SendNotice.setOnClickListener {
            doNotificationTest()
        }
        binding.startHttp.setOnClickListener {
            requestWithHttpUrlConnection()
        }
    }

    private fun doNotificationTest() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            val intent = Intent(this, NotificationActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(this, "normal")
                .setContentTitle("Notification Test")
                .setContentText("This si notification test")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Pumas are large, cat-like animals which were found in America. So, when reports come into London Zoo that a wild puma had been found 45miles south of London")
                )
                .setSmallIcon(R.drawable.push_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            manager.notify(1, notification)
        }
    }

    private fun requestWithHttpUrlConnection(){
        //线程中做耗时操作
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://www.pku.edu.cn")
                    .build()
                val response = client.newCall(request).execute()
                var responseData = response.body?.string()
                val regScript = Regex("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>")
                val regStyle = Regex("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>")
                val regHtml = Regex("<[^>]+>")
                val regEnter = Regex("\\s*")
//                val regLine = "(?m)^\\s*\$(\\n|\\r\\n)"
                if (responseData!=null){
                    responseData = regScript.replace(responseData, "")
                    responseData = regStyle.replace(responseData, "")
                    responseData = regHtml.replace(responseData, "")
                    responseData = responseData.trim()
                    responseData = regEnter.replace(responseData, "")
                    showResponse(responseData)
                }
            } catch (e: Exception){
                e.printStackTrace()
                e.println()
            }
        }
    }

    private fun showResponse(msg: String){
        runOnUiThread {
            binding.response.text = msg
        }
    }

}

class MyThread: Runnable{
    override fun run() {

    }
}

private fun <T> T?.println() = println(this)

private fun <T> T?.log() = Log.d("MainActivity", this.toString())