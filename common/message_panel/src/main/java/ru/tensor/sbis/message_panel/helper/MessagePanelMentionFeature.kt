@file:OptIn(DelicateCoroutinesApi::class)

package ru.tensor.sbis.message_panel.helper

import android.annotation.SuppressLint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService

/**
 * Вспомогательная реализация для проверки состояния включенности фичи упоминаний в панели сообщений.
 *
 * @author vv.chekurda
 */
internal object MessagePanelMentionFeature {

    @SuppressLint("StaticFieldLeak")
    private var localFeatureService: LocalFeatureToggleService? = null

    private val isLocalFeatureActive: Boolean
        get() = localFeatureService?.isFeatureActivated(FeatureSet.MENTIONS) == true

    private var isCloudFeatureActive: Boolean = false

    /**
     * Признак активированности фичи упоминаний.
     * Учитывается и локальная фича, и облачная для внедрения.
     */
    val isActive: Boolean
        get() = isLocalFeatureActive || isCloudFeatureActive

    fun init(localFeatureService: LocalFeatureToggleService?, featureService: SbisFeatureService?) {
        this.localFeatureService = localFeatureService
        GlobalScope.launch(Dispatchers.Main) {
            featureService?.getFeatureInfoFlow(listOf(MENTION_FEATURE_NAME))
                ?.collect {
                    isCloudFeatureActive = it.state == true
                }
        }
    }
}

private const val MENTION_FEATURE_NAME = "msg_mention_mobile"