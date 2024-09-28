package ru.tensor.sbis.base_components

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.base_components.fragment.FragmentBackPress

private const val DEFAULT_FRAGMENT_TAG = "DEFAULT_FRAGMENT_TAG"

/**
 * Класс активности-контейнера для одного фрагмента
 *
 * @author sa.nikitin
 */
abstract class FragmentContainerActivity : TrackingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isCustomLayout() || swipeBackEnabled()) {
            setContentView(getCustomLayoutRes())
        }
    }

    /** SelfDocumented */
    protected open fun replaceFragmentIfAbsent(
        createFragment: () -> Fragment,
        fragmentTag: String = getFragmentTag(),
        allowingStateLoss: Boolean = true
    ) {
        if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
            replaceFragment(createFragment(), fragmentTag, allowingStateLoss)
        }
    }

    /** SelfDocumented */
    protected open fun replaceFragment(
        fragment: Fragment,
        fragmentTag: String = getFragmentTag(),
        allowingStateLoss: Boolean = true
    ) {
        var fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(getFragmentContainerId(), fragment, fragmentTag)
        fragmentTransaction = editFragmentTransaction(fragmentTransaction)
        if (allowingStateLoss) {
            fragmentTransaction.commitAllowingStateLoss()
        } else {
            fragmentTransaction.commit()
        }
    }

    /** SelfDocumented */
    override fun swipeBackEnabled(): Boolean = false

    /**
     * Использовать ли конкретную разметку с контейнером для фрагмента.
     * Её идентификатор определяется через метод [getCustomLayoutRes]
     * Если false, то [setContentView] не будет вызван и фрагмент будет положен в контейнер с идентификатором [android.R.id.content]
     */
    protected open fun isCustomLayout(): Boolean = false

    /**
     * Идентификатор конкретной разметки с контейнером для фрагмента
     * Идентификатор контейнера определяется через метод [getCustomFragmentContainerId]
     */
    @LayoutRes
    protected open fun getCustomLayoutRes(): Int = R.layout.base_components_activity_fragment_container

    /**
     * Идентификатор контейнера для фрагмента
     */
    @IdRes
    protected open fun getCustomFragmentContainerId(): Int = R.id.base_components_content_container

    private fun getFragmentContainerId(): Int =
        if (isCustomLayout() || swipeBackEnabled()) {
            getCustomFragmentContainerId()
        } else {
            android.R.id.content
        }

    /** SelfDocumented */
    protected open fun getFragmentTag(): String = DEFAULT_FRAGMENT_TAG

    protected open fun editFragmentTransaction(fragmentTransaction: FragmentTransaction) = fragmentTransaction

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBackPressed() {
        val fragmentBackPress = supportFragmentManager.findFragmentByTag(getFragmentTag()) as? FragmentBackPress
        val backPressEventIntercepted = fragmentBackPress?.onBackPressed() ?: false
        if (!backPressEventIntercepted) {
            super.onBackPressed()
        }
    }
}