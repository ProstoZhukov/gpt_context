#### Основные сущности Gradle-проекта

1. settings.gradle - содержит пути до локальных подпроектов с исходниками;
2. build.gradle - настраиваются репозитории для внешних зависимостей, декларируются используемые плагины и другие общие настройки для проекта в целом;
3. gradle.properties - содержит параметры для запуска JVM, Gradle и подключенных плагинов;
4. local.propeties - cодержит доп. параметры (аналогично gradle.propeties), этот файл не попадает в git и существует лишь локально;
5. app/build.gradle - настраиваются подписи приложения, applicationId и прочее Android специфичное уровня application;

#### Структура android приложений в Тензоре
Все наши приложения являются многомодульными, количество подпроектов в которых составляет от 100 до 500.  
На примере приложения "Коммуникатор" (450+ модулей).

Упрощенно структура приложения представлена ниже:  
communicator  
├── app  
│   ├── src  
│   └── build.gradle  
├── auth (30 modules)  
│   ├── auth_lock  
│   │   ├── src  
│   │   └── build.gradle  
│   ├── auth_devices  
│   │   ├── src  
│   │   └── build.gradle  
│   ├── auth_invite  
│   │   ├── src  
│   │   └── build.gradle  
│   └── ...  
├── calendar (70+ modules)  
├── common (100+ modules)  
├── communicator (20+ modules)  
├── declaration (20+ modules)  
├── design (70+ modules)  
├── disk (20 modules)  
├── ...  
├── gradle.properties  
├── local.properties  
├── build.gradle  
└── settings.gradle

#### Разбиение настроек подключения модулей
Все настройки верхнего уровня(уровня приложения): settings.gradle и build.gradle, сосредоточены в отдельном репозитории.  
Репозиторий имеет следующую структуру:  
app_gradle_settings  
├── app  
│   ├── communicator  
│   │   ├── settings.gradle  
│   │   └── build.gradle  
│   ├── retail  
│   │   ├── settings.gradle  
│   │   └── build.gradle  
│   └── ...  
└── submodule  
├── auth  
│   ├── auth_communicator_settings.gradle  
│   ├── auth_retail_settings.gradle  
│   └── ...  
├── design  
│   ├── design_communicator_settings.gradle  
│   └── design_retail_settings.gradle  
└── ...

Разбиение, объединение и применение настроечных скриптов производится по приложениям.    
Файлы из папки `app_gradle_settings/submodule` - это переехавшие из прикладных репозиториев настройки с подключением фиче-модулей. В имени файла settings.gradle обязательно указывается имя приложения для которого эти настройки будут применяться.

В `app_gradle_settings/app` - располагаются агрегирующие settings.gradle, файлы build.gradle с настройками deeplink-ов, токенов и прочим.  
Для примера агрегирующий файл app_gradle_settings/app/waiter/settings.gradle будет выглядеть следующим образом:
```
apply from: "../global_settings.gradle"
setupGlobalSettings(settings, "$settingsDir/module/android-configs/gradle_configs".toString())

def appName = "waiter"

includeSubmodules(
        settings,
        appName,
        "auth",
        "android-crm",
        "barcodereader",
        "catalog",
        "common",
        "design",
        "declaration",
        "disk",
        "kitchen",
        "motivation",
        "presto-common",
        "review"
)

include ':app', ':controller'

includeControllerSources(settings, appName)
```
Секция `includeSubmodules` описывает перечень подключаемых фич к приложению, имя фичи наприм. "auth", указывает на соответствующие настройки располагаемые в `app_gradle_settings/submodule/auth`, которые будут взяты в соответствии с appName.

На уровне приложения файл settings.gradle примет вид:
```
apply from: "app_gradle_settings/app/waiter/settings.gradle"
```