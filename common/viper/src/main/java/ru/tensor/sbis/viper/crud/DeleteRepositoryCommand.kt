package ru.tensor.sbis.viper.crud

import ru.tensor.sbis.mvp.interactor.crudinterface.command.DeleteObservableCommand

/**
 * Интерфейс для реализации команды DELETE из CRUD.
 *
 * @author ga.malinskiy
 */
interface DeleteRepositoryCommand<ENTITY> :
        DeleteObservableCommand