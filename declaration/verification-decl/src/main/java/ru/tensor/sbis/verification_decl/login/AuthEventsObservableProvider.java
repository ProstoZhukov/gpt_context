package ru.tensor.sbis.verification_decl.login;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import ru.tensor.sbis.plugin_struct.feature.Feature;
import ru.tensor.sbis.verification_decl.login.event.AuthEvent;

/**
 * Предоставляет подписку на события авторизации.
 * Реализация на Java обеспечивает совместимость.
 *
 * @author ar.leschev
 */
public interface AuthEventsObservableProvider extends Feature {
    /**
     * Возвращает подписку на события логин/логаут.
     *
     * @return подписка на события
     */
    @NonNull
    Observable<AuthEvent> getEventsObservable();
}
