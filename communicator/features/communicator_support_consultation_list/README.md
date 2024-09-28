# Модуль реестра обращений в тех. поддержку

Модуль содержит реализацию интерфейсной части реестра обращений

## Дополнительная информация

- [ответственный Петров Р. А.](https://online.sbis.ru/person/6246f9f1-03a4-4d02-aecf-29fcc9a0d0ff)
- [ссылка на макет](http://axure.tensor.ru/mobile_crm/#g=1&p=служба_поддержки_клиента__провал_&c=1)

# Подключение.

Для добавления модуля коммуникатор в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должны быть подключены модули коммуникатора из
файла `$communicator_dir/settings.gradle` а так же все модули, от которых они зависят:


```
include ':communicator_support_consultation_list'
project(':communicator_support_consultation_list').projectDir = new File(settingsDir, "$communicator_dir/features/communicator_support_consultation_list")
```


## 2. Добавить зависимость в список плагинов приложения:
```
pluginManager.registerPlugins(
...
    SupportChannelListPlugin
)
```


## 3. Использовать фичу:
```
object SomeMyPlugin : BasePlugin<Unit>() {
    private var supportConsultationListFragmentFactory: FeatureProvider<SupportConsultationListFragmentFactory>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SomeMyFeature::class.java) { ... }
    )

    override val dependency: Dependency = Dependency.Builder()
        .optional(SupportConsultationListFragmentFactory::class.java) { supportConsultationListFragmentFactory = it }
        .build()

    override val customizationOptions: Unit = Unit

    ...    
}
```

## 4. Использование фичи для получения фрагмента
```
/**
 * Фабрика хост фрагмента реестра обращений в поддержку
 */
interface SupportConsultationListFragmentFactory : Feature {

    /**
     * Получить фрагмент
     * @param идентификатор канала
     */
    fun getSupportRequestsListFragmentFragment(channelId: UUID): Fragment
}
```

## 5. Примененить тему
В теме приложения установить стили
```
<style name="SomeAppTheme" parent="AppTheme">
        ...
        <item name="communicatorSupportConsultationTheme">@style/CommunicatorSupportConsultationTheme</item>
        ...
    </style>
```