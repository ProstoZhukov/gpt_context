package ru.tensor.sbis.onboarding.ui.utils

internal interface RequestPermissionDelegate {

    fun requestPermissions(
        permissions: List<String>,
        requestCode: Int
    )
}