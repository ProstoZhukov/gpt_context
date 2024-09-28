package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import java.util.UUID;

/**
 * Команда чтения по {@link UUID}
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface ReadObservableCommand<ENTITY> extends BaseReadObservableCommand<ENTITY, UUID> {
}
