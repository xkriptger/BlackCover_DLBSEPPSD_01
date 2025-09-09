package de.xkript.blackcover.core.util.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import de.xkript.blackcover.R

class FloatingFabService : Service() {
    
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var view: ViewGroup
    private var layoutType: Int? = null
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var imgFab: ImageView
    private var fabIconId: Int = R.drawable.ic_stroke_button
    private var fontId: Int = R.font.oswald_regular
    private var tapCounter: Int = 1
    private var isSkipUnlockScreen: Boolean = false
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    @SuppressLint("InflateParams", "ClickableViewAccessibility", "ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        
        // Tools
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.floating_fab_layout, null) as ViewGroup
        layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        imgFab = view.findViewById(R.id.img_fab)
        
        // Prepare layout
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        )
        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = 0
        
        // Keep the service alive by create and show notif for android >= 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotification()
        }
        
        // Handel layout move event
        imgFab.setOnTouchListener(object : OnTouchListener {
            val layoutUpdateParam: WindowManager.LayoutParams = layoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = layoutUpdateParam.x.toDouble()
                        y = layoutUpdateParam.y.toDouble()
                        
                        // returns the original raw X
                        // coordinate of this event
                        px = event.rawX.toDouble()
                        
                        // returns the original raw Y
                        // coordinate of this event
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        layoutUpdateParam.x = (x + event.rawX - px).toInt()
                        layoutUpdateParam.y = (y + event.rawY - py).toInt()
                        
                        // updated parameter is applied to the WindowManager
                        windowManager.updateViewLayout(view, layoutUpdateParam)
                    }
                }
                return false
            }
        })
        
        // Click listener
        imgFab.setOnClickListener {
            val intent = Intent(applicationContext, LockScreenService::class.java)
            intent.putExtra("font_id", fontId)
            intent.putExtra("tap_counter", tapCounter)
            intent.putExtra("is_skip_unlock_screen", isSkipUnlockScreen)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
            else {
                startService(intent)
            }
        }
        
        // Show it
        windowManager.addView(view, layoutParams)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        
        // Get data from intent
        fabIconId = intent?.getIntExtra("fab_icon_id", R.drawable.ic_stroke_button) ?: R.drawable.ic_stroke_button
        fontId = intent?.getIntExtra("font_id", R.font.oswald_regular) ?: R.font.oswald_regular
        tapCounter = intent?.getIntExtra("tap_counter", 1) ?: 1
        isSkipUnlockScreen = intent?.getBooleanExtra("is_skip_unlock_screen", false) ?: false
        
        // Update FabIcon
        imgFab.setImageDrawable(getDrawable(fabIconId))
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(STOP_FOREGROUND_DETACH)
        }
        else {
            stopSelf()
        }
        windowManager.removeView(view)
    }
    
    // Other
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ForegroundServiceType")
    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, "running_channel")
        val notificationId = 1
        val notification: Notification = builder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("BlackCover is Running")
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setSilent(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(notificationId, notification)
        }
        else {
            startForeground(
                notificationId, notification,
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        }
    }
    
}