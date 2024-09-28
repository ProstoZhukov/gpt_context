package ru.tensor.sbis.wheel_time_picker.feature

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Получить фичу, реализующую API пикера даты и времени.
 *
 * @param [viewModelStoreOwner] Родительский [Fragment], либо [Activity],
 * чей fragment manager будет использован при показе панели.
 * @param viewModelKey задаётся в случае, если на экране используется несколько исходных точек
 * вызова компонента (например, 2 поля).
 * В этом случае, [viewModelKey] должен совпадать с параметром customTag
 * метода [ru.tensor.sbis.wheel_time_picker.feature.TimePickerFeature.showDateTimePickerDialog]
 * и других методов вызова диалога.
 */
fun getTimePickerFeature(viewModelStoreOwner: ViewModelStoreOwner, viewModelKey: String? = null)
        : TimePickerFeature = getTimePickerFeatureInternal(viewModelStoreOwner, viewModelKey)

/**
 * Получить реализацию фичи с расширенным контрактом для внутреннего использования.
 */
internal fun getTimePickerFeatureInternal(
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModelKey: String? = null
): TimePickerFeatureInternal =
    if (viewModelKey == null) {
        ViewModelProvider(viewModelStoreOwner)[TimePickerFeatureImpl::class.java]
    } else {
        ViewModelProvider(viewModelStoreOwner)[viewModelKey, TimePickerFeatureImpl::class.java]
    }
