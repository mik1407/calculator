package net.pubnative.hybidstandalonedemo.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.hybidstandalonedemo.R
import net.pubnative.hybidstandalonedemo.activities.SimpleActivity
import net.pubnative.hybidstandalonedemo.extensions.config
import net.pubnative.hybidstandalonedemo.helpers.CalculatorImpl
import net.pubnative.hybidstandalonedemo.models.History
import com.simplemobiletools.commons.extensions.copyToClipboard
import kotlinx.android.synthetic.main.history_view.view.*

class HistoryAdapter(val activity: SimpleActivity, val items: List<History>, val calc: CalculatorImpl, val itemClick: () -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var textColor = activity.config.textColor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = activity.layoutInflater.inflate(R.layout.history_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(item: History): View {
            itemView.apply {
                item_formula.text = item.formula
                item_result.text = "= ${item.result}"
                item_formula.setTextColor(textColor)
                item_result.setTextColor(textColor)

                setOnClickListener {
                    calc.addNumberToFormula(item.result)
                    itemClick()
                }
                setOnLongClickListener {
                    activity.baseContext.copyToClipboard(item.result)
                    true
                }
            }

            return itemView
        }
    }
}
