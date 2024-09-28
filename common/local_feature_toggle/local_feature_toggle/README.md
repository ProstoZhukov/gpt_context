# модуль "Управление локальными фичами"

Модуль local_feature_toggle предназначен для тестирования новой функциональности приложения. 
Он позволяет переключать функции приложения в рантайме.

## Дополнительная информация

- [ответственный Круглова Марина](https://online.sbis.ru/person/8a7248e7-b4b2-4c2e-a988-3534eab414f8)

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)

## Подключение сервиса

### Шаг 1

Добавляем в enum класс [FeatureSet](src/main/java/ru/tensor/sbis/localfeaturetoggle/data/FeatureSet.kt) через запятую
новую функциональность приложения: уникальное имя и описание.
```kotlin
 NEW_FEATURE("new_feature", "Новая фича")
```

### Шаг 2

В любой модуль приложения через DI добавляем сервис фичетогла (LocalFeatureToggleService)
- в Module
```kotlin
  @Provides
  @Scope
  fun provideLocalFeatureToggleService(context: Context): LocalFeatureToggleService {
  return LocalFeatureToggleService(context)
  }
```
- в Component
```kotlin
  interface MyComponent {
    val localFeatureToggleService: LocalFeatureToggleService
}
```

### Шаг 3

Сервис фичетогла (LocalFeatureToggleService) используем, чтобы:

- allFeatures - получить набор всех доступных фичей

- isFeatureActivated(feature: Feature) - проверить активность фичи

```kotlin
  localFeatureToggleService.isFeatureActivated(feature)
```

## Фрагмент включения/выключения нового функционала

Для удобства тестирования существует инженерный экран [LocalFeatureToggleFragment](src/main/java/ru/tensor/sbis/localfeaturetoggle/presentation/LocalFeatureToggleFragment.kt), 
на котором можно включить или отключить ту или иную функциональность. 
Чтобы его использовать в приложении, нужно выполнить несколько шагов.

### Шаг 1

В аккордеон/ННП добавляем пункт меню ФичеТогл через LocalFeatureToggleMainScreenAddonPlugin.createAddon из модуля
local_feature_toggle_main_screen_addon.

### Шаг 2

На ФичеТогл фрагменте включаем/отключаем функциональность.
В случае её включения, она будет добавлена в SharedPreference, при выключении - она будет оттуда удалена.