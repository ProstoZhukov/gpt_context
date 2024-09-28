package ru.tensor.sbis.recipient_selection.profile.interactor

import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.DelegateListMapper
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupProfilesResult
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.RequestStatus
import ru.tensor.sbis.recipient_selection.profile.mapper.ContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Интерактор выбора получателей
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionInteractorImpl @Inject constructor(
    private val employeeProfileController: DependencyProvider<EmployeeProfileControllerWrapper>,
    wrappedContactItemMapper: ContactItemMapper,
) : BaseInteractor(),
    RecipientSelectionInteractor {

    private val wrappedContactListMapper = DelegateListMapper(wrappedContactItemMapper)

    override fun loadProfilesByGroups(groups: List<GroupItem>): Single<GroupProfilesResult> =
        Single.fromCallable {
            for (group in groups) {
                val personUuids = employeeProfileController.get().getProfilesByGroupUuid(group.group.uuid).map { it.uuid }
                // TODO Для некоторых групп количество сотрудников, возвращаемых из контроллера, больше чем реальное количество в группе.
                //  Метод getProfilesByGroupUuid должен быть перенесен в EmployeesController, возможно неправильное количество будет исправлено в процессе.
                //  До этого смягчил проверку на количество участников
                //  https://online.sbis.ru/opendoc.html?guid=eb53676b-8985-4e5f-a957-63da400d96db
                if (!group.isTaskType && personUuids.size < group.itemCount) {
                    return@fromCallable GroupProfilesResult(RequestStatus.ERROR, group)
                }
                group.group.personUuids = personUuids
                val persons = ArrayList<ContactVM>()
                val profiles = employeeProfileController.get().getEmployeeProfilesFromCache(personUuids)
                if (personUuids.size != profiles.size) {
                    return@fromCallable GroupProfilesResult(RequestStatus.ERROR, group)
                } else {
                    for (item in wrappedContactListMapper.apply(profiles)) {
                        persons.add(item.contact)
                    }
                    group.group.persons = persons
                }
            }
            return@fromCallable GroupProfilesResult(RequestStatus.SUCCESS)
        }
        .compose(getSingleBackgroundSchedulers())
}