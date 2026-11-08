package com.mariofronza.agis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mariofronza.agis.R
import com.onesignal.OneSignal
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 13+ requires asking for POST_NOTIFICATIONS at runtime.
        if (savedInstanceState == null) lifecycleScope.launch { OneSignal.Notifications.requestPermission(false) }
    }
}
