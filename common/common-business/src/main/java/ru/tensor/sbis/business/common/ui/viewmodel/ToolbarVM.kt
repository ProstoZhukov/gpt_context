package ru.tensor.sbis.business.common.ui.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.business.common.ui.base.contract.ToolbarContract
import ru.tensor.sbis.business.common.ui.base.router.BaseRouter
import ru.tensor.sbis.common.util.safeRunOnNullable
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.R as RDesign

/**
 * Вьюмодель Тулбара
 *
 * @param router роутер с базовым функционалом
 *
 * @author as.chadov
 */
open class ToolbarVM constructor(
    router: BaseRouter?
) : BaseViewModel(),
    ToolbarContract {

    override val toolbarVisibility = ObservableBoolean(true)
    override val customViewContainerVisibility = ObservableInt(View.VISIBLE)
    override val customViewLayoutId = ObservableInt(0)
    override val customViewData = ObservableField<Any?>()
    override val customViewAction = ObservableField<(() -> Unit)?>()
    override val toolbarColor = ObservableInt(0)
    override val toolbarColorAttr = ObservableInt(0)
    override val toolbarShadowVisibility = ObservableInt(View.NO_ID)
    override val toolbarAction = ObservableField<(() -> Unit)?>()

    override val leftIconShown = ObservableBoolean(true)
    override val leftIconActive = ObservableBoolean(true)
    override val leftIconColor = ObservableInt(0)
    override val leftIconText = ObservableInt(RDesign.string.design_mobile_android_arrow)
    override val leftIconAction = ObservableField {
        safeRunOnNullable(router, BaseRouter::goBack)
    }
    override val textColor = ObservableInt(0)

    override val title = ObservableField<CharSequence>("")
    override val subtitle = ObservableField<CharSequence>("")
    override val disableMerging = ObservableBoolean(false)
    override val titleAction = ObservableField<(() -> Unit)?>()
    override val personId = ObservableField("")
    override val personUuid = ObservableField("")
    override val textStyle = ObservableInt(0)

    override val leftTitle = ObservableField<CharSequence>("")
    override val leftTitleAction = ObservableField<(() -> Unit)?>()
    override val rightTitle = ObservableField<CharSequence>("")
    override val rightTitleAction = ObservableField<(() -> Unit)?>()

    override val rightIconShown = ObservableBoolean(false)
    override val rightIconActive = ObservableBoolean(true)
    override val rightIconText = ObservableInt(RDesign.string.design_mobile_icon_dots_vertical)
    override val rightIconColor = ObservableField<SbisColor>(SbisColor.NotSpecified)
    override val rightIconAction = ObservableField<(() -> Unit)?>()

    override val additionalRightIcon2Shown = ObservableBoolean(false)
    override val rightIcon2Shown = ObservableBoolean(false)
    override val rightIcon2Text = ObservableInt(RDesign.string.design_mobile_icon_video)
    override val rightIcon2Color = ObservableInt(0)
    override val rightIcon2Action = ObservableField<(() -> Unit)?>()

    override val menuIconShown = ObservableBoolean(false)
    override val menuIconAction = ObservableField { anchor: View ->
        menuOpenObservable.onNext(anchor)
    }

    override fun observeMenuIconEvent(): Observable<View> = menuOpenObservable

    private var menuOpenObservable = PublishSubject.create<View>()
}