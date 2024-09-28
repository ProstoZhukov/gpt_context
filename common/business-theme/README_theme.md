### business-theme
Модуль содержит темизацию компонентов МП Бизнес

#### Описание  
Атрибуты темизации бизнес компонентов:
Общие атрибуты [attr_base](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_base.xml)
Атрибуты графика [attr_chart](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_chart.xml)
Атрибуты шрифтов [attr_font](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_font.xml)
Атрибуты чека [attr_receipt](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_receipt.xml)
Атрибуты отчетов ОФД [attr_report](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_report.xml)
Атрибуты строки поиска [attr_search_panel](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_search_panel.xml)
Атрибуты заглушки [attr_stub](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/attr_stub.xml)

Прикладная цветовая палитра бизнес компонентов:
Палитра чека [color_receipt](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/color_receipt.xml)
Палитра базовых макетов [colors_business](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/colors_business.xml)
Палитра темной темы [colors_dark](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/colors_dark.xml)
Палитра светлой темы [colors_light](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/colors_light.xml)

Темы бизнес компонентов:
Стандартная тема графика [theme_chart](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/theme_chart.xml)
Базовая тема строки поиска [theme_search_panel](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/theme_search_panel.xml)
Базовая тема заглушки [theme_stub](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/theme_stub.xml)
Базовая тема общих макетов [themes_base](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_base.xml)
Светлая и темная темы чека [themes_receipt](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_receipt.xml)
Стандартная (МП Бизнес), светлая и темная (МП Розницы) темы отчетов ОФД [themes_report](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_report.xml)
Светлая и темная темы графика [themes_retails_chart](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_retails_chart.xml)
Светлая и темная темы строки поиска [themes_retails_search_panel](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_retails_search_panel.xml)
Светлая и темная темы заглушки [themes_retails_stub](https://git.sbis.ru/mobileworkspace/android-utils/blob/rc-21.1261/business-theme/src/main/res/values/themes_retails_stub.xml)

#### Подключение
Для добавления модуля в проект в settings.gradle проекта должны быть подключены следующие модули:
    `include ':business-theme'`
    `project(':business-theme').projectDir = new File(settingsDir, 'common/business-theme')`
    
Далее в модуле, который требует компонент добавить.
    `implementation project(':business-theme')`
