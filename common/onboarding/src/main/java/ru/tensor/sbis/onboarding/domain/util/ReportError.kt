package ru.tensor.sbis.onboarding.domain.util

import ru.tensor.sbis.onboarding.BuildConfig
import ru.tensor.sbis.onboarding.domain.util.OnboardingIssue.CALL_NON_EXIST_PAGE
import ru.tensor.sbis.onboarding.domain.util.OnboardingIssue.NOT_FOUND_PAGE
import timber.log.Timber

internal enum class OnboardingIssue {
    NOT_FOUND_PAGE,
    CALL_NON_EXIST_PAGE
}

/**
 * Сообщить о возможной проблеме в работе компонента приветственного экрана
 */
internal fun reportIssue(
    issue: OnboardingIssue,
    pageUuid: String = "",
    pagePosition: Int = 0,
    pageCount: Int = 0
) {
    val message = when (issue) {
        NOT_FOUND_PAGE      -> "Not found content feature page for uuid $pageUuid"
        CALL_NON_EXIST_PAGE -> "Trying to create nonexistent page for position $pagePosition when page count is $pageCount"
    }
    reportIssue(message)
}

/**
 * Сообщить о возможной проблеме в работе компонента приветственного экрана
 */
internal fun reportIssue(message: String) {
    val isDebug = BuildConfig.DEBUG
    require(isDebug.not()) { message }
    Timber.e(IllegalArgumentException(message))
}