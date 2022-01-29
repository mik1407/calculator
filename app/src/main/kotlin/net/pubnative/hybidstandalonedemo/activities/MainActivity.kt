package net.pubnative.hybidstandalonedemo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.LICENSE_AUTOFITTEXTVIEW
import com.simplemobiletools.commons.models.FAQItem
import com.simplemobiletools.commons.models.Release
import kotlinx.android.synthetic.main.activity_main.*
import me.grantland.widget.AutofitHelper
import net.pubnative.hybidstandalonedemo.BuildConfig
import net.pubnative.hybidstandalonedemo.R
import net.pubnative.hybidstandalonedemo.databases.CalculatorDatabase
import net.pubnative.hybidstandalonedemo.dialogs.HistoryDialog
import net.pubnative.hybidstandalonedemo.extensions.config
import net.pubnative.hybidstandalonedemo.extensions.updateViewColors
import net.pubnative.hybidstandalonedemo.helpers.*
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView


class MainActivity : SimpleActivity(), Calculator {
    private var storedTextColor = 0
    private var vibrateOnButtonPress = true

    lateinit var calc: CalculatorImpl

    private var mInterstitial : HyBidInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appLaunched(BuildConfig.APPLICATION_ID)

        calc = CalculatorImpl(this, applicationContext)

        btn_plus.setOnClickListener { calc.handleOperation(PLUS); checkHaptic(it) }
        btn_minus.setOnClickListener { calc.handleOperation(MINUS); checkHaptic(it) }
        btn_multiply.setOnClickListener { calc.handleOperation(MULTIPLY); checkHaptic(it) }
        btn_divide.setOnClickListener { calc.handleOperation(DIVIDE); checkHaptic(it) }
        btn_percent.setOnClickListener { calc.handleOperation(PERCENT); checkHaptic(it) }
        btn_power.setOnClickListener { calc.handleOperation(POWER); checkHaptic(it) }
        btn_root.setOnClickListener { calc.handleOperation(ROOT); checkHaptic(it) }

        btn_minus.setOnLongClickListener {
            calc.turnToNegative()
        }

        btn_clear.setOnClickListener { calc.handleClear(); checkHaptic(it) }
        btn_clear.setOnLongClickListener { calc.handleReset(); true }

        getButtonIds().forEach {
            it.setOnClickListener { calc.numpadClicked(it.id); checkHaptic(it) }
        }

        btn_equals.setOnClickListener {
            calc.handleEquals()
            checkHaptic(it)
            loadInterstitial()
        }
        formula.setOnLongClickListener { copyToClipboard(false) }
        result.setOnLongClickListener { copyToClipboard(true) }

        AutofitHelper.create(result)
        AutofitHelper.create(formula)
        storeStateVariables()
        updateViewColors(calculator_holder, config.textColor)
        checkWhatsNewDialog()
        checkAppOnSDCard()

        val mBanner: HyBidAdView = findViewById(R.id.hybid_banner)

        loadBanner(mBanner);

    }

    private fun loadBanner(mBanner : HyBidAdView) {
        mBanner.load("2", object : PNAdView.Listener {
            override fun onAdLoaded() {
                Log.d("ADV", "onAdLoaded")
            }
            override fun onAdLoadFailed(error: Throwable) {
                Log.e("ADV", "onAdLoadedFailed", error)
            }
            override fun onAdImpression() {}
            override fun onAdClick() {}
        })
    }

    private fun loadInterstitial() {
        mInterstitial = HyBidInterstitialAd(this, "3", object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                mInterstitial?.show()
            }

            override fun onInterstitialLoadFailed(error: Throwable) {}
            override fun onInterstitialImpression() {}
            override fun onInterstitialDismissed() {}
            override fun onInterstitialClick() {}
        })
        mInterstitial?.load()
    }

    override fun onResume() {
        super.onResume()
        if (storedTextColor != config.textColor) {
            updateViewColors(calculator_holder, config.textColor)
        }

        if (config.preventPhoneFromSleeping) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        vibrateOnButtonPress = config.vibrateOnButtonPress

        val adjustedPrimaryColor = getAdjustedPrimaryColor()
        arrayOf(btn_percent, btn_power, btn_root, btn_clear, btn_reset, btn_divide, btn_multiply, btn_plus, btn_minus, btn_equals, btn_decimal).forEach {
            it.setTextColor(adjustedPrimaryColor)
        }
    }

    override fun onPause() {
        super.onPause()
        storeStateVariables()
        if (config.preventPhoneFromSleeping) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            CalculatorDatabase.destroyInstance()
        }
        mInterstitial?.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        updateMenuItemColors(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> showHistory()
            R.id.settings -> launchSettings()
            R.id.about -> launchAbout()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun storeStateVariables() {
        config.apply {
            storedTextColor = textColor
        }
    }

    private fun checkHaptic(view: View) {
        if (vibrateOnButtonPress) {
            view.performHapticFeedback()
        }
    }

    private fun showHistory() {
        HistoryHelper(this).getHistory {
            if (it.isEmpty()) {
                toast(R.string.history_empty)
            } else {
                HistoryDialog(this, it, calc)
            }
        }
    }

    private fun launchSettings() {
        startActivity(Intent(applicationContext, SettingsActivity::class.java))
    }

    private fun launchAbout() {
        val licenses = LICENSE_AUTOFITTEXTVIEW

        val faqItems = arrayListOf(
            FAQItem(R.string.faq_1_title, R.string.faq_1_text),
            FAQItem(R.string.faq_1_title_commons, R.string.faq_1_text_commons),
            FAQItem(R.string.faq_4_title_commons, R.string.faq_4_text_commons),
            FAQItem(R.string.faq_2_title_commons, R.string.faq_2_text_commons),
            FAQItem(R.string.faq_6_title_commons, R.string.faq_6_text_commons)
        )

        startAboutActivity(R.string.app_name, licenses, BuildConfig.VERSION_NAME, faqItems, true)
    }

    private fun getButtonIds() = arrayOf(btn_decimal, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

    private fun copyToClipboard(copyResult: Boolean): Boolean {
        var value = formula.value
        if (copyResult) {
            value = result.value
        }

        return if (value.isEmpty()) {
            false
        } else {
            copyToClipboard(value)
            true
        }
    }

    override fun showNewResult(value: String, context: Context) {
        result.text = value
    }

    private fun checkWhatsNewDialog() {
        arrayListOf<Release>().apply {
            add(Release(18, R.string.release_18))
            add(Release(28, R.string.release_28))
            checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }

    override fun showNewFormula(value: String, context: Context) {
        formula.text = value
    }
}
