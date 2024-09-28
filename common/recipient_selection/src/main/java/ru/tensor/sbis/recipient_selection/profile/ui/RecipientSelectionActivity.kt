package ru.tensor.sbis.recipient_selection.profile.ui

import android.os.Bundle
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilter
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionComponentProvider
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import ru.tensor.sbis.design.R as RDesign

internal open class RecipientSelectionActivity : AdjustResizeActivity() {

    private var selectionManager: RecipientSelectionResultManager? = null

    /**
     * Отметка о том, что пользователь выбрал адресатов.
     *
     * Так как метод [onBackPressed] вызывается в обоих сценариях (при выборе и отмене), нужна дополнительная информация
     * о том, какой именно сценарий отрабатывает. Размещение вызова [RecipientSelectionResultManager.putResultCanceled]
     * снаружи недостаточно, т.к. вызовы системной кнопки "Назад" доставляются непосредственно в метод [onBackPressed]
     * (минуя логику отмены в компоненте выбора)
     */
    var isSelectionCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(R.layout.recipient_selection_activity_add_contacts)
        if (savedInstanceState == null) {
            initializeSelectionFragment()
        }
        initSelectionManager()
    }

    private fun initSelectionManager() {
        selectionManager = RecipientSelectionComponentProvider
                .getRecipientSelectionSingletonComponent(this)
                .getRecipientSelectionResultManager()
    }

    protected open fun initializeSelectionFragment() {
        intent.extras?.let {
            val fragment = createRecipientSelectionFragment(RecipientSelectionFilter(it))
            supportFragmentManager.beginTransaction()
                    .replace(contentViewId, fragment, fragment::class.java.simpleName)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (!isSelectionCompleted) {
            selectionManager?.putResultCanceled()
        }
        val fragment = supportFragmentManager.findFragmentById(contentViewId)
        if (!(fragment is FragmentBackPress && (fragment as FragmentBackPress).onBackPressed())) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun onViewGoneBySwipe() {
        selectionManager?.putResultCanceled()
        super.onViewGoneBySwipe()
    }

    override fun onDestroy() {
        super.onDestroy()
        selectionManager = null
    }

    override fun getContentViewId(): Int = R.id.contact_list_content

    override fun swipeBackEnabled(): Boolean = true
}