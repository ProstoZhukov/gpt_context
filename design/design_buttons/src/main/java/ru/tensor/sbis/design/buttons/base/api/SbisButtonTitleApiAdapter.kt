package ru.tensor.sbis.design.buttons.base.api

import android.content.Context
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle

/**
 * @author ma.kolpakov
 */
internal class SbisButtonTitleApiAdapter(
    private val getTitleModel: () -> SbisButtonTitle?,
    private val setTitleModel: (SbisButtonTitle?) -> Unit,
    private val context: Context
) : SbisButtonTitleApi {

    override fun setTitle(title: CharSequence?) {
        val newTitle = getTitleModel()?.copy(text = title)
            ?: title?.let { SbisButtonTitle(it) }

        setTitleModel(newTitle)
    }

    override fun setTitleRes(titleRes: Int) =
        setTitle(context.getString(titleRes))

    override fun setTitleScaleOn(scaleOn: Boolean?) {
        val newTitle = getTitleModel()?.copy(scaleOn = scaleOn)
            ?: SbisButtonTitle(text = null, scaleOn = scaleOn)
        setTitleModel(newTitle)
    }
}