/**
 * Классы контента заглушки
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.stubview

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat.ID_NULL
import com.mikepenz.iconics.typeface.IIcon
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.stubview.actionrange.ActionRangeProvider
import ru.tensor.sbis.design.stubview.actionrange.ResourceActionRangeProvider
import ru.tensor.sbis.design.stubview.actionrange.StringActionRangeProvider

/**
 * Модель контента заглушки.
 *
 * Может содержать следующие компоненты в любых комбинациях:
 *  * Иконка;
 *  * Заголовок;
 *  * Описание.
 */
sealed class StubViewContent {

    /**
     * Текст заголовка, аналог [messageRes]
     */
    internal abstract val message: String?

    /**
     * Текст описания, аналог [detailsRes]
     */
    internal abstract val details: String?

    /**
     * Ресурс текста заголовка, аналог [message]
     */
    @get:StringRes
    internal abstract val messageRes: Int

    /**
     * Ресурс текста описания, аналог [details]
     */
    @get:StringRes
    internal abstract val detailsRes: Int

    /**
     * Набор действий <id_ресурса_текста, действие_на_клик>. Текст ресурса <id_ресурса_текста> в комментарии будет
     * выделен цветом и кликабелен. По клику произойдёт вызов лямбды <действие_на_клик>.
     */
    internal abstract val actions: Map<ActionRangeProvider, () -> Unit>
}

/**
 * Основной вариант модели контента заглушки с использованием абстрактного типа стандартного заглушечного изображения.
 *
 * @param imageType тип картинки заглушки
 * @param message сообщение заглушки
 * @param details дополнительное сообщение заглушки
 * @param messageRes ресурс сообщения заглушки
 * @param detailsRes ресурс дополнительного сообщения заглушки
 * @param actions набор действий
 */
