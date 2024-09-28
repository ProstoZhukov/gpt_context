package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view

import android.view.View
import ru.tensor.sbis.common.util.ActivityStatusUtil
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.design.profile.person.ActivityStatusView
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.profiles.generated.Gender as ControllerGender

/**
 * Контроллер вью единственного участника на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
internal class ConversationInformationParticipantViewController: ConversationInformationParticipantViewAPI {

    /** Вью компонент. */
    var view: ConversationInformationParticipantView? = null

    private val personView: PersonView?
        get() = view?.findViewById(R.id.themes_registry_participant_view_photo_id)

    private val fullNameTextView: SbisTextView?
        get() = view?.findViewById(R.id.themes_registry_participant_view_full_name_id)

    private val positionTextView: SbisTextView?
        get() = view?.findViewById(R.id.themes_registry_participant_view_position_id)

    private val activityStatusTextView: SbisTextView?
        get() = view?.findViewById(R.id.themes_registry_participant_view_status_text_id)

    private val activityStatusView: ActivityStatusView?
        get() = view?.findViewById(R.id.themes_registry_participant_view_status_icon_id)

    override var viewData: ConversationInformationParticipantViewData? = null
        set(value) {
            field = value
            value?.let {
                personView?.setData(it.photoData)
                fullNameTextView?.text = it.fullName
                positionTextView?.text = it.position
                it.profileActivityStatus?.let { status ->
                    activityStatusView?.setActivityStatus(status.activityStatus)
                    activityStatusTextView?.text = ActivityStatusUtil.getActivityStateText(
                        view!!.context,
                        status,
                        it.gender.toUiGender()
                    )
                }
            }
        }

    private fun ControllerGender.toUiGender(): Gender = when (this) {
        ControllerGender.MALE -> Gender.MALE
        ControllerGender.FEMALE -> Gender.FEMALE
        ControllerGender.UNKNOWN -> Gender.UNKNOWN
    }

    override fun setOnPhotoClickListener(listener: View.OnClickListener?) {
        personView?.setOnClickListener(listener)
    }

    /** @SelfDocumented */
    fun initController(view: ConversationInformationParticipantView) {
        this.view = view
    }
}