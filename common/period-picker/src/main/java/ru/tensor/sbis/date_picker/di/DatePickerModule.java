package ru.tensor.sbis.date_picker.di;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.rx.RxBus;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.date_picker.DatePickerInteractor;
import ru.tensor.sbis.date_picker.DatePickerPresenter;
import ru.tensor.sbis.date_picker.DayCountersRepository;
import ru.tensor.sbis.date_picker.MonthMarkersRepository;
import ru.tensor.sbis.date_picker.NoDayCountersRepository;
import ru.tensor.sbis.date_picker.PeriodHelper;
import ru.tensor.sbis.date_picker.Validator;
import ru.tensor.sbis.date_picker.current.CurrentPeriodSelectionPresenter;
import ru.tensor.sbis.date_picker.current.CurrentPeriodVmFactory;
import ru.tensor.sbis.date_picker.free.DatePickerRepository;
import ru.tensor.sbis.date_picker.items.CalendarVmFactory;
import ru.tensor.sbis.date_picker.selection.SelectionStrategyFactory;
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerProvider;
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerServiceSubscriberFactory;

/**
 * @author mb.kruglova
 */
@Module
public class DatePickerModule {

    @NonNull
    @Provides
    DatePickerPresenter provideDatePickerPresenter(@NonNull DatePickerInteractor interactor,
                                                   @NonNull RxBus rxBus,
                                                   @NonNull ResourceProvider resourceProvider,
                                                   @NonNull PeriodHelper periodHelper,
                                                   @NonNull CalendarVmFactory calendarVmFactory,
                                                   @NonNull SelectionStrategyFactory selectionStrategyFactory,
                                                   @NonNull Validator validator,
                                                   @Nullable DayCountersRepository repository) {
        return new DatePickerPresenter(
                interactor,
                rxBus,
                resourceProvider,
                periodHelper,
                calendarVmFactory,
                selectionStrategyFactory,
                validator,
                repository == null ? NoDayCountersRepository.INSTANCE : repository
        );
    }

    @NonNull
    @Provides
    DatePickerInteractor provideDatePickerInteractor(@Nullable DatePickerRepository repository,
                                                     @Nullable MonthMarkersRepository monthMarkersRepository) {
        return new DatePickerInteractor(repository, monthMarkersRepository);
    }

    @NonNull
    @Provides
    PeriodHelper providePeriodHelper() {
        return new PeriodHelper();
    }

    @NonNull
    @Provides
    CalendarVmFactory provideCalendarVmFactory(@NonNull ResourceProvider resourceProvider) {
        return new CalendarVmFactory(resourceProvider);
    }

    @NonNull
    @Provides
    CurrentPeriodVmFactory provideCurrentPeriodVmFactory(@NonNull ResourceProvider resourceProvider,
                                                         @NonNull PeriodHelper periodHelper) {
        return new CurrentPeriodVmFactory(resourceProvider, periodHelper);
    }

    @NonNull
    @Provides
    SelectionStrategyFactory provideSelectionStrategyFactory(@NonNull PeriodHelper periodHelper,
                                                             @NonNull Validator validator) {
        return new SelectionStrategyFactory(periodHelper, validator);
    }

    @NonNull
    @Provides
    CurrentPeriodSelectionPresenter provideCurrentPeriodSelectionPresenter(@NonNull RxBus rxBus,
                                                                           @NonNull CurrentPeriodVmFactory currentPeriodVmFactory) {
        return new CurrentPeriodSelectionPresenter(rxBus, currentPeriodVmFactory);
    }

    @NonNull
    @Provides
    EventManagerServiceSubscriberFactory provideEventManagerServiceSubscriberFactory(@NonNull EventManagerProvider eventManagerProvider) {
        return eventManagerProvider.getEventManagerServiceSubscriberFactory();
    }

    @NonNull
    @Provides
    Validator provideValidator() {
        return new Validator();
    }
}
