package ru.tensor.sbis.business.common.ui.bind_adapter

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/**
 * Скрытие View
 *
 * @param isGone true - скрыть, false - отобразить
 */
@BindingAdapter("isGone")
internal fun View.setIsGone(isGone: Boolean) {
    visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("visibility")
fun View.isVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("isNotGone")
fun View.isNotGone(@BoolRes isVisible: Int) {
    if (isVisible != 0) {
        isNotGone(resources.getBoolean(isVisible), 0, 0, false)
    }
}

/**
 * Data Binding адаптер для настройки видимости вью в зависимости от нескольких условий
 *
 * @param isVisible опциональное состояние видимости View
 * @param isForciblyGoneRes опциональный ресурс для принудительного скрытия View (настройка под конфигурацию экрана)
 * @param isForciblyGoneAttr опциональный ресурс для скрытия View. Задает минимальную ширину экрана, меньше которой
 * стоит скрывать [View]
 * @param isInvisibleIfNotGone должен ли View быть видим [View.VISIBLE] если размещен в разметке (не [View.GONE]),
 * (настройка под временное скрытие View например во время прогресса)
 */
@BindingAdapter(
    value = ["isNotGone", "isForciblyGoneRes", "isForciblyGoneAttr", "isInvisibleIfNotGone"],
    requireAll = false
)
fun View.isNotGone(
    isVisible: Boolean?,
    @BoolRes isForciblyGoneRes: Int,
    @AttrRes isForciblyGoneAttr: Int,
    isInvisibleIfNotGone: Boolean
) {
    var visible = isVisible
    if (isForciblyGoneRes != ID_NULL && resources.getBoolean(isForciblyGoneRes)) {
        visible = false
    }
    if (isForciblyGoneAttr != ID_NULL) {
        val attrValue = context.getDataFromAttrOrNull(isForciblyGoneAttr)
        val screenWidthDp = resources.configuration.screenWidthDp
        if (attrValue != null && screenWidthDp < attrValue) {
            visible = false
        }
    }
    this.visibility = when {
        visible == null || visible.not() -> View.GONE
        isInvisibleIfNotGone -> View.INVISIBLE
        else -> View.VISIBLE
    }
}
