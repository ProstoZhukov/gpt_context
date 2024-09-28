package ru.tensor.sbis.date_picker

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.date_picker.di.*
import ru.tensor.sbis.date_picker.feature.DatePickerFeature
import ru.tensor.sbis.date_picker.feature.DatePickerFeatureImpl
import ru.tensor.sbis.date_picker.free.DatePickerRepository
import ru.tensor.sbis.date_picker.free.DatePickerService
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerProvider
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин для компонента выбора дат.
 *
 * @author kv.martyshenko
 */
object DatePickerPlugin : BasePlugin<Unit>(), DatePickerComponentHolder {
    private lateinit var singletonComponent: DatePickerSingletonComponent

    /** @SelfDocumented */
    internal lateinit var component: DatePickerComponent

    private lateinit var commonSingletonComponent: FeatureProvider<CommonSingletonComponent>
    private lateinit var eventManagerProvider: FeatureProvider<EventManagerProvider>
    private var datePickerService: FeatureProvider<DatePickerService>? = null
    private var monthMarkersRepository: FeatureProvider<MonthMarkersRepository>? = null
    private val datePickerFeature by lazy { DatePickerFeatureImpl() }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(DatePickerFeature::class.java) { datePickerFeature },
        FeatureWrapper(DatePickerSingletonComponent::class.java) { singletonComponent}
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponent = it }
        .require(EventManagerProvider::class.java) { eventManagerProvider = it }
        .optional(DatePickerService::class.java) { datePickerService = it }
        .optional(MonthMarkersRepository::class.java) { monthMarkersRepository = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        singletonComponent = DaggerDatePickerSingletonComponent
            .builder()
            .withBus(commonSingletonComponent.get().rxBus)
            .withResourceProvider(commonSingletonComponent.get().resourceProvider).apply {
                datePickerService?.let {
                    withDatePickerRepository(
                        DatePickerRepository(
                            it.get(),
                            commonSingletonComponent.get().resourceProvider
                        )
                    )
                }
                monthMarkersRepository?.let {
                    withMonthMarkersRepository(it.get())
                }
            }
            .withEventManagerProvider(eventManagerProvider.get())
            .build()

        component = DaggerDatePickerComponent.builder()
            .datePickerSingletonComponent(singletonComponent)
            .build()
    }

    override fun getDatePickerComponent(): DatePickerComponent {
        return component
    }
}