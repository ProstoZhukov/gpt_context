package ru.tensor.sbis.viper.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.NO_GRAVITY
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.viper.R
import java.lang.ref.WeakReference

/**
 * Базовый класс для списоков saby_clients
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class SabyClientsListView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    AbstractListView<StubView, StubViewContent>(context, attrs, defStyle) {

    private var stubViewReference: WeakReference<StubView>? = null

    companion object {

        /**@SelfDocumented*/
        fun getDefaultSearchContent(context: Context) = getStubContent(
            ResourceProvider(context),
            titleResId = R.string.viper_no_items_found_placeholder,
            descriptionResId = R.string.viper_try_another_search
        )

        /**@SelfDocumented*/
        fun getDefaultSearchContent(resourceProvider: ResourceProvider) = getStubContent(
            resourceProvider,
            titleResId = R.string.viper_no_items_found_placeholder,
            descriptionResId = R.string.viper_try_another_search
        )

        /**@SelfDocumented*/
        fun getStubContent(
            context: Context,
            iconResId: Int = ID_NULL,
            titleResId: Int? = null,
            descriptionResId: Int? = null,
            actions: Map<Int, () -> Unit> = emptyMap()
        ) = getStubContent(ResourceProvider(context), iconResId, titleResId, descriptionResId, actions)

        /**@SelfDocumented*/
        fun getStubContent(
            resourceProvider: ResourceProvider,
            iconResId: Int = ID_NULL,
            titleResId: Int? = null,
            descriptionResId: Int? = null,
            actions: Map<Int, () -> Unit> = emptyMap()
        ): ResourceImageStubContent = ResourceImageStubContent(
            icon = iconResId,
            message = titleResId?.let { resourceProvider.getString(it) },
            details = descriptionResId?.let { resourceProvider.getString(it) },
            actions = actions
        )
    }

    /**@SelfDocumented*/
    fun setStubBackgroundColor(@ColorInt color: Int) {
        stubViewReference?.get()?.setBackgroundColor(color)
    }

    override fun createInformationView(container: ViewGroup): StubView {
        StubView(context).run {
            stubViewReference = WeakReference(this)
            return this
        }
    }

    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) {
        informationView.updateLayoutParams { (this as LayoutParams).gravity = NO_GRAVITY }
        content?.let { informationView.setContent(it) }
    }
}