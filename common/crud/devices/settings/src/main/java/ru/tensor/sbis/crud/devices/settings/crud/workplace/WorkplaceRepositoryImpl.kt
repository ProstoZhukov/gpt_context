package ru.tensor.sbis.crud.devices.settings.crud.workplace

import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*

/** @see WorkplaceRepository */
internal class WorkplaceRepositoryImpl(private val controller: DependencyProvider<WorkplaceFacade>) :
        WorkplaceRepository {

    override fun create(): Workplace =
        controller.get().create()

    override fun update(entity: Workplace): Workplace =
        controller.get().update(entity)

    override fun updateTry(workplace: Workplace): Workplace =
        controller.get().updateTry(workplace)

    override fun delete(uuid: UUID): Boolean =
            controller.get().delete(uuid)

    override fun read(uuid: UUID): Workplace =
            controller.get().read(uuid) ?: emptyWorkplace

    override fun readFromCache(uuid: UUID): Workplace =
            read(uuid)

    override fun list(filter: WorkplaceFilter): ListResultOfWorkplaceMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: WorkplaceFilter): ListResultOfWorkplaceMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedWorkplaceFacadeCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)

    override fun readId(workPlaceId: Long): Workplace =
            controller.get().readId(workPlaceId) ?: emptyWorkplace

    override fun fetch(): Workplace =
            controller.get().fetch() ?: emptyWorkplace

    override fun fetchNullable(): Workplace? = controller.get().fetch()

    override fun create(name: String, companyId: Long, deviceId: String?, deviceName: String?): Workplace =
            controller.get().create(name, companyId, deviceId, deviceName)

    override fun create(deviceId: String?, deviceName: String?): Workplace {
        return controller.get().create(deviceId, deviceName)
    }

    override fun delete(workPlaceId: Long): Boolean =
            controller.get().delete(workPlaceId)

    override fun deleteTry(workPlaceId: Long) =
            controller.get().deleteTry(workPlaceId)

    override fun getDefaultName(companyId: Long): String =
            controller.get().getDefaultName(companyId)

    override fun setCompanyTry(workplaceId: Long, companyId: Long) =
            controller.get().setCompanyTry(workplaceId, companyId)

    override fun setCompany(workplaceId: Long, companyId: Long) =
            controller.get().setCompany(workplaceId, companyId)

    override fun restore(workplaceId: Long) = controller.get().restore(workplaceId)

    override fun readCurrent() = controller.get().readCurrent()

    override val emptyWorkplace = Workplace().apply {
        settings.interfaceScale = InterfaceScale.LARGE
        settings.interfaceTheme = InterfaceTheme.LIGHT
    }
}
