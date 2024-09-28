package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.retail_settings.generated.*
import java.util.*

internal class RetailSettingsRepositoryImpl(private val controller: DependencyProvider<SettingsFacade>) :
        RetailSettingsRepository {

    override fun create(): Settings =
            controller.get().create() ?: Settings(UUIDUtils.NIL_UUID)

    override fun update(entity: Settings): Settings =
            controller.get().update(entity) ?: Settings(entity.uuid)

    override fun delete(uuid: UUID): Boolean =
            controller.get().delete(uuid)

    override fun read(uuid: UUID): Settings =
            controller.get().read(uuid) ?: Settings(uuid)

    override fun readFromCache(uuid: UUID): Settings =
            read(uuid)

    override fun list(filter: SettingsFilter): ListResultOfSettingsMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: SettingsFilter): ListResultOfSettingsMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)

    override fun setSettings(allowCashierCancel: Boolean, returnRequireSale: Boolean) =
            controller.get().setRefusalSettings(allowCashierCancel, returnRequireSale)

}
