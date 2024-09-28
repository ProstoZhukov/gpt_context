@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.design.view.input.searchinput.util

import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.utils.provideViewLocation
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Контекст показа вью поиска
 *
 * Вспомогательная сущность для хранения мета-информации о вью.
 *
 * Для хранения информации о месте использования использован подход хранения двух идентификаторов - основного и второстепенного.
 * Основной идентификатор хранит путь до фрагмента, в котором используется данный компонент,
 * второстепенный - хранит идентификатор вкладки в [ToolbarTabLayout], либо [ResourcesCompat.ID_NULL] если реестр не подразумевает вкладок
 * @property primaryLocationId - идентификатор, представляющий из себя путь внутри иерархии фрагментов, ведущий до данной вью
 *
 * @author ma.kolpakov
 */
class SearchInputContext private constructor(private val primaryLocationId: String) : Parcelable {

    /**
     * Идентификатор, представляющий из себя полный путь до вью, к которой принадлежит данный контекст
     */
    private var fullLocationId = primaryLocationId

    /**
     * Конструктор контекста показа вью поиска
     * Принимает на вход [SearchInput], вычисляет его местоположение в иерархии приложения и хранит эти данные.
     */
    constructor(targetView: SearchInput) : this(provideViewLocation(targetView).orEmpty())

    constructor(parcel: Parcel) : this(parcel.readString().orEmpty())

    /**
     * Получить строковое представление пути в иерархии до экземпляра [SearchInput], который держит данный [SearchInputContext]
     */
    fun getViewLocationInApplication() = fullLocationId

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(primaryLocationId)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SearchInputContext> {

        const val TAB_PREFIX = "; current tab : "

        override fun createFromParcel(parcel: Parcel): SearchInputContext {
            return SearchInputContext(parcel)
        }

        override fun newArray(size: Int): Array<SearchInputContext?> {
            return arrayOfNulls(size)
        }
    }
}