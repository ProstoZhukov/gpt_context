@file:Suppress("unused")

package ru.tensor.sbis.design.stubview

import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.*

/**
 * Набор стандартных случаев отображения заглушки
 *
 * Все поля открыты и могут быть использованы для кастомного контента.
 * Например, если от [StubViewCase.NO_MESSAGES] нужны иконка и заголовок, а описание нужно заменить своим:
 * ```
 * val customContent = ResourceImageStubContent(
 *      image = StubViewImageType.NO_MESSAGES,
 *      messageRes = StubViewCase.NO_MESSAGES.messageRes,
 *      details = "My custom details"
 * )
 * stubView.setContent(customContent)
 * ```
 *
 * @param imageType абстракция над стандартным изображением заглушки
 * @param messageRes ресурс текста заголовка заглушки
 * @param detailsRes ресурс текста описания заглушки
 *
 * @author ma.kolpakov
 */
enum class StubViewCase(
    val imageType: StubViewImageType,
    @Deprecated("Png изображения заглушек, на которые ссылается атрибут, будут удалены. Используй абстракцию imageType: StubViewImageType.")
    @get:StringRes val messageRes: Int,
    @get:StringRes val detailsRes: Int,
) {
    /** Список диалогов пуст (название lottie.json - Нет сообщений) */
    NO_MESSAGES(
        imageType = StubViewImageType.NO_MESSAGES,
        messageRes = R.string.design_stub_view_no_messages_message,
        detailsRes = R.string.design_stub_view_no_messages_details,
    ),

    /** Список контактов пуст (название lottie.json - Нет контактов) */
    NO_CONTACTS(
        imageType = StubViewImageType.NO_CONTACTS,
        messageRes = R.string.design_stub_view_no_contacts_message,
        detailsRes = R.string.design_stub_view_no_contacts_details,
    ),

    /** Список новостей пуст (название lottie.json - Нет новостей) */
    NO_NEWS(
        imageType = StubViewImageType.NO_NEWS,
        messageRes = R.string.design_stub_view_no_news_message,
        detailsRes = R.string.design_stub_view_no_news_details,
    ),

    /** Список событий пуст (название lottie.json - Нет событий) */
    NO_EVENTS(
        imageType = StubViewImageType.NO_EVENTS,
        messageRes = R.string.design_stub_view_no_events_message,
        detailsRes = R.string.design_stub_view_no_events_details,
    ),

    /** Список задач пуст (название lottie.json - Нет задач) */
    NO_TASKS(
        imageType = StubViewImageType.NO_TASKS,
        messageRes = R.string.design_stub_view_no_tasks_message,
        detailsRes = R.string.design_stub_view_no_tasks_details,
    ),

    /** Список файлов пуст (название lottie.json - Нет файлов) */
    NO_FILES(
        imageType = StubViewImageType.NO_FILES,
        messageRes = R.string.design_stub_view_no_files_message,
        detailsRes = R.string.design_stub_view_no_files_details,
    ),

    /** Список уведомлений пуст (название lottie.json - Нет уведомлений) */
    NO_NOTIFICATIONS(
        imageType = StubViewImageType.NO_NOTIFICATIONS,
        messageRes = R.string.design_stub_view_no_notifications_message,
        detailsRes = R.string.design_stub_view_no_notifications_details,
    ),

    /** Нет данных о зарплате (название lottie.json - Нет данных о зарплате) */
    NO_SALARY_DATA(
        imageType = StubViewImageType.NO_SALARY_DATA,
        messageRes = R.string.design_stub_view_no_salary_data_message,
        detailsRes = R.string.design_stub_view_no_salary_data_details,
    ),

    /** Страница не найдена (название lottie.json - Ошибка) */
    PAGE_NOT_FOUND(
        imageType = StubViewImageType.ERROR,
        messageRes = R.string.design_stub_view_page_not_found_message,
        detailsRes = R.string.design_stub_view_page_not_found_details,
    ),

    /** Нет подключения к интернету (название lottie.json - Ошибка) */
    NO_CONNECTION(
        imageType = StubViewImageType.ERROR,
        messageRes = R.string.design_stub_view_no_connection_message,
        detailsRes = R.string.design_stub_view_no_connection_details,
    ),

    /** Сервис недоступен (на тех. обслуживании) (название lottie.json - Ошибка) */
    SERVICE_UNAVAILABLE(
        imageType = StubViewImageType.ERROR,
        messageRes = R.string.design_stub_view_service_unavailable_message,
        detailsRes = R.string.design_stub_view_service_unavailable_details
    ),

    /** Ошибка СБИС (название lottie.json - Ошибка) */
    SBIS_ERROR(
        imageType = StubViewImageType.ERROR,
        messageRes = R.string.design_stub_view_sbis_error_message,
        detailsRes = R.string.design_stub_view_sbis_error_details,
    ),

    /** Список результатов поиска пуст (название lottie.json - Не найдено) */
    NO_SEARCH_RESULTS(
        imageType = StubViewImageType.NOT_FOUND,
        messageRes = R.string.design_stub_view_no_search_results_message,
        detailsRes = R.string.design_stub_view_no_search_results_details,
    ),

    /** Список результатов фильтрации пуст (название lottie.json - Не найдено) */
    NO_FILTER_RESULTS(
        imageType = StubViewImageType.NOT_FOUND,
        messageRes = R.string.design_stub_view_no_filter_results_message,
        detailsRes = R.string.design_stub_view_no_filter_results_details,
    ),

    /** Список сотрудников пуст (название lottie.json - Универсальное изображение) */
    NO_EMPLOYEES(
        imageType = StubViewImageType.ETC,
        messageRes = R.string.design_stub_view_no_employees_message,
        detailsRes = ID_NULL
    ),

    /** Список камер пуст (название lottie.json - Не найдено) */
    NO_CAMERAS_RESULTS(
        imageType = StubViewImageType.NOT_FOUND,
        messageRes = R.string.design_stub_view_no_cameras_results_message,
        detailsRes = ID_NULL
    ),

    /** Нет Разрешений (название lottie.json - Универсальное изображение) */
    NO_PERMISSIONS(
        imageType = StubViewImageType.ETC,
        messageRes = R.string.design_stub_view_no_permissions_message,
        detailsRes = ID_NULL
    ),

    /** Нет данных (название lottie.json - Нет данных) */
    NO_DATA(
        imageType = StubViewImageType.NO_DATA,
        messageRes = R.string.design_stub_view_no_data_message,
        detailsRes = ID_NULL
    ),

    /** Чаты техподдержки (название lottie.json - Чаты техподдержки) */
    TECHNICAL_SUPPORT_CHATS(
        imageType = StubViewImageType.TECHNICAL_SUPPORT_CHATS,
        messageRes = R.string.design_stub_view_technical_support_chats_message,
        detailsRes = ID_NULL
    ),

    /** Сканирование (название lottie.json - Сканирование) */
    SCANNING(
        imageType = StubViewImageType.SCANNING,
        messageRes = R.string.design_stub_view_scanning_message,
        detailsRes = ID_NULL
    ),

    /** Нет геоданных (название lottie.json - Нет геоданных) */
    NO_GEODATA(
        imageType = StubViewImageType.NO_GEODATA,
        messageRes = R.string.design_stub_view_no_geodata_message,
        detailsRes = ID_NULL
    ),

    /** Пусто (название lottie.json - Пусто) */
    EMPTY(
        imageType = StubViewImageType.EMPTY,
        messageRes = R.string.design_stub_view_empty_message,
        detailsRes = ID_NULL
    ),

    /** Нет броней (название lottie.json - Нет броней) */
    NO_RESERVATION(
        imageType = StubViewImageType.NO_RESERVATION,
        messageRes = R.string.design_stub_view_no_reservation_message,
        detailsRes = ID_NULL
    ),

    /** Список пуст (название lottie.json - Список пуст) */
    EMPTY_LIST(
        imageType = StubViewImageType.EMPTY_LIST,
        messageRes = R.string.design_stub_view_empty_list_message,
        detailsRes = ID_NULL
    ),

    /** Все выполнено (название lottie.json - Все выполнено) */
    ALL_DONE(
        imageType = StubViewImageType.ALL_DONE,
        messageRes = R.string.design_stub_view_all_done_message,
        detailsRes = ID_NULL
    ),

    /** Нет готовых блюд (название lottie.json - Нет готовых блюд) */
    NO_READY_MEALS(
        imageType = StubViewImageType.NO_READY_MEALS,
        messageRes = R.string.design_stub_view_no_ready_meals_message,
        detailsRes = ID_NULL
    ),

    /** Перезагрузите приложение (название lottie.json - Перезагрузите приложение) */
    RESTART_APP(
        imageType = StubViewImageType.RESTART_APP,
        messageRes = R.string.design_stub_view_restart_app_message,
        detailsRes = ID_NULL
    ),

    /** Нет готовых блюд (название lottie.json - Нет готовых блюд) */
    AUTH_SOCIAL(
        imageType = StubViewImageType.AUTH_SOCIAL,
        messageRes = R.string.design_stub_view_auth_social_message,
        detailsRes = ID_NULL
    ),

    /** Перезагрузите приложение (название lottie.json - Перезагрузите приложение) */
    TOUCH_ID(
        imageType = StubViewImageType.TOUCH_ID,
        messageRes = R.string.design_stub_view_touch_id_message,
        detailsRes = ID_NULL
    );

    /**
     * Получение контента заглушки с возможностью модификации параметров стандартной заглушки.
     */
    fun getContent(): StubViewContent = ImageStubContent(imageType, messageRes, detailsRes)

    fun getContent(
        image: StubViewImageType = imageType,
        message: Int = messageRes,
        details: Int = detailsRes,
        actions: Map<Int, () -> Unit> = emptyMap()
    ): StubViewContent = ImageStubContent(image, message, details, actions)

    fun getContent(
        image: StubViewImageType = imageType,
        message: String?,
        details: Int = detailsRes,
        actions: Map<Int, () -> Unit> = emptyMap()
    ): StubViewContent = ImageStubContent(image, message, details, actions)

    fun getContent(
        image: StubViewImageType = imageType,
        message: Int = messageRes,
        details: String?,
        actions: Map<Int, () -> Unit> = emptyMap()
    ): StubViewContent = ImageStubContent(image, message, details, actions)

    fun getContent(
        image: StubViewImageType = imageType,
        message: String?,
        details: String?,
        actions: Map<Int, () -> Unit> = emptyMap()
    ): StubViewContent = ImageStubContent(image, message, details, actions)

    /**
     * Получение контента заглушки с клик экшенами
     *
     * @param actions список клик экненов для описания заглушки
     */
    fun getContent(actions: Map<Int, () -> Unit>): StubViewContent =
        ImageStubContent(imageType, messageRes, detailsRes, actions)

}
