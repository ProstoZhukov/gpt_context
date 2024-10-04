# Компонент выбора получателей.

| Модуль                       | Ответственные                                                                          |
|------------------------------|----------------------------------------------------------------------------------------|
| [design_recipient_selection] | [Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877) |

#### Описание
Модуль содержит реализацию компонента выбора получателей (контактов/сотрудников/подразделений/лиц) на базе компонента SelectionFragment:
- RecipientSelectionFragment
- RecipientSelectionActivity

А также реализацию менеджера для работы с результатом выбора:
- RecipientSelectionResultImpl

#### Подключение
Модуль подключается к проекту в settings.gradle следующим образом:
`include ':design_recipient_selection'`
`project(':design_recipient_selection').projectDir = new File(settingsDir, 'design/design_recipient_selection')`

А также должны присутствовать все требуемые модули-зависимости.

К application-модуля в build.gradle должен быть добавлен модуль выбора получателей:
`implementation project(':design_selection')`

И его плагин в плагинную систему `RecipientSelectionPlugin`.

##### Стилизация
Стандартная тема компонента выбора получателей: `SelectionTheme`.
Атрибут для установки темы компонента выбора: `selectionTheme`.

Атрибуты стилизации:
- `Selection_topNavigationStyle` - стиль для шапки (см. атрибуты SbisTopNavigationView).
- `Selection_selectionPanelStyle` - стиль панели выбранных элементов.
- `Selection_selectionItemsTheme` - тема для ячеек невыбранных элементов списка.
- `Selection_selectedItemsTheme` - тема для ячеек выбранных элементов списка.

Компонент выбора получателей может стилизоваться двумя способами:
1) Через xml темизацию, путем указания темы компонента на уровне манифестов Application или Activity.
```xml
<style name="SelectionTheme">
    <item name="Selection_topNavigationStyle">@style/SelectionTopNavigationStyle</item>
    <item name="Selection_selectionPanelStyle">@style/SelectionPanelStyle</item>
    <item name="Selection_selectedItemsTheme">@style/SelectedItemsTheme</item>
    <item name="Selection_selectionItemsTheme">@style/SelectionItemsTheme</item>
</style>
```
2) Программно, путем указания ресурса темы в `RecipientSelectionConfig` компонента выбора получателей.
```kotlin
feature.getRecipientSelectionFragment(
    config = RecipientSelectionConfig(
        themeRes = R.style.CustomRecipientSelectionTheme
    )
)
```

#### Использование
Для использования компонента к вашему модулю необходимо подключить декларативный модуль с провайдерами компонента:
`implementation project(':communication-decl')`
В плагин вашего модуля добавить зависимость от `RecipientSelectionProvider`:
```kotlin
object SomePlugin : BasePlugin<Unit>() {
    override val dependency: Dependency = Dependency.Builder()
        .require(RecipientSelectionProvider::class.java) { recipientSelectionProvider = it }
        .build() 
}
```

Данная фича позволит вам получить:
1) Инстанс фрагмента:
```kotlin
val recipientSelectionFragment = dependency.getRecipientSelectionFragment(
    config = RecipientSelectionConfig(
        useCase = RecipientSelectionUseCase.NewPrivateChat
    )
)
```
2) Инстанс активити:
```kotlin
val recipientSelectionIntent = dependency.getRecipientSelectionIntent(
    context = context,
    config = RecipientSelectionConfig(
        useCase = RecipientSelectionUseCase.NewPrivateChat
    )
)
```
3) Менеджер для работы с результатом выбора:
```kotlin
dependency.getRecipientSelectionResultManager()
```

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Storekeeper](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)