@file:JvmName("FloatingSnackBar")

package ru.tensor.sbis.design_notification.snackbar

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.Snackbar
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design_notification.R
import java.util.*
import ru.tensor.sbis.design.R as RDesign

/**
 * Показывает [Snackbar] с закруглёнными краями, отображаемый над ННП.
 *
 * @param view View, на основе которого определяется контейнер, в котором будет отображаться [Snackbar].
 * @param messageResId id ресурса отображаемого сообщения.
 * @param actionResId id ресурса текста на кнопке.
 * @param action действие по клику на кнопку.
 * @param callback обработчик событий показа и скрытия [Snackbar]'а.
 * @param showInFirstCoordinatorLayoutInHierarchyIfPresent если true, то в качестве контейнера будет использован
 * первый найденный в иерархии [CoordinatorLayout], в противном случае будет использован ближайший предок [view].
 */
@SuppressLint("WrongConstant")
@JvmOverloads
fun showFloatingSnackbar(
    view: View,
    @StringRes
    messageResId: Int,
    @StringRes
    actionResId: Int = 0,
    action: () -> Unit = { },
    callback: Snackbar.Callback? = null,
    showInFirstCoordinatorLayoutInHierarchyIfPresent: Boolean = true
): Snackbar {
    fun getSize(@DimenRes size: Int) = view.resources.getDimensionPixelSize(size)

    val height = getSize(R.dimen.design_notification_floating_snackbar_height)
    val marginHorizontal = getSize(R.dimen.design_notification_floating_snackbar_margin_horizontal)
    val paddingBottom = getSize(R.dimen.design_notification_floating_snackbar_margin_bottom)
    val paddingHorizontal = getSize(R.dimen.design_notification_floating_snackbar_padding_horizontal)
    val elevation = view.resources.getDimension(RDesign.dimen.elevation_high)

    val targetView = view
        .takeUnless { showInFirstCoordinatorLayoutInHierarchyIfPresent }
        ?: (view.rootView as? ViewGroup)?.tryFindFirstCoordinatorLayout()
        ?: view

    Snackbar.make(targetView, messageResId, SNACKBAR_SHOW_DURATION_MS).apply {
        configureView(height, paddingHorizontal, marginHorizontal, paddingBottom, elevation)
        configureMessageView(paddingHorizontal)
        configureActionView()

        setActionTextColor(ContextCompat.getColor(context, RDesign.color.text_color_link_1))

        setText(messageResId)

        if (actionResId != 0) {
            setAction(actionResId) { action() }
        }

        callback?.let {
            addCallback(it)
        }

        show()
        return this
    }
}


@SuppressLint("RestrictedApi")
private fun Snackbar.configureView(
    viewHeight: Int,
    paddingHorizontal: Int,
    marginHorizontal: Int,
    paddingBottom: Int,
    elevation: Float
) {
    with(view as Snackbar.SnackbarLayout) {
        background = ContextCompat.getDrawable(context, R.drawable.design_notification_rounded_corners_snackbar_bg)
        setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
        ViewCompat.setElevation(this, elevation)
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = marginHorizontal
            marginEnd = marginHorizontal
        }
        getChildAt(0).updateLayoutParams {
            height = viewHeight
        }
    }
}

private fun Snackbar.configureMessageView(paddingHorizontal: Int) {
    with(view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)) {
        setTextColor(ContextCompat.getColor(context, RDesign.color.palette_color_white1))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(RDesign.dimen.size_body2_scaleOff))
        setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
        setLines(1)
    }
}

private fun Snackbar.configureActionView() {
    with(view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)) {
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(RDesign.dimen.size_body2_scaleOff)
        )
        typeface = TypefaceManager.getRobotoMediumFont(context)
    }
}

private fun ViewGroup.tryFindFirstCoordinatorLayout(): CoordinatorLayout? {
    val queue: Queue<View> = LinkedList()
    queue.add(this)

    while (queue.isNotEmpty()) {
        when (val view = queue.poll()) {
            is CoordinatorLayout -> return view
            is ViewGroup -> for (i in 0 until view.childCount) {
                queue.add(view.getChildAt(i))
            }
        }
    }
    return null
}

private const val SNACKBAR_SHOW_DURATION_MS = 4000