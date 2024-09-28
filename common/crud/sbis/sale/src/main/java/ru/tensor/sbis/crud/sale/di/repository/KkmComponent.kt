package ru.tensor.sbis.crud.sale.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.crud.kkm.KkmCommandWrapper
import ru.tensor.sbis.crud.sale.crud.kkm.KkmListFilter
import ru.tensor.sbis.crud.sale.crud.kkm.KkmRepository
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.sale.mobile.generated.KkmFacade
import ru.tensor.sbis.sale.mobile.generated.KkmFilter
import ru.tensor.sbis.sale.mobile.generated.KkmListResult
import ru.tensor.sbis.sale.mobile.generated.KkmModel

/**@SelfDocumented */
@Suppress("DEPRECATION")
interface KkmComponent : Feature {

    /**@SelfDocumented */
    fun getKkmFacade(): DependencyProvider<KkmFacade>

    /**@SelfDocumented */
    fun getKkmListFilter(): KkmListFilter

    /**@SelfDocumented */
    fun getKkmRepository(): KkmRepository

    /**@SelfDocumented */
    fun getKkmCommandWrapper(): KkmCommandWrapper

    /**@SelfDocumented */
    fun getKkmMapper(): BaseModelMapper<KkmModel, CashRegister>

    /**@SelfDocumented */
    fun getKkmListMapper(): BaseModelMapper<KkmListResult, PagedListResult<CashRegister>>

    /**@SelfDocumented */
    fun getKkmListCommand(): ListObservableCommand<PagedListResult<CashRegister>, KkmFilter>
}