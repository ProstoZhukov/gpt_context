package ru.tensor.sbis.richtext.span.view.table

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.design.utils.AnimationUtil
import ru.tensor.sbis.richtext.R
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import java.util.concurrent.TimeUnit

/**
 * Диалог для отображения полного содержимого таблицы без ограничений по высоте и количеству ячеек.
 *
 * @author am.boldinov
 */
class RichTableFullDialog(
    context: Context,
    layout: View
) : AppCompatDialog(context, R.style.RichTextFullScreenDialogTheme) {

    private val panelInvisible = MovablePanelPeekHeight.Percent(0f)
    private val panelVisible = MovablePanelPeekHeight.Percent(1f)

    private val disposables = CompositeDisposable()

    private val movablePanel =
        (LayoutInflater.from(context)
            .inflate(R.layout.richtext_table_full_panel, FrameLayout(context), false) as MovablePanel).apply {
            setPeekHeightList(listOf(panelInvisible, panelVisible), panelVisible)
            setOnShadowClickListener {
                peekHeight = panelInvisible
            }
            disposables += getPanelStateSubject().distinctUntilChanged()
                .skip(1)
                .filter { it == panelInvisible }
                .debounce(
                    AnimationUtil.ANIMATION_DURATION,
                    TimeUnit.MILLISECONDS,
                    AndroidSchedulers.mainThread()
                ) // wait for alpha animation
                .subscribe {
                    cancel()
                }
            layout.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val scrollView = NestedScrollView(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                addView(layout)
            }
            contentContainer?.addView(scrollView)
        }

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setContentView(movablePanel)
        setOnDismissListener {
            disposables.dispose()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        movablePanel.peekHeight = panelInvisible
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            WindowCompat.getInsetsController(this, movablePanel).hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}