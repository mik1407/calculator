package net.pubnative.hybidstandalonedemo.dialogs

import androidx.appcompat.app.AlertDialog
import net.pubnative.hybidstandalonedemo.R
import net.pubnative.hybidstandalonedemo.activities.SimpleActivity
import net.pubnative.hybidstandalonedemo.adapters.HistoryAdapter
import net.pubnative.hybidstandalonedemo.extensions.calculatorDB
import net.pubnative.hybidstandalonedemo.helpers.CalculatorImpl
import net.pubnative.hybidstandalonedemo.models.History
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import kotlinx.android.synthetic.main.dialog_history.view.*

class HistoryDialog() {
    constructor(activity: SimpleActivity, items: List<History>, calculator: CalculatorImpl) : this() {
        val view = activity.layoutInflater.inflate(R.layout.dialog_history, null)

        val dialog = AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok, null)
            .setNeutralButton(R.string.clear_history) { _, _ ->
                ensureBackgroundThread {
                    activity.applicationContext.calculatorDB.deleteHistory()
                    activity.toast(R.string.history_cleared)
                }
            }.create().apply {
                activity.setupDialogStuff(view, this, R.string.history)
            }

        view.history_list.adapter = HistoryAdapter(activity, items, calculator) {
            dialog.dismiss()
        }
    }
}
