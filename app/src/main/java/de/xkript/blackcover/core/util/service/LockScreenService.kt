package de.xkript.blackcover.core.util.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import de.xkript.blackcover.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class LockScreenService : Service() {
    
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var view: ViewGroup
    private var layoutType: Int? = null
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var job: Job? = null
    private var fontId: Int = R.font.oswald_regular
    private var tapCounter: Int = 1
    private var isSkipUnlockScreen: Boolean = false
    private var tapCounterHelper: Int = 1
    private lateinit var llRoot: LinearLayout
    private lateinit var txtTime: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtUnlock: TextView
    private var clickJob: Job? = null
    private var unlockJob: Job? = null
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    @SuppressLint("InflateParams", "ClickableViewAccessibility", "ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        
        // Tools
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(de.xkript.blackcover.R.layout.lock_screen_layout, null) as ViewGroup
        layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        llRoot = view.findViewById(de.xkript.blackcover.R.id.root)
        txtTime = view.findViewById(de.xkript.blackcover.R.id.txt_time)
        txtDate = view.findViewById(de.xkript.blackcover.R.id.txt_date)
        txtUnlock = view.findViewById(de.xkript.blackcover.R.id.txt_unlock)
        
        // Prepare layout
        layoutParams = WindowManager.LayoutParams(
            width,
            height,
            layoutType!!,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT,
        )
        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = 0
        
        // Keep the service alive by create and show notif for android >= 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotification()
        }
        
        // Click
        llRoot.setOnClickListener {
            if (tapCounterHelper == 0) {
                if (isSkipUnlockScreen) {
                    stopSelf()
                }
                else {
                    txtUnlock.visibility = View.VISIBLE
                    unlockJob?.cancel()
                    unlockJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        txtUnlock.visibility = View.GONE
                    }
                }
            }
            else {
                tapCounterHelper--
                clickJob?.cancel()
                clickJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(1000)
                    tapCounterHelper = tapCounter
                }
            }
        }
        txtUnlock.setOnClickListener {
            stopSelf()
        }
        
        // Show it
        windowManager.addView(view, layoutParams)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get data from intent
        fontId = intent?.getIntExtra("font_id", R.font.oswald_regular) ?: R.font.oswald_regular
        tapCounter = intent?.getIntExtra("tap_counter", R.font.oswald_regular) ?: R.font.oswald_regular
        isSkipUnlockScreen = intent?.getBooleanExtra("is_skip_unlock_screen", false) ?: false
        
        tapCounterHelper = tapCounter
        // Set font to texts
        val typeface = ResourcesCompat.getFont(applicationContext, fontId)
        txtTime.setTypeface(typeface)
        txtDate.setTypeface(typeface)
        txtUnlock.setTypeface(typeface)
        
        // Show time
        showTimeDate()
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        clickJob?.cancel()
        unlockJob?.cancel()
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
        val notificationId = 2
        val notification: Notification = builder.setOngoing(true)
            .setSmallIcon(de.xkript.blackcover.R.drawable.ic_notification)
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
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        }
    }
    
    private fun showTimeDate() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(10) // Delay for one second
                
                // Tools
                val calendar = Calendar.getInstance()
                
                txtTime.text = getTimeInfo(calendar)
                txtDate.text = getDateInfo(calendar)
                
            }
        }
    }
    
    private fun getDateInfo(calendar: Calendar): String {
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val dayOfWeekName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return "$dayOfWeekName, $monthName$dayOfMonth"
    }
    
    private fun getTimeInfo(calendar: Calendar): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }
    
}