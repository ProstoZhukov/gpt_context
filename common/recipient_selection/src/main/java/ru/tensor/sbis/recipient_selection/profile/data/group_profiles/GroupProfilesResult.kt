package ru.tensor.sbis.recipient_selection.profile.data.group_profiles


class GroupProfilesResult(
    val status: RequestStatus,
    val errorGroup: GroupItem? = null
)

enum class RequestStatus {
    SUCCESS,
    ERROR
}
