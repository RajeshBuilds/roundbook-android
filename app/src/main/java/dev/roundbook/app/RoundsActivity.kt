package dev.roundbook.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class RoundsActivity : Activity() {
    private enum class Filter(val labelRes: Int, val emptyRes: Int) {
        OPEN(R.string.filter_open, R.string.none_open),
        COMPLETED(R.string.filter_completed, R.string.none_completed),
        ALL(R.string.filter_all, R.string.none_available)
    }

    private var filter = Filter.OPEN
    private val filters = mutableMapOf<Filter, Button>()
    private lateinit var summary: TextView
    private lateinit var cards: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filter = Filter.entries.firstOrNull { it.name == savedInstanceState?.getString("filter") } ?: Filter.OPEN
        val root = page()
        val scroll = ScrollView(this)
        root.addView(scroll, MATCH_PARENT, MATCH_PARENT)
        val content = column().apply { setPadding(dp(20), dp(24), dp(20), dp(24)) }
        scroll.addView(content)

        content.stack(label(getString(R.string.app_name), 15f, Colors.green, bold = true))
        content.stack(label(getString(R.string.rounds_title), 32f, Colors.ink, bold = true), 6)
        summary = label("", 16f, Colors.secondary)
        content.stack(summary, 4)

        val bar = LinearLayout(this)
        Filter.entries.forEachIndexed { index, option ->
            val button = actionButton(getString(option.labelRes)).apply {
                setPaddingRelative(dp(4), dp(6), dp(4), dp(6))
                setOnClickListener {
                    filter = option
                    render()
                }
            }
            filters[option] = button
            bar.addView(button, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
                if (index > 0) marginStart = dp(6)
            })
        }
        content.stack(bar, 20)
        cards = column()
        content.stack(cards, 6)
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("filter", filter.name)
        super.onSaveInstanceState(outState)
    }

    private fun render() {
        val inspections = InspectionStore.get(this).all()
        val completed = inspections.count { it.completed }
        summary.text = getString(R.string.rounds_summary, inspections.size - completed, completed)
        filters.forEach { (option, button) ->
            val selected = option == filter
            button.isSelected = selected
            button.setTextColor(if (selected) Color.WHITE else Colors.green)
            button.background = touchSurface(if (selected) Colors.green else Color.WHITE, 12, Colors.line)
        }

        val visible = inspections.filter {
            when (filter) {
                Filter.OPEN -> !it.completed
                Filter.COMPLETED -> it.completed
                Filter.ALL -> true
            }
        }
        cards.removeAllViews()
        for (inspection in visible) cards.stack(inspectionCard(inspection), 10)
        if (visible.isEmpty()) cards.stack(label(getString(filter.emptyRes), 16f, Colors.secondary), 24)
    }

    private fun inspectionCard(inspection: Inspection) = column().apply {
        background = touchSurface(Color.WHITE, 16, Colors.line)
        setPadding(dp(16), dp(14), dp(16), dp(14))
        stack(label(inspection.title, 19f, Colors.ink, bold = true))
        stack(label(inspection.location, 15f, Colors.secondary), 2)
        val badge = statusBadge(inspection.completed)
        stack(badge, 10, WRAP_CONTENT)
        contentDescription = listOf(inspection.title, inspection.location, badge.text).joinToString(", ")
        isFocusable = true
        setOnClickListener {
            startActivity(Intent(this@RoundsActivity, InspectionActivity::class.java)
                .putExtra(InspectionActivity.EXTRA_ID, inspection.id))
        }
    }
}
