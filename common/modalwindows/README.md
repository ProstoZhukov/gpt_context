#### Modal Windows

|Модуль|Ответственные|
|------|-------------|
|modalwindows|[Головкин Сергей](https://online.sbis.ru/person/d43bba4d-da45-48a9-a6df-33fc27d4d211)

#### Использование в приложениях
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [СБИС СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)

#### Описание
Модуль содержит кастомные компоненты для отображения модальных окон (Dialog Alert, Bottom Sheet, Options Content)

#### Темизация

Компоненты списка опций AbstractOptionSheetContentFragment и AbstractBottomOptionsSheet поддерживают темизацию.
По умолчанию используется базовая тема `ModalWindowsOptionSheetTheme`.

Для применения собственной стилизации необходимо добавить в тему приложения атрибут `optionSheetTheme`.
В качестве значения атрибута нужно указать ссылку на перегруженную тему `ModalWindowsOptionSheetTheme` из модуля `modalwindows`.

Доступные атрибуты:

`optionSheet_optionItemTextColor` - цвет текста опции для светлой темы
`optionSheet_optionItemTextColorDark` - цвет текста опции для темной темы
`optionSheet_optionItemCheckboxIconColor` - цвет иконки выбранной опции для светлой темы
`optionSheet_optionItemCheckboxIconColorDark` - цвет иконки выбранной опции для темной темы

#### ContainerMovableFragment и ContainerMovableDialogFragment

Данные классы являют собой обертки над компонентом шторка с базовой реализацией двух высот - открыто по высоте контента и скрыто полностью.
Для создания используется класс Builder с рядом методов для более тонкой настройки.
Методы описаны в классе ContainerMovableDelegateImpl.AbstractBuilder.
Представленные классы реализуют логику ряда полезных интерфейсов таких как:
   - AdjustResizeHelper.KeyboardEventListener
   - DefaultViewInsetDelegate
   - Container.Closeable
   - Container.Showable
   - Lockable
   - ForceCloseable
   - StatusBarColorKeeper

Так же дополнительно реализованы интерфейсы для взаимодействия со шторкой, а именно:

 - interface Lockable с единственным методом lockPanel(locked: Boolean) для принудительной блокировки реакции шторки на свайпы
 - interface ForceCloseable с единственным методом forceClose() для закрытия без анимации
 - interface ReadyToCloseChecker с единственным методом readyToClose() для фрагментов контента, необходим для проверки готовности контента к закрытию

