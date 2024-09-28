package ru.tensor.sbis.recipient_selection.profile.data.factory_models

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.communicator.generated.ProfilesFoldersResult as ControllerProfilesFoldersResult
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.design.utils.delegateProperty
import java.util.UUID

/**
 * Локальная модель результата от [RecipientsController], со вспомогательными утилитами и определением [equals] и
 * [hashCode] для корректного определения ключа при кэшировании результатов
 *
 * @author us.bessonov
 */
internal class ProfilesFoldersResult(
    private val controllerResult: ControllerProfilesFoldersResult
) {

    /** @SelfDocumented */
    var folders by delegateProperty(controllerResult::folders)
    /** @SelfDocumented */
    var profiles by delegateProperty(controllerResult::profiles)
    /** @SelfDocumented */
    var status by delegateProperty(controllerResult::status)
    /** @SelfDocumented */
    var hasMore by delegateProperty(controllerResult::hasMore)

    /**
     * Список uuid всех профилей и папок
     */
    val uuidList: List<UUID?>
        get() = profiles.map { it.person.uuid }.plus(folders.map { it.uuid })

    /**
     * Суммарное число профилей и папок
     */
    val size: Int
        get() = profiles.size + folders.size

    /** @SelfDocumented */
    val isEmpty: Boolean
        get() = size == 0

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(controllerResult.folders)
            .append(controllerResult.profiles)
            .append(controllerResult.status)
            .append(controllerResult.hasMore)
            .build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfilesFoldersResult

        controllerResult.apply {
            return EqualsBuilder()
                .append(folders, other.folders)
                .append(profiles, other.profiles)
                .append(status, other.status)
                .append(hasMore, other.hasMore)
                .build()
        }
    }

    override fun toString(): String = controllerResult.toString()
}
