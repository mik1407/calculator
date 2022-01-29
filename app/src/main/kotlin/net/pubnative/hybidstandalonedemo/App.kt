package net.pubnative.hybidstandalonedemo

import android.app.Application
import com.simplemobiletools.commons.extensions.checkUseEnglish
import net.pubnative.lite.sdk.HyBid

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()

        HyBid.initialize("dde3c298b47648459f8ada4a982fa92d", this)
        HyBid.setTestMode(true)
    }
}
