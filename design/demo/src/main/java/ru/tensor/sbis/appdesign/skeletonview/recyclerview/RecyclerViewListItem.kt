package ru.tensor.sbis.appdesign.skeletonview.recyclerview

import androidx.annotation.StringRes
import ru.tensor.sbis.appdesign.R

/**
 * @author us.merzlikina
 */
data class RecyclerViewListItem(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val avatarResId: Int
) {
    companion object {
        val DEMO_ITEMS = listOf(
            RecyclerViewListItem(R.string.user_0_name, R.string.user_0_statement, R.string.user_0_icon),
            RecyclerViewListItem(R.string.user_1_name, R.string.user_1_statement, R.string.user_1_icon),
            RecyclerViewListItem(R.string.user_2_name, R.string.user_2_statement, R.string.user_2_icon)
        )
    }
}