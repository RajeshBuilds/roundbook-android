package dev.roundbook.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class InspectionActivity : Activity() {
    companion object {
        const val EXTRA_ID = "inspection_id"
    }

    private lateinit var inspectionId: String
    private lateinit var status: TextView
    private lateinit var toggle: Button
    private val store by lazy { InspectionStore.get(this) }
    private val onInspectionChanged: (String) -> Unit = { changedId ->
        if (changedId == inspectionId) store.find(changedId)?.let(::showStatus)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inspection = intent.getStringExtra(EXTRA_ID)?.let(store::find)
        if (inspection == null) {
            finish()
            return
        }
        inspectionId = inspection.id
        val root = page()
        val scroll = ScrollView(this)
        root.addView(scroll, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        val body = column().apply { setPadding(dp(20), dp(12), dp(20), dp(20)) }
        scroll.addView(body)

        val back = actionButton(getString(R.string.back_to_rounds)).apply { setOnClickListener { finish() } }
        body.stack(back, width = WRAP_CONTENT)
        status = statusBadge(inspection.completed)
        body.stack(status, 20, WRAP_CONTENT)
        body.stack(label(inspection.title, 30f, Colors.ink, bold = true), 6)
        body.stack(label(inspection.location, 16f, Colors.secondary), 4)
        body.stack(label(getString(R.string.field_notes), 18f, Colors.ink, bold = true), 24)
        body.stack(label(inspection.instructions, 16f, Colors.secondary), 6)
        body.stack(label(getString(R.string.inspection_checklist), 18f, Colors.ink, bold = true), 24)

        val checklist = column().apply {
            setPadding(dp(16), dp(2), dp(16), dp(2))
            background = shape(Color.WHITE, 16, Colors.line)
        }
        inspection.checkpoints.forEachIndexed { index, step ->
            if (index > 0) checklist.addView(View(this).apply { setBackgroundColor(Colors.line) }, MATCH_PARENT, dp(1))
            val row = LinearLayout(this).apply {
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(12), 0, dp(12))
            }
            val number = label((index + 1).toString(), 15f, Colors.green, bold = true)
            row.addView(number, dp(28), WRAP_CONTENT)
            row.addView(label(step, 16f, Colors.ink), LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
            checklist.stack(row)
        }
        body.stack(checklist, 8)

        toggle = actionButton("", primary = true).apply {
            setOnClickListener {
                store.find(inspectionId)?.let { store.setCompleted(it.id, !it.completed) }
            }
        }
        root.addView(toggle, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(dp(20), dp(8), dp(20), dp(12))
        })
        showStatus(inspection)
        store.observe(onInspectionChanged)
    }

    private fun showStatus(inspection: Inspection) {
        val completed = inspection.completed
        status.text = getString(if (completed) R.string.filter_completed else R.string.filter_open)
        status.background = shape(if (completed) Colors.mint else Colors.sand, 8)
        status.setTextColor(if (completed) Colors.green else Colors.amber)
        toggle.setText(if (completed) R.string.reopen_inspection else R.string.mark_completed)
    }
}