data class ImageStubContent internal constructor(
    internal val imageType: StubViewImageType = StubViewImageType.EMPTY_STUB_IMAGE,
    override val message: String? = null,
    override val details: String? = null,
    @StringRes override val messageRes: Int = ID_NULL,
    @StringRes override val detailsRes: Int = ID_NULL,
    override val actions: Map<ActionRangeProvider, () -> Unit> = emptyMap(),
) : StubViewContent() {

    /**
     * @param imageType тип картинки заглушки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        imageType: StubViewImageType,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(imageType, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        imageType: StubViewImageType,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(imageType, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        imageType: StubViewImageType,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(imageType, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        imageType: StubViewImageType,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(imageType, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())

    /**
     * Использовать фабричные методы  их стоит только если для [details] и текст для [actions] формируются динамически.
     * В остальных случаях рекомендуется использовать конструкторы
     */
    companion object {

        /**
         * Фабричный метод для создания контента используя в качестве [actions] ассоциативный список с ключами
         * типа [String] а не [Int]. Ключ [Map.Entry.key], является строкой, которая будет выделена цветом в
         * комментарии заглушки и по клику по ней будет вызываться лямбда из поля [Map.Entry.value] элемента списка.
         */
        fun createContent(
            imageType: StubViewImageType = StubViewImageType.EMPTY_STUB_IMAGE,
            message: String? = null,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ImageStubContent(imageType, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

        /** @SelfDocumented */
        fun createContent(
            imageType: StubViewImageType = StubViewImageType.EMPTY_STUB_IMAGE,
            @StringRes messageRes: Int = ID_NULL,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ImageStubContent(imageType, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
    }
}

/**
 * Вариант модели контента заглушки с [Drawable] в качестве иконки.
 *
 * @param icon ресурс иконки
 */
@Suppress("unused")
data class DrawableImageStubContent internal constructor(
    internal val icon: Drawable,
    override val message: String?,
    override val details: String?,
    @StringRes override val messageRes: Int,
    @StringRes override val detailsRes: Int,
    override val actions: Map<ActionRangeProvider, () -> Unit>,
) : StubViewContent() {

    /**
     * @param icon ресурс иконки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        icon: Drawable,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: Drawable,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: Drawable,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: Drawable,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
}

/**
 * Вариант модели контента заглушки с [DrawableRes] в качестве иконки.
 *
 * @param icon ресурс иконки
 */
data class ResourceImageStubContent internal constructor(
    @DrawableRes internal val icon: Int = ID_NULL,
    override val message: String? = null,
    override val details: String? = null,
    @StringRes override val messageRes: Int = ID_NULL,
    @StringRes override val detailsRes: Int = ID_NULL,
    override val actions: Map<ActionRangeProvider, () -> Unit> = emptyMap(),
) : StubViewContent() {

    /**
     * @param icon ресурс иконки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        @DrawableRes icon: Int = ID_NULL,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @DrawableRes icon: Int = ID_NULL,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @DrawableRes icon: Int = ID_NULL,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @DrawableRes icon: Int = ID_NULL,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())

    /**
     * Использовать фабричные методы  их стоит только если для [details] и текст для [actions] формируются динамически.
     * В остальных случаях рекомендуется использовать конструкторы
     */
    companion object {

        /**
         * Фабричный метод для создания контента используя в качестве [actions] ассоциативный список с ключами
         * типа [String] а не [Int]. Ключ [Map.Entry.key], является строкой, которая будет выделена цветом в
         * комментарии заглушки и по клику по ней будет вызываться лямбда из поля [Map.Entry.value] элемента списка.
         */
        fun createContent(
            @DrawableRes icon: Int = ID_NULL,
            message: String? = null,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ResourceImageStubContent(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

        /** @SelfDocumented */
        fun createContent(
            @DrawableRes icon: Int = ID_NULL,
            @StringRes messageRes: Int = ID_NULL,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ResourceImageStubContent(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
    }
}

/**
 * Вариант модели контента заглушки с [AttrRes] в качестве иконки.
 *
 * @param icon атрибут иконки
 */
data class ResourceAttributeStubContent internal constructor(
    @AttrRes internal val icon: Int = ID_NULL,
    override val message: String? = null,
    override val details: String? = null,
    @StringRes override val messageRes: Int = ID_NULL,
    @StringRes override val detailsRes: Int = ID_NULL,
    override val actions: Map<ActionRangeProvider, () -> Unit> = emptyMap(),
) : StubViewContent() {

    /**
     * @param icon атрибут иконки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        @AttrRes icon: Int = ID_NULL,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @AttrRes icon: Int = ID_NULL,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @AttrRes icon: Int = ID_NULL,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        @AttrRes icon: Int = ID_NULL,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())

    /**
     * Использовать фабричные методы  их стоит только если для [details] и текст для [actions] формируются динамически.
     * В остальных случаях рекомендуется использовать конструкторы
     */
    companion object {

        /**
         * Фабричный метод для создания контента используя в качестве [actions] ассоциативный список с ключами
         * типа [String] а не [Int]. Ключ [Map.Entry.key], является строкой, которая будет выделена цветом в
         * комментарии заглушки и по клику по ней будет вызываться лямбда из поля [Map.Entry.value] элемента списка.
         */
        fun createContent(
            @AttrRes icon: Int = ID_NULL,
            message: String? = null,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ResourceAttributeStubContent(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

        /** @SelfDocumented */
        fun createContent(
            @AttrRes icon: Int = ID_NULL,
            @StringRes messageRes: Int = ID_NULL,
            details: String,
            actions: Map<String, () -> Unit>,
        ) = ResourceAttributeStubContent(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
    }
}

/**
 * Вариант модели контента заглушки кастомным [View] для отображения.
 *
 * @param icon кастомное view иконки
 */
@Suppress("unused")
data class ViewStubContent internal constructor(
    internal val icon: View,
    override val message: String? = null,
    override val details: String? = null,
    @StringRes override val messageRes: Int = ID_NULL,
    @StringRes override val detailsRes: Int = ID_NULL,
    override val actions: Map<ActionRangeProvider, () -> Unit> = emptyMap(),
) : StubViewContent() {

    /**
     * @param icon кастомное view иконки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        icon: View,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: View,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: View,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: View,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
}

/**
 * Вариант модели контента заглушки с использованием иконок из [SbisMobileIcon.Icon].
 *
 * @param icon иконка
 * @param iconColor ресурс цвета иконки
 * @param iconSize ресурс размера иконки
 */
data class IconStubContent internal constructor(
    internal val icon: IIcon,
    @ColorRes internal val iconColor: Int,
    @DimenRes internal val iconSize: Int,
    override val message: String? = null,
    override val details: String? = null,
    @StringRes override val messageRes: Int = ID_NULL,
    @StringRes override val detailsRes: Int = ID_NULL,
    override val actions: Map<ActionRangeProvider, () -> Unit> = emptyMap(),
) : StubViewContent() {

    /**
     * @param icon иконка
     * @param iconColor ресурс цвета иконки
     * @param iconSize ресурс размера иконки
     * @param message сообщение заглушки
     * @param details дополнительное сообщение заглушки
     * @param actions ассоциативный список где ключом является id строкового ресурса, который будет подсвечиваться в
     * комментарии заглушки, и по клику на этот текст будет вызываться лямбда из [Map.Entry.value] поля.
     */
    constructor(
        icon: IIcon,
        @ColorRes iconColor: Int,
        @DimenRes iconSize: Int,
        message: String? = null,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, iconColor, iconSize, message, details, ID_NULL, ID_NULL, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: IIcon,
        @ColorRes iconColor: Int,
        @DimenRes iconSize: Int,
        @StringRes messageRes: Int = ID_NULL,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, iconColor, iconSize, null, null, messageRes, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: IIcon,
        @ColorRes iconColor: Int,
        @DimenRes iconSize: Int,
        message: String? = null,
        @StringRes detailsRes: Int = ID_NULL,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, iconColor, iconSize, message, null, ID_NULL, detailsRes, actions.toRangeProviderMap())

    /** @SelfDocumented */
    constructor(
        icon: IIcon,
        @ColorRes iconColor: Int,
        @DimenRes iconSize: Int,
        @StringRes messageRes: Int = ID_NULL,
        details: String? = null,
        actions: Map<Int, () -> Unit> = emptyMap(),
    ) : this(icon, iconColor, iconSize, null, details, messageRes, ID_NULL, actions.toRangeProviderMap())
}

@JvmName("toRangeProviderIntMap")
private fun Map<Int, () -> Unit>.toRangeProviderMap(): Map<ActionRangeProvider, () -> Unit> =
    map { ResourceActionRangeProvider(it.key) to it.value }.toMap()

@JvmName("toRangeProviderStringMap")
private fun Map<String, () -> Unit>.toRangeProviderMap(): Map<ActionRangeProvider, () -> Unit> =
    map { StringActionRangeProvider(it.key) to it.value }.toMap()
