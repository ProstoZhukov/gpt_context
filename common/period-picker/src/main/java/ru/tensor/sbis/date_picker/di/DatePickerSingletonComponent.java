package ru.tensor.sbis.date_picker.di;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.common.rx.RxBus;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.date_picker.MonthMarkersRepository;
import ru.tensor.sbis.date_picker.free.DatePickerRepository;
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerProvider;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * @author mb.kruglova
 */
@Singleton
@Component
public interface DatePickerSingletonComponent extends Feature {

    @NonNull
    RxBus getBus();

    @NonNull
    ResourceProvider getResourceProvider();

    @Nullable
    DatePickerRepository historyRepository();

    @Nullable
    MonthMarkersRepository getMonthMarkersRepository();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder withResourceProvider(@NonNull ResourceProvider resourceProvider);

        @BindsInstance
        Builder withDatePickerRepository(@Nullable DatePickerRepository datePickerRepository);

        @BindsInstance
        Builder withMonthMarkersRepository(@Nullable MonthMarkersRepository monthMarkersRepository);

        @BindsInstance
        Builder withBus(@NonNull RxBus bus);

        @BindsInstance
        Builder withEventManagerProvider(@NonNull EventManagerProvider eventManagerProvider);

        DatePickerSingletonComponent build();

    }
}
