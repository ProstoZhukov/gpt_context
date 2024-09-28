# Модуль "Яндекс AppMetrica" для сбора аналитики

Модуль содержит комплекс компонентов для работы с АппМетрикой


## Дополнительная информация

- [ответственный Малинский Г.А.](https://online.sbis.ru/person/0511feb3-fa27-4b74-bced-7b45e697ef51)
- [ответственный Алексиков А.Ю.](https://online.sbis.ru/person/db1847ad-cfc3-417d-ae40-3f5455ab152b)

- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/a2c7643e-8bee-4714-87ff-15aab43894ad)


# Подключение

Инструкция по созданию проекта в АппМетрике
https://n.sbis.ru/article/717244bc-a72d-4a22-89ed-34b6a47c97ad
В результате получим API Key для интеграции аппМетрики 

Порядок интеграции АппМетрики в проект

1. В build.gradle проекта добавляем ключи, полученные в АппМетрике при создании там проекта
   `yandexAppMetrica = "API_key проекта релиз"
    yandexAppMetricaDebug = "API-key проекта дебаг"`
2. Добавляем ключи в BuildConfig в build.gradle (:app)
3. Инициализируем библиотеку в методе Plugin.doAfterInitialize()
   `private val appMetrica: AppMetrica by lazy { AppMetricaImpl(application, yandexAppMetrica) }
    override fun doAfterInitialize() {
       appMetrica.init()
    }`

### Описание публичного API

Модуль содержит два метода для отправки событий в AppMetrica
- sendAnalyticsEvent - метод отправляет события о пользовательских действиях, в качестве параметров
  принимает два значения Контент + Событие. Формируем свои енумы с типом контента и событием для этого контента. 
  Для этого есть Маркерные интерфейсы AppMetricaContentType и AppMetricaEvent
  Пример их использования:

  `enum class ContentType(override val key: String) : AppMetricaContentType {
    COMPANY_RESTAURANT("Карточка заведения ресторана")
  }`

  `enum class Event(override val key: String) : AppMetricaEvent {
    DELIVERY_BUTTON_CLICK("Нажатие на кнопку \"Доставка\"")
  }`
 В результате вызова метода sendAnalyticsEvent в АппМетрике появится папка "Карточка заведения ресторана" с событием в ней "Нажатие на кнопку "Доставка""

- sendAnalyticsErrorEvent - метод отправляет события о некритичных ошибках в работах методов,
  в качестве параметров принимает значение типа Throwable

## Использование в приложениях

- [Sabyget](https://git.sbis.ru/mobileworkspace/apps/droid/sabyget)
- [Brand](https://git.sbis.ru/mobileworkspace/apps/droid/brand)
