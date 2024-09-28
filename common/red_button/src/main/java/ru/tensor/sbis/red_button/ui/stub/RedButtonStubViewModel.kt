package ru.tensor.sbis.red_button.ui.stub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon

/**
 * Вью модель для [RedButtonStubActivity]
 * @property preferencesInteractor интерактор для работы с [SharedPrefecences]
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonStubViewModel @Inject constructor(
    private val preferencesInteractor: RedButtonPreferencesInteractor
) : ViewModel() {

    private val _stubContent = MutableLiveData<StubViewContent>()

    /** Контент заглушки */
    val stubContent: LiveData<StubViewContent> = _stubContent

    /**@SelfDocumented */
    override fun onCleared() {
        super.onCleared()
        preferencesInteractor.clearStubPreference()
    }

    /**
     * Обновить значение контента заглушки в записимости от типа [RedButtonStubType]
     */
    fun refreshStubContent(stubType: RedButtonStubType) {
        val titleID = when (stubType) {
            RedButtonStubType.CLOSE_STUB -> R.string.red_button_stub_title_close
            RedButtonStubType.OPEN_STUB -> R.string.red_button_stub_title_open
            RedButtonStubType.NO_STUB -> RCommon.string.common_unknown_error
        }

        _stubContent.value = ImageStubContent(
            imageType = StubViewCase.SBIS_ERROR.imageType,
            messageRes = titleID,
            detailsRes = R.string.red_button_stub_subtitle
        )

    }
}