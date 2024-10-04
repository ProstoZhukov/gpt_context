package ru.tensor.sbis.design.profile.personcollagelist.controller

import androidx.annotation.IntRange
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonCollageLineViewPool
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize

/**
 * API компонента [PersonCollageLineView].
 *
 * @author us.bessonov
 */
interface PersonCollageLineViewApi {

    /**
     * Задаёт список отображаемых фото.
     */
    fun setDataList(dataList: List<PhotoData>)

    /**
     * Программная установка размера фото в коллаже.
     * По умолчанию используется [PhotoSize.UNSPECIFIED] - размер фото соответствует высоте коллажа. Размер
     * рекомендуется задавать через атрибут [R.attr.PersonCollageLineView_size]. При использовании данного метода, он
     * должен быть вызван до установки данных.
     */
    fun setSize(size: PhotoSize)

    /**
     * Задаёт максимальное число видимых фото.
     * По умолчанию число ограничено только доступной шириной. При наличии скрытых фото, их число отображается в
     * счётчике.
     *
     * @throws IllegalArgumentException если [count] меньше 1
     */
    fun setMaxVisibleCount(@IntRange(from = 1L) count: Int)

    /**
     * Задаёт общее число элементов.
     * Значение будет учитываться при формировании счётчика, если оно превышает число элементов, заданных посредством
     * [setDataList].
     */
    fun setTotalCount(count: Int)

    /**
     * Задаёт пул view элементов коллажа, чтобы избежать лишних пересозданий элементов при использовании коллажа в
     * списке.
     */
    fun setViewPool(pool: PersonCollageLineViewPool)

    /**
     * Возвращает минимальную ширину, требуемую для отображения во View заданного числа элементов (единственного фото и
     * счётчика с числом оставшихся).
     *
     * @throws IllegalArgumentException если [count] меньше 0
     */
    @Px
    fun getMinRequiredWidth(@IntRange(from = 0L) count: Int): Int
}