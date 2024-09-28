# Модуль для рендера богатого текста (richtext)
| Ответственность | Ответственные |
|-----------------|---------------|
| Участок работ | [Богатый текст (richtext) Android](https://online.sbis.ru/area/9f5a655e-5301-4528-8460-4c272d6e51c9) |

## Описание
Модуль необходим для конвертации текста с набором тегов в стилизованную markup-model с помощью
[RichTextConverter](src/main/java/ru/tensor/sbis/richtext/converter/RichTextConverter.java)<br>
и рендера модели на экран с использованием
[RichTextView](src/main/java/ru/tensor/sbis/richtext/view/RichTextView.java) или
[RichViewLayout](src/main/java/ru/tensor/sbis/richtext/view/RichViewLayout.java)

## Руководство по подключению и инициализации
Для добавления модуля в проект, в `settings.gradle` проекта должны быть подключены следующие модули:

| Репозиторий | модуль |
|-----------------|---------------|
| https://git.sbis.ru/mobileworkspace/android-serviceAPI.git | edo-decl, toolbox-decl |
| https://git.sbis.ru/mobileworkspace/android-design.git | design, design_utils, design_collection_view, design_dialogs, list_utils, text_span |
| https://git.sbis.ru/mobileworkspace/android-utils.git | richtext, common, objectpool, base_components |

## Описание публичного API
- Для использования модуля необходимо зарегистрировать в приложении плагин [RichTextPlugin](src/main/java/ru/tensor/sbis/richtext/RichTextPlugin.kt)  
и предоставить зависимости, описанные в интерфейсе [RichTextDependency](src/main/java/ru/tensor/sbis/richtext/contract/RichTextDependency.kt)
- Конвертация богатого текста осуществляется с помощью одной из реализаций интерфейса [RichTextConverter](src/main/java/ru/tensor/sbis/richtext/converter/RichTextConverter.java):
[JsonRichTextConverter](src/main/java/ru/tensor/sbis/richtext/converter/json/JsonRichTextConverter.java) для преобразования строки в виде json markup-model в стилизованный текст для рендера, 
[SabyDocRichTextConverter](src/main/java/ru/tensor/sbis/richtext/converter/sabydoc/SabyDocRichTextConverter.kt) для преобразования файла с расширением .sabydoc в модель, 
содержащую стилизованный текст и опциональное оглавление для рендера.
- Для установки глобальной конфигурации отрисовки богатого текста на уровне приложения необходимо использовать [RichTextGlobalConfiguration](src/main/java/ru/tensor/sbis/richtext/converter/cfg/RichTextGlobalConfiguration.java)
- Для установки конфигурации отрисовки богатого текста на конкретном экране необходимо создать [Configuration](src/main/java/ru/tensor/sbis/richtext/converter/cfg/Configuration.java) и передать ее в [RichTextConverter](src/main/java/ru/tensor/sbis/richtext/converter/RichTextConverter.java)
- Стилизация богатого текста осуществляется стандартными методами и свойствами [TextView](https://developer.android.com/reference/android/widget/TextView)

## Ссылки на руководства
Подробное описание использования модуля опубликовано в [статье](https://wi.sbis.ru/doc/platform/developmentapl/mobile/android/rich-text/)

##### Трудозатраты внедрения
0.8 ч/д