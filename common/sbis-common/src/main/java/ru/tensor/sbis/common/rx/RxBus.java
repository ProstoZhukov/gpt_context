package ru.tensor.sbis.common.rx;

import static ru.tensor.sbis.common.rx.RxBusHelperKt.postEventOnEventBus;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import ru.tensor.sbis.event_bus.EventBus;
import ru.tensor.sbis.event_bus.EventBusUtilsKt;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * Created by ss.buvaylink on 22.02.2017.
 *
 * @deprecated использовать {@link ru.tensor.sbis.event_bus.EventBus}
 */
public class RxBus implements Feature {

    @NonNull
    private final Subject<Object> mBus = EventBus.INSTANCE.getBus();

    public void post(@NonNull Object event) {
        mBus.onNext(event);
        postEventOnEventBus(event);
    }

    @NonNull
    public <T> Observable<T> subscribe(@NonNull Class<T> clazz) {
        return mBus.filter(clazz::isInstance).cast(clazz);
    }

    @NonNull
    public Observable<Object> subscribe(@NonNull Class... clazzArray) {
        return mBus.filter(o -> {
            boolean result = false;
            for (Class clazz : clazzArray) {
                if (clazz.isInstance(o)) {
                    result = true;
                    break;
                }
            }
            return result;
        });
    }

}
