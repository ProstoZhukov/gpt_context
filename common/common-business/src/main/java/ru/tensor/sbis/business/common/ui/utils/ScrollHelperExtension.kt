package ru.tensor.sbis.business.common.ui.utils

import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper

fun ScrollHelper.hideNavigationPanel() =
    sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)

fun ScrollHelper.showNavigationPanel() =
    sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)