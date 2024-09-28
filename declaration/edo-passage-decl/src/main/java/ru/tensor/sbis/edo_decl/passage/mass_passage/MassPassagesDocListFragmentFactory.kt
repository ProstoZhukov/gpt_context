package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.util.UUID

/**
 * Фабрика фрагмента прикладного списка документов для массовых переходов.
 * Отображается на экране выбора перехода для предоставления пользователю информации об обрабатываемых документах
 *
 * ВНИМАНИЕ! Получить текущий фильтр для списка документов можно через интерфейс [MassPassagesDocListFilterProvider],
 * получить реализацию которого можно через плагинную систему
 *
 * ВНИМАНИЕ! Фрагмент должен содержать RecyclerView с установленным на него AppBarLayout.ScrollingViewBehavior
 * Это требуется для прокрутки панели с переходами вместе со списком документов
 *
 * @author sa.nikitin
 */
interface MassPassagesDocListFragmentFactory : Parcelable {

    /**
     * Создать экземпляр фрагмента
     *
     * @param sessionKey Идентификатор сессии массовых переходов
     * Следует передать в [MassPassagesDocListFilterProvider.getDocListFilter]
     */
    fun createDocListFragment(sessionKey: UUID): Fragment
}