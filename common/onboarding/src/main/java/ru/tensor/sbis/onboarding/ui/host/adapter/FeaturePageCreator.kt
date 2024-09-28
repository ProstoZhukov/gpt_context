package ru.tensor.sbis.onboarding.ui.host.adapter

import androidx.fragment.app.Fragment

/**
 * Интерфейс создателя страницы фичи приветственного экрана
 *
 * @author as.chadov
 */
internal interface FeaturePageCreator {

    /**
     * @return новый экземпляр страницы фичи приветственного экрана
     */
    fun createFeaturePage(params: PageParams): Fragment

    interface Provider {
        val creator: FeaturePageCreator
    }
}