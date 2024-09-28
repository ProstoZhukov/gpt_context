package ru.tensor.sbis.communicator_support_channel_list.utills

import android.view.View
import ru.tensor.sbis.common.provider.BottomBarProvider

/**
 * Заглушка для BottomBarProvider, где все методы реализованы как пустые
 */
internal class BottomBarProviderAdapter : BottomBarProvider {
    override fun setNavigationFabClickListener(navigationFabClickListener: View.OnClickListener?) = Unit

    override fun setExtraFabClickListener(extraFabClickListener: View.OnClickListener?) = Unit

    override fun setExtraFab2ClickListener(extraFabClickListener: View.OnClickListener?) = Unit

    override fun setExtraFab3ClickListener(extraFabClickListener: View.OnClickListener?) = Unit

    override fun setExtraFab4ClickListener(extraFabClickListener: View.OnClickListener?) = Unit

    override fun showExtraFabButton() = Unit

    override fun showExtraFab2Button() = Unit

    override fun showExtraFab3Button() = Unit

    override fun showExtraFab4Button() = Unit

    override fun hideExtraFabButton() = Unit

    override fun hideExtraFab2Button() = Unit

    override fun hideExtraFab3Button() = Unit

    override fun hideExtraFab4Button() = Unit

    override fun swapFabButton(isAddItemEnabled: Boolean) = Unit
}