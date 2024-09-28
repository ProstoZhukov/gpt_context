package ru.tensor.sbis.date_picker.di;

import androidx.annotation.Nullable;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.common.util.di.PerActivity;
import ru.tensor.sbis.date_picker.DatePickerPresenter;
import ru.tensor.sbis.date_picker.DayCountersRepository;
import ru.tensor.sbis.date_picker.current.CurrentPeriodSelectionPresenter;

/**
 * @author mb.kruglova
 */
@PerActivity
@Component(modules = DatePickerModule.class, dependencies = DatePickerSingletonComponent.class)
public interface DatePickerComponent {

    DatePickerPresenter getDatePickerPresenter();

    CurrentPeriodSelectionPresenter getCurrentPeriodSelectionPresenter();

    @Nullable
    DayCountersRepository getDayCountersRepository();

    @Component.Builder
    interface Builder {
        Builder datePickerSingletonComponent(DatePickerSingletonComponent datePickerSingletonComponent);

        @BindsInstance
        Builder withDayCountersRepository(@Nullable DayCountersRepository repository);

        DatePickerComponent build();
    }
}
