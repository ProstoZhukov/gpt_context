package ru.tensor.sbis.business.common.ui.base.contract

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.reactivex.Observable
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.toolbar.Toolbar

/**
 * Контракт, состояния и взаимодействия VM с компонентом тулбара
 * @see <a href="http://axure.tensor.ru/MobileStandart8/#p=шапка_2_0_">Шапка</a>
 *
 * @author as.chadov
 *
 * @property toolbarVisibility видимость toolbar
 * @property toolbarColor идентификатор цветового ресурса для заливки тулбара
 * @property toolbarColorAttr идентификатор атрибут с id цветового ресурса для заливки тулбара. Приоритетнее [toolbarColor]
 * @property toolbarShadowVisibility видимость тени toolbar. По умолчанию не указано
 * @property toolbarAction действие по клику на весь тулбар
 *
 * @property customViewContainerVisibility видимость кастомного блока toolbar (используем для заголовков)
 * @property customViewLayoutId идентификатор макета кастомной вью для тулбара [Toolbar.customViewContainer]
 * @property customViewData модель данных для кастомной вью [customViewLayoutId]
 * @property customViewAction действие по клику на вью [customViewLayoutId], когда использование [customViewData] избыточно
 *
 * @property leftIconShown видимость левой иконки
 * @property leftIconActive активность левой иконки, (для неактивного цвета нужно определить селектор с цветом для [textColor] c android:state_enabled="false"
 * @property leftIconText шрифт для левой иконки
 * @property leftIconAction callback левой иконки
 *
 * @property leftTitle текст toolbar-заголовка с левого края в [Toolbar]
 * @property leftTitleAction callback заголовка с левого края в [Toolbar]
 * @property rightTitle текст toolbar-заголовка с правого края в [Toolbar]
 * @property rightTitleAction callback заголовка с правого края в [Toolbar]
 * @property titleTailIcon текст иконки в конце заголовка не подлежащей обрезанию в [Toolbar]
 * @property textColor идентификатор цветового ресурса для текста в [Toolbar] или [SbisTitleView] (заголовки, иконки)
 * @property titleReducible true если заголовок может быть уменьшен для вмещения текста в [Toolbar] или [SbisTitleView]
 *
 * Свойства специфичные для [SbisTitleView] вложенного в [Toolbar]
 * @property title текст [SbisTitleView]-заголовка
 * @property subtitle текст [SbisTitleView]-подзаголовка
 * @property titleAction callback заголовка и подзаголовка в [SbisTitleView]
 * @property disableMerging true если отключено возможное слияние заголовока и подзаголовка в [SbisTitleView]
 *
 * @property rightIconShown видимость правой иконки
 * @property rightIconActive активность правой иконки, (для неактивного цвета нужно определить селектор с цветом для [textColor] c android:state_enabled="false"
 * @property rightIconText шрифт для правой иконки
 * @property rightIconColor цвет для правой иконки
 * @property rightIconAction callback правой иконки
 *
 * @property additionalRightIcon2Shown видимость для дополнительной правкой кнопки. Нужно для отображения сразу двух кнопок справа
 * @property rightIcon2Shown видимость второй правой иконки
 * @property rightIcon2Text текст (текстовая иконка) для второй правой иконки
 * @property rightIcon2Color цвет для второй правой кнопки
 * @property rightIcon2Action callback второй правой иконки
 *
 * @property menuIconShown видимость иконки спинера (меню)
 * @property menuIconAction callbacks нажатия на иконку меню
 *
 * @property personId идентификатор лица, фото которого требуется отображать
 * @property personUuid идентификатор лица, фото которого требуется отображать
 */
interface ToolbarContract {

    val toolbarVisibility: ObservableBoolean
    val toolbarColor: ObservableInt
    val toolbarColorAttr: ObservableInt
    val toolbarShadowVisibility: ObservableInt
    val toolbarAction: ObservableField<(() -> Unit)?>

    //region Левая иконка
    val leftIconShown: ObservableBoolean
    val leftIconActive: ObservableBoolean
    val leftIconText: ObservableInt
    val leftIconColor: ObservableInt
    val leftIconAction: ObservableField<(() -> Unit)?>
    //endregion Левая иконка

    //region Custom View
    val customViewContainerVisibility: ObservableInt
    val customViewLayoutId: ObservableInt
    val customViewData: ObservableField<Any?>
    val customViewAction: ObservableField<(() -> Unit)?>
    //endregion Custom View

    // region Заголовки используемые в тулбаре [Toolbar]. Обычный или уменьшеный заголовок
    val leftTitle: ObservableField<CharSequence>
    val leftTitleAction: ObservableField<(() -> Unit)?>
    val rightTitle: ObservableField<CharSequence>
    val rightTitleAction: ObservableField<(() -> Unit)?>
    val titleTailIcon: ObservableField<CharSequence>
        get() = ObservableField("")
    val textColor: ObservableInt
    val titleReducible: ObservableBoolean
        get() = ObservableBoolean(false)
    // endregion Заголовки используемые в тулбаре [Toolbar]. Обычный или уменьшеный заголовок

    // region Расширеный заголовок с подзаголовком и изображением в SbisTitleView
    val title: ObservableField<CharSequence>
    val subtitle: ObservableField<CharSequence>
    val titleAction: ObservableField<(() -> Unit)?>
    val personId: ObservableField<String>
    val personUuid: ObservableField<String>
    val disableMerging: ObservableBoolean
        get() = ObservableBoolean(false)
    val textStyle: ObservableInt
    // endregion Расширеный заголовок с подзаголовком и изображением в SbisTitleView

    //region Первая правая иконка
    val rightIconShown: ObservableBoolean
    val rightIconActive: ObservableBoolean
    val rightIconText: ObservableInt
    val rightIconColor: ObservableField<SbisColor>
    val rightIconAction: ObservableField<(() -> Unit)?>
    //endregion Первая правая иконка

    //region Вторая правая иконка
    val additionalRightIcon2Shown: ObservableBoolean
    val rightIcon2Shown: ObservableBoolean
    val rightIcon2Text: ObservableInt
    val rightIcon2Color: ObservableInt
    val rightIcon2Action: ObservableField<(() -> Unit)?>
    //endregion Вторая правая иконка

    //region Меню
    val menuIconShown: ObservableBoolean
    val menuIconAction: ObservableField<((anchor: View) -> Unit)?>

    /**
     * Предоставляет [Observable] события нажатия на иконку спиннера
     * @return [Observable] события клика спиннера, содержащего якорь для привязки меню
     */
    fun observeMenuIconEvent(): Observable<View>
    //endregion Меню
}