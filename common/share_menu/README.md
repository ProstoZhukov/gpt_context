# Меню приложения для функциональности "поделиться"
| Модуль       | Ответственные                                                                          |
|--------------|----------------------------------------------------------------------------------------|
| [share_menu] | [Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877) |

## Описание
Модуль содержит реализацию [ShareMenuActivity](src/main/java/ru/tensor/sbis/share_menu/ShareMenuActivity.kt) - меню приложения для функциональности "поделиться".
Меню отображает доступные для пользователя пункты, куда он может поделиться выбранными данными.
Меню будет открываться при выборе приложения или по клику на зарегистрированную
системную миниатюрку "недавнего", например контакта, которому писал пользователь.

## Руководство по подключению и инициализации меню
Для подключения меню к приложению необходимо выполнить 2 действия:
1) Добавить модуль в `settings.gradle` приложения.
```
include ':share_menu' 
project(':share_menu').projectDir = new File(settingsDir, "$common_dir/share_menu")
```
2) Подключить [ShareMenuPlugin](src/main/java/ru/tensor/sbis/share_menu/ShareMenuPlugin.kt) в `PluginManager` приложения.

После подключения плагин меню сам соберет все зарегистрированные реализации пунктов,
подключенных в приложении.

## Руководство по реализации прикладного пункта
Для отображения своего пункта в меню необходимо реализовать обработчик [ShareHandler](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/rc-23.7160/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/share/ShareHandler.kt).
Подробное описание доступных настроек находится в самом интерфейсе.
`ShareHandler` поставляет модель для отображения пункта меню, и его контент,
который будет отображаться в меню при выборе пункта.

Реализованный обработчик необходимо поставлять в качестве `api` в плагине вашего модуля реализации.

### Пример реализации обработчика
```kotlin
/**
 * Обработчик функциональности "поделиться" с контактами.
 */
internal class ContactsShareHandler : ShareHandler {

    // Данный пункт будет отображаться в навигационной панели меню
    override val menuItem: ShareMenuItem =
        ShareMenuItem(
            // Указываем id раздела, на который завязан пункт.
            id = NavxId.DIALOGS.id,
            icon = SbisMobileIcon.Icon.smi_menuContacts,
            title = R.string.communicator_share_target_title_contacts,
            // Указываем order относительно пунктов, между которыми должен встраиваться Ваш пункт в общем списке
            order = 100
        )

    // Название обработчика для сбора аналитики по использованию раздела в меню.
    // Если не будет указано название - аналитики не будет.
    override val analyticHandlerName: String = CONTACTS_SHARE_ANALYTIC_NAME

    // Указываем типы поддерживаемых данных для "поделиться".
    // По умолчанию поддерживаются все типы.
    override fun isShareSupported(shareData: ShareData): Boolean =
        when (shareData) {
            is ShareData.Text,
            is ShareData.Files,
            is ShareData.Contacts -> true
            else -> false
        }
    
    // Если регистрируете свои миниатюрки "недавних" (Direct share),
    // указанный в интенте ключ проверяется здесь, чтобы идентифицировать обработчик.
    override fun isQuickShareSupported(quickShareKey: String): Boolean =
        quickShareKey.contains(menuItem.id)

    // Переопределяем для создания фрагмента, который будет отображаться в меню при выборе пункта.
    // Для обычного шаринга через иконку приложения будет приходить только shareData,
    // для "недавних" еще и ключ.
    override fun getShareContent(shareData: ShareData, quickShareKey: String?): Fragment =
        ContactsShareFragment.newInstance(shareData = shareData, quickShareKey = quickShareKey)
}

// Название для аналитики должно быть конкретным и простым, остальные приписки добавляются на уровне компонента меню.
// Таким образом финальное событие будет иметь название share_send_contacts или direct_share_send_contacts.
private const val CONTACTS_SHARE_ANALYTIC_NAME = "contacts"
```

### Пример реализации специфичного обработчика
```kotlin
/**
 * Реализация обработчика просмотра sabydoc файла, поступившего из меню "Поделиться".
 * При выборе пункта будет открываться Activity.
 */
internal class SabyDocShareHandler(private val context: Context) : ShareHandler {

    override val menuItem: ShareMenuItem =
        ShareMenuItem(
            // Элемент не завязан на навигацию NavigationService, поэтому придумываем свой id.
            id = SABY_DOC_ITEM_ID,
            icon = SbisMobileIcon.Icon.smi_read,
            title = R.string.sabydoc_viewer_share_target_title,
            order = 700
        )

    // Элемент не завязан на навигацию NavigationService, явно это указываем.
    // Может быть иной случай, когда в разных приложениях разные NavxId - указываем несколько.
    override val navxIds: Set<String>? = null

    // Поддержка только определенного типа файлов.
    override fun isShareSupported(shareData: ShareData): Boolean =
        shareData is ShareData.Files && shareData.files.any(::isSabyDocUri)

    // По нажатию на пункт должна открываться Acitivity, а не фрагмент внутри меню -
    // переопределяем getShareContentIntent.
    override fun getShareContentIntent(
        context: Context,
        shareData: ShareData,
        quickShareKey: String?
    ): Intent =
        ViewerSliderActivity.createViewerSliderIntent(
            context,
            ViewerSliderArgs(
                viewerArgsList = ArrayList(
                    shareData.files.mapNotNull {
                        if (isSabyDocUri(it)) {
                            SabyDocViewerArgs.ByUri(it)
                        } else {
                            null
                        }
                    }
                ),
                thumbnailListDisplayArgs = ThumbnailListDisplayArgs(visible = true)
            )
        )

    private fun isSabyDocUri(uri: String): Boolean =
        FileUriUtil
            .getFileName(context = context, uri = Uri.parse(uri))
            ?.endsWith(FileUtil.SABYDOC_EXTENSION)
            ?: false
}

private const val SABY_DOC_ITEM_ID = "SABY_DOC_ITEM_ID"
```

## Использование в приложениях
- [Коммуникутор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Courier](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [SabyDisk](https://git.sbis.ru/mobileworkspace/apps/droid/sabydisk)
- [MySaby](https://git.sbis.ru/mobileworkspace/apps/droid/mysaby)
- [Brand](https://git.sbis.ru/mobileworkspace/apps/droid/brand)