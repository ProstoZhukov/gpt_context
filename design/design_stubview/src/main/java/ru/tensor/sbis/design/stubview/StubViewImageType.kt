package ru.tensor.sbis.design.stubview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.RawRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat.ID_NULL
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import ru.tensor.sbis.design.stubview.utils.StubViewImageColors
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Тип стандартного изображения заглушки
 *
 * @author ra.geraskin
 */
enum class StubViewImageType(

    /**
     * Ресурс lottie json файла изображения.
     */
    @RawRes private val lottieRawRes: Int,

    ) {

    /** Нет изображения заглушки */
    EMPTY_STUB_IMAGE(lottieRawRes = ID_NULL),

    /** Список диалогов пуст (название lottie.json - Нет сообщений) */
    NO_MESSAGES(lottieRawRes = R.raw.no_messages),

    /** Список контактов пуст (название lottie.json - Нет контактов) */
    NO_CONTACTS(lottieRawRes = R.raw.no_contacts),

    /** Список новостей пуст (название lottie.json - Нет новостей) */
    NO_NEWS(lottieRawRes = R.raw.no_news),

    /** Список событий пуст (название lottie.json - Нет событий) */
    NO_EVENTS(lottieRawRes = R.raw.no_events),

    /** Список задач пуст (название lottie.json - Нет задач) */
    NO_TASKS(lottieRawRes = R.raw.no_tasks),

    /** Список файлов пуст (название lottie.json - Нет файлов) */
    NO_FILES(lottieRawRes = R.raw.no_files),

    /** Список уведомлений пуст (название lottie.json - Нет уведомлений) */
    NO_NOTIFICATIONS(lottieRawRes = R.raw.no_notifications),

    /** Нет данных о зарплате (название lottie.json - Нет данных о зарплате) */
    NO_SALARY_DATA(lottieRawRes = R.raw.no_salary_data),

    /** Страница не найдена (название lottie.json - Ошибка) */
    ERROR(lottieRawRes = R.raw.error),

    /** Список результатов поиска пуст (название lottie.json - Не найдено) */
    NOT_FOUND(lottieRawRes = R.raw.not_found),

    /** Список сотрудников пуст (название lottie.json - Универсальное изображение) */
    ETC(lottieRawRes = R.raw.etc),

    /** Нет данных (название lottie.json - Нет данных) */
    NO_DATA(lottieRawRes = R.raw.no_data),

    /** Чаты техподдержки (название lottie.json - Чаты техподдержки) */
    TECHNICAL_SUPPORT_CHATS(lottieRawRes = R.raw.technical_support_chats),

    /** Сканирование (название lottie.json - Сканирование) */
    SCANNING(lottieRawRes = R.raw.scanning),

    /** Нет геоданных (название lottie.json - Нет геоданных) */
    NO_GEODATA(lottieRawRes = R.raw.no_geodata),

    /** Пусто (название lottie.json - Пусто) */
    EMPTY(lottieRawRes = R.raw.empty),

    /** Нет броней (название lottie.json - Нет броней) */
    NO_RESERVATION(lottieRawRes = R.raw.no_reservation),

    /** Список пуст (название lottie.json - Список пуст) */
    EMPTY_LIST(lottieRawRes = R.raw.empty_list),

    /** Все выполнено (название lottie.json - Все выполнено) */
    ALL_DONE(lottieRawRes = R.raw.all_done),

    /** Нет готовых блюд (название lottie.json - Нет готовых блюд) */
    NO_READY_MEALS(lottieRawRes = R.raw.no_ready_meals),

    /** Перезагрузите приложение (название lottie.json - Перезагрузите приложение) */
    RESTART_APP(lottieRawRes = R.raw.restart_app),

    /** Поиск sso */
    AUTH_SOCIAL(lottieRawRes = R.raw.auth_social),

    /** Настройка биометрической аутентификации */
    TOUCH_ID(lottieRawRes = R.raw.touc_id);

    /**
     * Синхронное получение Drawable изображения заглушки.
     */
    fun getDrawable(context: Context): Drawable? {
        if (lottieRawRes == ID_NULL) return null
        LottieCompositionFactory.fromRawResSync(context, lottieRawRes).value?.let { composition ->
            LottieDrawable().apply {
                this.composition = composition
                StubViewImageColors.values().forEach { color ->
                    addLottieValueCallback(context, color.lottieLairName, color.colorAttrRes)
                }
                try {
                    playAnimation()
                    repeatCount = ValueAnimator.INFINITE
                } catch (e: Exception) {
                    Timber.e("Ошибка воспроизведения lottie анимации при создании LottieDrawable.")
                }
                return this
            }
        }
        return null
    }

    /**
     * Асинхронное получение Drawable изображения заглушки.
     */
    internal fun getDrawable(context: Context, onSuccess: (Drawable) -> Unit, onFailure: (() -> Unit)?) {
        if (lottieRawRes == ID_NULL) {
            onFailure?.invoke()
            return
        }
        LottieCompositionFactory
            .fromRawRes(context, lottieRawRes)
            .addListener {
                LottieDrawable().apply {
                    composition = it
                    StubViewImageColors.values().forEach { color ->
                        addLottieValueCallback(context, color.lottieLairName, color.colorAttrRes)
                    }
                    onSuccess(this)
                    playAnimation()
                    repeatCount = ValueAnimator.INFINITE
                }
            }
            .addFailureListener {
                val lottieImageName = context.resources.getResourceName(lottieRawRes)
                Timber.e("Ошибка загрузки lottie заглушки fileName = $lottieImageName")
                onFailure?.invoke()
            }
    }

    /**
     * Установка слушателя на слой с названием [colorName], для покраски его в цвет [colorAttr].
     */
    private fun LottieDrawable.addLottieValueCallback(context: Context, colorName: String, @AttrRes colorAttr: Int) {
        val keyPath = KeyPath("**", colorName, "Fill")
        val color = context.getColorFromAttr(colorAttr).takeUnless { it == 0 }
            ?: ContextThemeWrapper(context, RDesign.style.DefaultLightTheme).getColorFromAttr(colorAttr)
        addValueCallback(keyPath, LottieProperty.COLOR) { color }
        addValueCallback(keyPath, LottieProperty.OPACITY) { Color.alpha(color) * 100 / 255 }
    }
}
