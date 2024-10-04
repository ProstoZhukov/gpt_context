package ru.tensor.sbis.design_splash

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design_splash.databinding.DesignSplashViewBinding

/**
 * Вью статуса загрузки приложения.
 *
 * @author av.krymov
 */
class SplashLoadingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = DesignSplashViewBinding.inflate(LayoutInflater.from(context), this)

    private val statusTextView: SbisTextView
        get() = binding.appInitStatus

    init {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    /**
     * Задать [SharedFlow] в качестве поставщика статуса загрузки приложения
     */
    fun setStatusFlow(flow: SharedFlow<String>) = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(RESUMED) {
                flow.collect { setStatusText(it) }
            }
        }
    }

    /**
     * Установить текст для статуса загрузки приложения
     */
    fun setStatusText(status: String) {
        statusTextView.text = status
    }
}