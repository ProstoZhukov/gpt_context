package ru.tensor.sbis.common.util;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.tensor.sbis.common.R;

/**
 * Utility methods for toolbar.
 *
 * @author am.boldinov
 */
public class ToolbarUtil {

    /**
     * Inflate sub content options with activity toolbar by default.
     *
     * @param subContentFragment - sub content fragment
     * @param toolbar            - activity toolbar
     */
    public static boolean inflateSubContentOptions(@NonNull Fragment subContentFragment, @NonNull Toolbar toolbar) {
        boolean isTablet = DeviceConfigurationUtils.isTablet(subContentFragment.requireContext());
        return inflateSubContentOptions(subContentFragment, toolbar, isTablet);
    }

    /**
     * Inflate sub content options with activity toolbar depending on purpose of using.
     *
     * @param subContentFragment - sub content fragment
     * @param toolbar            - activity toolbar
     * @param forSubContent      - true if using for sub content
     */
    public static boolean inflateSubContentOptions(@NonNull Fragment subContentFragment, @NonNull Toolbar toolbar, boolean forSubContent) {
        if (forSubContent) {
            // For tablet
            // Manually update toolbar menu options
            Menu menu = toolbar.getMenu();
            menu.clear();
            Activity activity = subContentFragment.getActivity();
            assert activity != null;
            subContentFragment.onCreateOptionsMenu(menu, activity.getMenuInflater());
            // Manually set menu options listener
            toolbar.setOnMenuItemClickListener(subContentFragment::onOptionsItemSelected);
            return true;
        } else {
            // Request fragment menu options
            subContentFragment.setHasOptionsMenu(true);
            Activity activity = subContentFragment.getActivity();
            if (activity instanceof AppCompatActivity) {
                // Set fragment toolbar as action bar of activity
                ((AppCompatActivity) activity).setSupportActionBar(toolbar);
                return true;
            }
            return false;
        }
    }

    /**
     * Set toolbar option visibility.
     *
     * @param toolbar    - toolbar
     * @param optionId   - option
     * @param visibility - visibility
     */
    public static void setOptionVisibility(@NonNull Toolbar toolbar, int optionId, boolean visibility) {
        setOptionVisibility(toolbar.getMenu(), optionId, visibility);
    }

    /**
     * Set menu option visibility.
     *
     * @param menu       - toolbar menu
     * @param optionId   - option
     * @param visibility - visibility
     */
    public static void setOptionVisibility(@Nullable Menu menu, int optionId, boolean visibility) {
        if (menu != null) {
            MenuItem item = menu.findItem(optionId);
            if (item != null && item.isVisible() != visibility) {
                item.setVisible(visibility);
            }
        }
    }

}
