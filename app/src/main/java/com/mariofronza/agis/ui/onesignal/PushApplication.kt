package com.mariofronza.agis.ui.onesignal

import android.app.Application
import com.onesignal.OneSignal

class PushApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }

    companion object {
        private const val ONESIGNAL_APP_ID = "98fe2c2e-e4e5-4953-aafb-46faac044c66"
    }
}