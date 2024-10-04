package ru.tensor.sbis.appdesign.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.FragmentNavigationSampleDataBinding

/**
 * Экран с тестовыми данными для навигации.
 *
 * @author ma.kolpakov
 */
class NavigationSampleDataFragment : Fragment(R.layout.fragment_navigation_sample_data) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemType = arguments?.getString(ITEM_TYPE_KEY) ?: DEFAULT_ITEM_TYPE
        val viewBinding = FragmentNavigationSampleDataBinding.bind(view)
        viewBinding.navigationDataList.adapter = NavigationSampleDataAdapter(itemType)
    }

    companion object {
        private const val ITEM_TYPE_KEY = "item_type"
        private const val DEFAULT_ITEM_TYPE = "Unknown type"

        @JvmStatic
        fun newInstance(itemType: String) = NavigationSampleDataFragment().apply {
            arguments = Bundle().apply {
                putString(ITEM_TYPE_KEY, itemType)
            }
        }
    }
}