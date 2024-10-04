package ru.tensor.sbis.design.profile.person.controller

import android.view.View
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.imageview.drawer.ShapedDrawer
import ru.tensor.sbis.design.profile.person.PERSON_VIEW_DEFAULT_CORNER_RADIUS
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import java.util.UUID

/**
 * API компонента [PersonView].
 *
 * @author us.bessonov
 */
interface PersonViewApi {

    /**
     * Текущий используемый размер фото.
     */
    val photoSize: PhotoSize

    /**
     * Задаёт отображаемые данные.
     * Использование [DepartmentData] допускается только для отображения заглушки подразделения (без указания
     * [DepartmentData.persons]).
     */
    fun setData(data: PhotoData)

    /**
     * При отсутствии фото, устанавливает заглушку с инициалами.
     * Если информация о firstName и secondName отсутствует, устанавливается заглушка по умолчанию.
     *
     * @param lastName String - фамилия персоны.
     * @param firstName String - имя персоны.
     * @param uuid UUID - уникальный идентификатор персоны.
     * @param masterImageThumbUrl String - превью фото персоны.
     */
    fun setPersonImage(lastName: String, firstName: String, uuid: UUID, masterImageThumbUrl: String)

    /**
     * Программная установка размера фото.
     * Размер рекомендуется задавать через атрибут [R.attr.PersonView_size]. При установке из кода, данный метод должен
     * быть вызван до установки данных.
     * По умолчанию используется [PhotoSize.UNSPECIFIED] - размеры из [View.getLayoutParams]. В этом случае необходимо
     * явно указать желаемое значение в `layout_width` и `layout_height` (оба параметра обязательны).
     */
    fun setSize(size: PhotoSize)

    /** @SelfDocumented */
    fun setShape(shape: Shape)

    /**
     * Радиус скругления углов. Применяется только если указана квадратная форма ([Shape.SQUARE]).
     *
     * @see [R.attr.PersonView_shape]
     */
    fun setCornerRadius(radius: BorderRadius? = PERSON_VIEW_DEFAULT_CORNER_RADIUS)

    /**
     * Установка произвольного способа применения специфичной формы для изображения.
     */
    fun setShapedDrawer(drawer: ShapedDrawer)

    /**
     * Должен ли отображаться статус активности.
     * Статус будет отображаться для значения [PhotoData.uuid].
     * По умолчанию, статус активности не отображается.
     *
     * @param displayOfflineHomeStatus Должен ли отображаться индикатор статуса [ActivityStatus.OFFLINE_HOME].
     */
    fun setHasActivityStatus(hasActivityStatus: Boolean, displayOfflineHomeStatus: Boolean = false)

    /** @SelfDocumented */
    fun setClickListener(onClick: (UUID?) -> Unit)

    /**
     * Сохранить имя пользователя, соответствующего иконке саггеста, чтобы потом его добавить в атрибут text.
     */
    fun setFullNameForNodeInfo(personFullName: String)
}