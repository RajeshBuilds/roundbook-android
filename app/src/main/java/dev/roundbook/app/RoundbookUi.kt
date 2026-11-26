package dev.roundbook.app

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

internal object Colors {
    val paper = Color.rgb(247, 248, 244)
    val ink = Color.rgb(30, 47, 39)
    val secondary = Color.rgb(77, 94, 83)
    val green = Color.rgb(30, 86, 65)
    val mint = Color.rgb(220, 238, 225)
    val line = Color.rgb(210, 219, 210)
    val amber = Color.rgb(112, 72, 26)
    val sand = Color.rgb(251, 238, 212)
}

internal fun Context.dp(value: Int) = (value * resources.displayMetrics.density + 0.5f).toInt()

internal fun Context.column() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

internal fun LinearLayout.stack(view: View, gap: Int = 0, width: Int = MATCH_PARENT) {
    addView(view, LinearLayout.LayoutParams(width, WRAP_CONTENT).apply { topMargin = context.dp(gap) })
}

internal fun Context.label(value: CharSequence, size: Float, color: Int, bold: Boolean = false) =
    TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(Typeface.DEFAULT, Typeface.BOLD)
    }

internal fun Context.shape(fill: Int, radius: Int, border: Int? = null) =
    GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radius).toFloat()
        if (border != null) setStroke(dp(1), border)
    }

internal fun Context.touchSurface(fill: Int, radius: Int, border: Int? = null) =
    RippleDrawable(ColorStateList.valueOf(0x3069997d), shape(fill, radius, border), null)

internal fun Context.actionButton(value: CharSequence, primary: Boolean = false) =
    Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 15f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setTextColor(if (primary) Color.WHITE else Colors.green)
        stateListAnimator = null
        backgroundTintList = null
        background = touchSurface(if (primary) Colors.green else Color.WHITE, 12, if (primary) null else Colors.line)
        setPaddingRelative(dp(12), dp(6), dp(12), dp(6))
        minHeight = dp(48)
        minWidth = 0
    }

internal fun Context.statusBadge(completed: Boolean) =
    label(getString(if (completed) R.string.filter_completed else R.string.filter_open), 13f,
        if (completed) Colors.green else Colors.amber, bold = true).apply {
        setPaddingRelative(dp(10), dp(4), dp(10), dp(4))
        background = shape(if (completed) Colors.mint else Colors.sand, 8)
        gravity = Gravity.CENTER
    }

@Suppress("DEPRECATION")
internal fun Activity.page(): LinearLayout {
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    return column().apply {
        setBackgroundColor(Colors.paper)
        if (Build.VERSION.SDK_INT >= 29) isForceDarkAllowed = false
        setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets
        }
        setContentView(this)
    }
}
