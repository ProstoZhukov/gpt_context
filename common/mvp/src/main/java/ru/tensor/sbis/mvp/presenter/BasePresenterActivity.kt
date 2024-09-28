@file:Suppress("KDocUnresolvedReference", "SpellCheckingInspection")

package ru.tensor.sbis.mvp.presenter

import android.os.Bundle
import ru.tensor.sbis.base_components.BaseActivity
import ru.tensor.sbis.mvp.presenter.activity.ArchDelegate
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable
import ru.tensor.sbis.verification_decl.auth.AuthAware

/**
 * Базовый класс презентера активити
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BasePresenterActivity<VIEW, PRESENTER : BasePresenter<VIEW>> : BaseActivity(),
    UserActivityTrackable,
    AuthAware {

    //region properties
    @Suppress("LeakingThis")
    protected val presenter: PRESENTER by ArchDelegate(
        this,
        creatingMethod = this::createPresenter,
        doOnCleared = { it.onDestroy() })

    @Deprecated("Оставлен для обратной совместимости", ReplaceWith("presenter для Kotlin или getPresenter() для Java"))
    protected lateinit var mPresenter: PRESENTER
    //endregion

    // region UserActivityTrackable
    override val isTrackActivityEnabled: Boolean
        get() = needTracking()

    override val screenName: String by lazy {
        javaClass.name
    }
    // endregion

    /**
     * Предназначен для инъекции зависимости посредством компонентов di
     */
    protected abstract fun inject()

    /**
     * Должен создаваться НОВЫЙ объект презентера, с которым будет работать Activity. Метод вызывается единожды, как только Activity создана.
     * После этого, для использования самого объекта презентера необходимо использовать [presenter]
     * @return PRESENTER объект презентера
     */
    protected abstract fun createPresenter(): PRESENTER

    /**
     * Должен предоставить вью для прикрепления к презентеру, как правило, передается указатель на текущий фрагмент
     * @see ru.tensor.sbis.folderspanel.BaseFolderPickPanel.getPresenterView
     * @return VIEW
     */
    protected abstract fun getPresenterView(): VIEW

    /**
     * При создании Fragment, происходит получение презентера, либо через создание нового объекта, либо получение ранее созданного(например, до поворота экрана).
     * Здесь, непосредственно перед вызовом метода [createPresenter], происходит вызов метода иньекции [inject]
     * @param savedInstanceState [Bundle]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        mPresenter = presenter
    }

    /**
     * Проверка необходимости включать трекинг активности пользователя
     */
    protected open fun needTracking(): Boolean = true

    /**
     * Прикрепление вью к презентеру
     */
    protected fun onCreated() {
        presenter.attachView(getPresenterView())
    }

    /**
     * Открепление вью от презентера
     */
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    //region deprecated method
    /**
     * Раньше использовался для получения идентификатора [androidx.loader.content.Loader], когда с помощью этого механизма выполнялось сохранение презентера
     * @return Int идентификатор Loader
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Больше не используется")
    protected open fun getPresenterLoaderId(): Int {
        return 0
    }
    //endregion
}
