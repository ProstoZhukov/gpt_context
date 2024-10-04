package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.createRepository
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractor
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractorImpl
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntityFactory
import ru.tensor.sbis.design.selection.ui.utils.enableRecentSelectionCaching
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.list.base.domain.ListInteractorImpl
import ru.tensor.sbis.list.base.domain.boundary.Repository

/**
 * Модуль для предоставления реализации [SelectionListInteractor]
 *
 * @author ma.kolpakov
 */
@Module
internal class InteractorModule {

    @Provides
    @SelectionListScreenScope
    fun providersRepository(
        entityFactory: SelectionListScreenEntityFactory<Any, Any, Any>,
        serviceWrapper: ServiceWrapper<Any, Any>?,
        arguments: Bundle
    ): Repository<SelectionListScreenEntity<Any, Any, Any>, Any> =
        createRepository(entityFactory, serviceWrapper, arguments)

    @Provides
    @SelectionListScreenScope
    fun provideListInteractor(
        repository: Repository<SelectionListScreenEntity<Any, Any, Any>, Any>,
        fragment: Fragment,
        arguments: Bundle
    ): SelectionListInteractor<Any, Any, Any, SelectionListScreenEntity<Any, Any, Any>> =
        SelectionListInteractorImpl(
            ListInteractorImpl(repository),
            fragment.lifecycle,
            arguments.enableRecentSelectionCaching
        )
}