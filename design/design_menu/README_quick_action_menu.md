### Меню быстрых действий в пустом представлении

| Класс                                                                          | Ответственные                                                                     | Добавить                                                                                    |
|--------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| [QuickActionMenu](src/main/java/ru/tensor/sbis/design/design_menu/QuickActionMenu.kt) | [Гераськин Р.А.](https://dev.sbis.ru/person/45883fa3-9b14-458b-97b3-3f55f527230d) | [Задачу/поручение/ошибку](https://online.sbis.ru/area/63ffe6b3-193f-4d77-91c5-3bafeef9cf49) |

##### Внешний вид

![SbisMunu](doc_resources/SbisMenu_3.png)

[Описание и спецификация из заглушек](http://axure.tensor.ru/MobileStandart8/%D0%B7%D0%B0%D0%B3%D0%BB%D1%83%D1%88%D0%BA%D0%B8_ver2_24_1200.html)

##### Описание

- Такое меню можно использовать, как отдельно, так и совместно с заглушкой в блоке. При
  нехватке места на экране, например при повороте в альбомную ориентацию, приоритет отдается меню
  быстрых действий, картинка в заглушке будет автоматически скрываться.
  Рекомендуется использовать для меню не более 6 пунктов. Если пункты меню не умещаются на экране
  появляется возможность скролирования и замыливание.

- В отличие от обычного меню, меню быстрых действий не встраивается в контейнер, а возвращается в
  виде `View` для последующего встраивания.

- Меню может добавить второй столбец элементов, если это позволяет ширина контейнера. Ширина
  родителя для изменения компоновки, проверяется в методе `doOnPreDraw`, поэтому для получения
  актуальных размеров меню предусмотрен метод обратного вызова, находящийся в переменной
  `onMenuLayoutReady`. Это может быть актуально для подстраивания заглушки под размер
  меню, т.к. только в этом методе можно получить актуальные значения размеров компонента.

##### Руководство по подключению и инициализации

Для добавления модуля в проект, в `settings.gradle` проекта должны быть подключены следующие модули:

| Репозиторий                                            | Модуль                |
|--------------------------------------------------------|-----------------------|
| https://git.sbis.ru/mobileworkspace/android-design.git | design                |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_sbis_text_view |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_utils          |
| https://git.sbis.ru/mobileworkspace/android-utils.git  | common                |

##### Стилизация

Тема компонента основана на глобальных атрибутах и не имеет отдельного атрибута для переопределения
темы.

##### Описание особенностей работы

- Для получения view компонента меню быстрых действий необходимо создать
  экземпляр [SbisMenu](src/main/java/ru/tensor/sbis/design/context_menu/SbisMenu.kt), передав в
  конструкторе список
  элементов [QuickActionMenuItem](src/main/java/ru/tensor/sbis/design/context_menu/QuickActionMenuItem.kt).
  Метод [SbisMenu.createQuickActionMenuView](src/main/java/ru/tensor/sbis/design/context_menu/SbisMenu.kt#L137)
  вернёт view, готовый к внедрению в необходимое место.

- Для подписки на событие, когда view полностью рассчитало свой размер свой размер можно
  использовать метод обратного вызова:`onQuickActionMenuLayoutReady`.

```kotlin
import ru.tensor.sbis.design.design_menu.databinding.QuickActionMenuItemBinding

fun getMenu(someItems: List<SomeItems>): QuickActionMenu {
  val menuItems = someItems.map { someItem ->
    QuickActionMenuItem(
      title = someItem.itemTitle,
      image = someItem.itemIcon,
      handler = {/* обработчик нажатий на элемент */ }
    )
  }
  return QuickActionMenu(items = menuItems)
}

...

val menu = getMenu(myRawIntems)
menu.createView(requireContext(), menuContainer)
menu.onMenuLayoutReady = {
  /* изменение размеров окружающих элементов если то требуется */
}

```

##### Трудозатраты внедрения

1 ч/д
