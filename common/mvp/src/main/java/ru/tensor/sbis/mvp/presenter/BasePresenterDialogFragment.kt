@file:Suppress("KDocUnresolvedReference", "SpellCheckingInspection")

package ru.tensor.sbis.mvp.presenter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.mvp.presenter.fragment.ArchDelegate

/**
 * Базовых фрагмент для обслуживания логики инициализации презентера и сохранение его состояния в течении жизненного цикла DialogFragment
 * @param VIEW тип вью, с которой должен взаимодействовать презентер. Вью может быть прикреплена(Attach) к фрагменту или откреплена(Detach) о него в течении жизненного цикла Fragment с захватом и освобождение5м ссылки на него в презентере. Взаимодействие с вью должно происходить посредством вызова методов интерфейса вью с обязательной проверкой не является ли null ссылка на вью
 * @param PRESENTER [BasePresenter]
 * @property mPresenter объект презентера для доступа к нему из наследованных фрагментов
 * @property presenterKey [String] идентификатор фрагмента для конкретизации презентера. Если для нескольких фрагментов задан одинаковый ключ, то они будут получать один общий объект презентера
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BasePresenterDialogFragment<VIEW, PRESENTER : BasePresenter<VIEW>> : DialogFragment() {

    //region properties
    @Suppress("LeakingThis")
    protected val presenter: PRESENTER by ArchDelegate(
        this,
        creatingMethod = this::createPresenter,
        doOnCleared = { it.onDestroy() })

    @Deprecated("Оставлен для обратной совместимости", ReplaceWith("presenter для Kotlin или getPresenter() для Java"))
    protected lateinit var mPresenter: PRESENTER
    //endregion

    /**
     * Должен создаваться НОВЫЙ объект презентера, с которым будет работать Fragment. Метод вызывается единожды, как только фрагмент создан.
     * После этого, для использования самого объекта презентера необходимо использовать [presenter]
     * @return PRESENTER объект презентера
     */
    protected abstract fun createPresenter(): PRESENTER

    /**
     * Предназначен для инъекции зависимости посредством компонентов di
     */
    protected abstract fun inject()

    /**
     * Должен предоставить вью для прикрепления к презентеру, как правило, передается указатель на текущий фрагмент
     * @see ru.tensor.sbis.folderspanel.BaseFolderPickPanel.getPresenterView
     * @return VIEW
     */
    protected abstract fun getPresenterView(): VIEW

    //region fragment lifecycle methods
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
     * Прикрепление вью к презентеру
     * @param view [View]
     * @param savedInstanceState [Bundle]
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(getPresenterView())
    }

    /**
     * Открепление вью от презентера
     */
    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
    //endregion

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