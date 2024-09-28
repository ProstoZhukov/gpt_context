@file:Suppress("PrivatePropertyName")

package ru.tensor.sbis.mvp.fragment.selection

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.base_components.R
import ru.tensor.sbis.base_components.databinding.BaseComponentsSelectionWindowHeaderBinding
import ru.tensor.sbis.base_components.fragment.selection.SelectionWindowLayout
import ru.tensor.sbis.base_components.fragment.selection.ViewDependencyProvider
import ru.tensor.sbis.base_components.fragment.selection.shadow.SelectionWindowShadowBehavior
import ru.tensor.sbis.base_components.fragment.selection.shadow.ShadowVisibilityDispatcher
import ru.tensor.sbis.common.util.date.BaseDateUtils
import ru.tensor.sbis.common.util.getTargetFragmentAs
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common_filters.FilterWindowHeaderItem
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.utils.mergeAttrsWithCurrentTheme
import ru.tensor.sbis.design_dialogs.dialogs.container.util.restrictDialogContentWidthOnTablet
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import kotlin.math.abs

/**
 * Created by aa.mironychev on 10.05.2018.
 */
/**
 * Базовый фрагмент, стилизованный под "Окно выбора".
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class SelectionWindowFragment<
    V : SelectionWindowContract.View,
    P : SelectionWindowContract.Presenter<V>
    > : BasePresenterFragment<V, P>(), SelectionWindowContract.View, ViewDependencyProvider {

    /**
     * Индикатор загрузки.
     */
    private var mProgressBar: SbisLoadingIndicator? = null

    /**
     * Объект для отправки сообщений [mProgressBar]
     */
    private val mProgressBarHandler = Handler()

    /**
     * Полупрозрачный фон.
     */
    private var mFadingBackground: View? = null

    /**
     * Корневой view.
     */
    private var mWindowContainer: SelectionWindowLayout? = null

    /**
     * Верняя часть окна с кнопкой "Закрыть".
     */
    private var mCloseHood: View? = null

    /**
     * Контейнер шапки окна.
     */
    private var mHeaderContainer: FrameLayout? = null

    /**
     * Тень шапки окна.
     */
    private var mHeaderShadow: View? = null

    private var mDivider: View? = null

    /**
     * Кнопка "Применить" в шапке
     */
    private var mAcceptButton: View? = null

    /**
     * Контейнер контента.
     */
    private var mContentContainer: FrameLayout? = null

    private var hideAnimationIsRunning = false

    /**
     * Флаг, сигнализирующий о полном открытии панели
     * и готовности к взаимодействию с пользователем.
     */
    @JvmField
    protected var mPaneShown = false

    /**
     * Флаг, сигнализирующий о выполняющемся действии закрытия панели.
     */
    private var mPaneClosed = false

    /**
     *  Использовать анимацию при появлении
     */
    protected var useAnimationForAppear = true

    //В случае прерывания анимации (например, при переходе на домашний экран) событие onBackPressed,
    //вызываемое при ее окончании, не будет обработано. В таком случае необходимо откатить анимацию
    //при следующем старте
    private var revertAnimationOnStart = false

    private var mWindowContainerPreviousHeight = 0F

    private var defaultHeaderBinding: BaseComponentsSelectionWindowHeaderBinding? = null

    //Слушатель, отвечающий за обработку добавления в список новых фильтров и
    //изменение высоты RecyclerView в тех случаях, когда это необходимо
    private var onPreDrawListener: ViewTreeObserver.OnPreDrawListener? = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            mWindowContainer?.viewTreeObserver?.removeOnPreDrawListener(this)
            if (context == null) {
                // Панель может быть удалена быстрее, чем сработает onPreDraw, в таком случае контекст будет null
                return true
            }

            //Отрисовать список без анимации если его высота не изменилась
            if (mWindowContainerPreviousHeight == mWindowContainer?.height?.toFloat()) {
                return true
            }

            if (useAnimationForAppear) {
                getAnimatorSet(true, getAppearAnimationListener(), getAnimationDuration()).start()
            } else {
                onAppearAnimationEnd()
            }
            return false
        }
    }

    /**
     * @see [SelectionWindowContent.setHeaderViewModel]
     */
    fun setHeaderViewModel(vm: FilterWindowHeaderItem) {
        val binding = checkNotNull(defaultHeaderBinding) {
            "Cannot set viewModel. Make sure header view is created and you are not using custom header"
        }
        binding.viewModel = vm
        binding.executePendingBindings()
    }

    /**
     * @see [SelectionWindowContent.updateHeaderViewModel]
     */
    fun updateHeaderViewModel(update: FilterWindowHeaderItem.() -> FilterWindowHeaderItem) {
        val viewModel = defaultHeaderBinding?.viewModel
            ?: FilterWindowHeaderItem()
        setHeaderViewModel(viewModel.update())
    }

    private fun getAppearAnimationListener(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                onAppearAnimationEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                mPresenter.onAppearAnimationCompleted()
            }

            override fun onAnimationStart(animation: Animator) {
                mPresenter.onAppearAnimationStarted()
            }
        }
    }

    private fun onAppearAnimationEnd() {
        mWindowContainerPreviousHeight = mWindowContainer?.height?.toFloat() ?: 0F
        mWindowContainer?.clearAnimation()
        mPresenter.onAppearAnimationCompleted()
        mPaneShown = true
    }

    private fun getAnimatorSet(appear: Boolean, listener: Animator.AnimatorListener, duration: Long): AnimatorSet {
        val animatorSet = AnimatorSet()
        animatorSet.duration = duration
        animatorSet.playTogether(getAnimatorFromProgressBarState(appear))
        animatorSet.addListener(listener)
        return animatorSet
    }

    private fun getAnimationDuration(): Long =
        abs(
            ((mWindowContainer?.height?.toFloat() ?: mWindowContainerPreviousHeight - mWindowContainerPreviousHeight)
                / (resources.displayMetrics.density * 2)).toLong()
        )


    private fun getTranslateAnimation(slideIn: Boolean): ObjectAnimator? {
        return view?.let { root ->
            mWindowContainer?.let {
                val from: Float
                val to: Float
                if (slideIn) {
                    from = root.height - mWindowContainerPreviousHeight
                    to = (root.height - it.height).toFloat()
                } else {
                    to = root.height.toFloat()
                    from = (root.height - it.height).toFloat()
                }
                ObjectAnimator.ofFloat(it, "y", from, to)
            }
        }
    }

    private fun getFadeAnimation(fadeIn: Boolean): ObjectAnimator? {
        val to = if (fadeIn) 1f else 0f
        return mFadingBackground?.let { ObjectAnimator.ofFloat(it, "alpha", to) }
    }

    /**
     * Включить/отключить скроллирование шапки.
     */
    protected fun setHoodScrollingEnabled(enable: Boolean) {
        mWindowContainer?.isHoodScrollingEnabled = enable
    }

    // region SelectionWindowContract.View impl

    override fun showAppearAnimation() {
        mProgressBarHandler.removeCallbacksAndMessages(null)
        mProgressBar?.visibility = INVISIBLE
        mCloseHood?.visibility = VISIBLE
        if (hasHeader()) {
            mHeaderContainer?.visibility = VISIBLE
        }
        mContentContainer?.visibility = VISIBLE
        mWindowContainer?.visibility = VISIBLE
        mWindowContainer?.viewTreeObserver?.addOnPreDrawListener(onPreDrawListener)
    }

    override fun closeWindow() {
        if (!mPaneShown || mPaneClosed) {
            return
        }
        mPaneClosed = true
        closeWithAnimation()
    }

    private fun closeWithAnimation() {
        //Сброс значения предыдущей высоты для правильного расчета длительности анимации исчезновения
        mWindowContainerPreviousHeight = 0F

        getAnimatorSet(
            false,
            object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    hideAnimationIsRunning = false
                    mPaneShown = false
                    mPaneClosed = false
                    removeItself()
                    getTargetFragmentAs<Listener>()?.onSelectionWindowClosed(this@SelectionWindowFragment)
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationStart(animation: Animator) {
                    hideAnimationIsRunning = true
                }
            },
            getAnimationDuration()
        ).start()
    }

    // endregion

    /** @SelfDocumented */
    protected open fun isAcceptButtonVisible(): Boolean = false

    /**
     * @see [SelectionWindowContent.inflateHeaderView]
     */
    protected open fun inflateHeaderView(inflater: LayoutInflater, container: ViewGroup) {
        container.addView(createDefaultHeader(inflater, container))
    }

    /**
     * Наполнить контент.
     */
    protected abstract fun inflateContentView(inflater: LayoutInflater, container: ViewGroup)

    /** @SelfDocumented */
    protected open fun hasHeader() = true

    /** @SelfDocumented */
    protected open fun removeItself() {
        fragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commitAllowingStateLoss()
    }

    // region Show progress

    private var mLoadingBeginTimestamp = BaseDateUtils.currentTimestamp()
    private val LOADING_TIMEOUT: Long = 1000

    override fun showProgress() {
        // скрытие области контента, торчащей внизу
        mWindowContainer?.visibility = INVISIBLE
        // затемнение фона
        mFadingBackground?.alpha = 1.0f
        // отображение индикатора загрузки
        val timeout = LOADING_TIMEOUT - BaseDateUtils.currentTimestamp() + mLoadingBeginTimestamp
        if (timeout > 0) {
            mProgressBarHandler.postDelayed({ mProgressBar?.visibility = VISIBLE }, timeout)
        } else {
            mProgressBar?.visibility = VISIBLE
        }
    }

    private fun getAnimatorFromProgressBarState(appear: Boolean): List<ObjectAnimator> {
        return if (mProgressBar?.visibility == VISIBLE) {
            // только аниматор возникновения
            listOfNotNull(getTranslateAnimation(appear))
        } else {
            // аниматор затемнения фона + аниматор возникновения
            listOfNotNull(getFadeAnimation(appear), getTranslateAnimation(appear))
        }
    }
    // endregion

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().mergeAttrsWithCurrentTheme(
            R.attr.base_components_selection_window_content_theme,
            R.style.SelectionWindowContentTheme
        )
        val root = inflater.inflate(R.layout.base_components_fragment_selection_window, container, false)
        // Инициализируем компоненты экрана
        initViews(root, savedInstanceState)
        // Наполняем шапку
        mHeaderContainer?.let {
            inflateHeaderView(
                inflater,
                it
            )
        }
        // Наполняем контентную область
        mContentContainer?.let {
            inflateContentView(
                inflater,
                it
            )
        }
        // Задаем диспетчер видимости тени
        initShadowVisibilityDispatcher()
        // Восстанавливаем состояние
        onRestoreInstanceState(savedInstanceState)
        // Инициализируем слушатели
        initListeners(root, savedInstanceState)

        // Ограничиваем ширину окна в конфигурации планшета
        if (isTablet) {
            mWindowContainer?.let { restrictDialogContentWidthOnTablet(it) }
        }

        if (savedInstanceState != null) {
            mCloseHood?.visibility = VISIBLE
            mHeaderContainer?.visibility = VISIBLE
            mContentContainer?.visibility = VISIBLE
        }

        if (!hasHeader()) {
            mHeaderContainer?.visibility = View.GONE
            mHeaderShadow?.visibility = View.GONE
            mDivider?.visibility = View.GONE
        }

        if (!isAcceptButtonVisible()) {
            mAcceptButton?.visibility = View.GONE
        }

        return root
    }

    /**
     * Инициализируем компоненты экрана.
     */
    @CallSuper
    protected open fun initViews(root: View, savedInstanceState: Bundle?) {
        mProgressBar = root.findViewById(R.id.base_components_progress_bar)
        mFadingBackground = root.findViewById(R.id.base_components_fading_background)
        mWindowContainer = root.findViewById(R.id.base_components_window_container)
        mCloseHood = root.findViewById(R.id.base_components_hood_container)
        mHeaderContainer = root.findViewById(R.id.base_components_header_container)
        mHeaderShadow = root.findViewById(R.id.base_components_app_bar_shadow)
        mContentContainer = root.findViewById(R.id.base_components_content_container)
        mDivider = root.findViewById(R.id.base_components_app_bar_divider)
        mAcceptButton = root.findViewById(R.id.base_components_header_button_accept)
    }

    /**
     * Инициализируем слушатели.
     */
    @CallSuper
    protected open fun initListeners(root: View, savedInstanceState: Bundle?) {
        // Слушаем кнопку "Закрыть" и пустую область над ней
        mCloseHood?.setOnClickListener { mPresenter.onCloseClick() }
        mFadingBackground?.setOnClickListener { mPresenter.onCloseClick() }

        // Слушаем нажатия на стрелку "Назад"
        root.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                mPresenter.onBackPressed()
                return@OnKeyListener true
            }
            false
        })
        mCloseHood?.setOnClickListener { mPresenter.onCloseClick() }
        mAcceptButton?.setOnClickListener { onAcceptButtonClick() }

        root.isFocusableInTouchMode = true
        root.requestFocus()
    }

    private fun createDefaultHeader(inflater: LayoutInflater, container: ViewGroup): View {
        return BaseComponentsSelectionWindowHeaderBinding.inflate(inflater, container, false)
            .apply { viewModel = FilterWindowHeaderItem() }
            .also { defaultHeaderBinding = it }
            .root
    }

    /**
     * Инициализируем диспетчер видимости тени.
     */
    private fun initShadowVisibilityDispatcher() {
        val dispatcher = getShadowVisibilityDispatcher()
        val params = mHeaderShadow?.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            val behavior = params.behavior
            if (behavior is SelectionWindowShadowBehavior) {
                behavior.setDispatcher(dispatcher)
                behavior.setupDependency(this)
            }
        }
    }

    /**
     * Задать видимость кнопки подтверждения (не видна по дефолту)
     */
    protected fun setAcceptButtonVisible(visible: Boolean) {
        mAcceptButton?.visibility = if (visible) VISIBLE else View.GONE
    }

    /**
     * Получить иконку для плавающей кнопки
     */
    protected fun getFabIcon(): SbisMobileIcon.Icon {
        return SbisMobileIcon.Icon.smi_checked
    }

    /**
     * Возвращает диспетчер, отвечающий за видимость тени под заголовом.
     */
    protected abstract fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher?

    /**
     * Обработчик нажатия на кнопку подтверждения
     */
    protected open fun onAcceptButtonClick() {
        checkNotNull(mPresenter as? SelectionWindowContract.OnApplyClickListener) {
            "${javaClass.simpleName}#Presenter should implement SelectionWindowContract.OnApplyClickListener!"
        }.onApplyClick()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mFadingBackground?.let { outState.putFloat(EXTRA_BACKGROUND_ALPHA, it.alpha) }
        outState.putBoolean(EXTRA_PANE_SHOWN, mPaneShown)
        outState.putBoolean(EXTRA_PANE_CLOSED, mPaneClosed)
        outState.putLong(EXTRA_LOADING_BEGIN_TIMESTAMP, mLoadingBeginTimestamp)
    }

    protected open fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mFadingBackground?.alpha = savedInstanceState.getFloat(EXTRA_BACKGROUND_ALPHA, 0F)
            mPaneShown = savedInstanceState.getBoolean(EXTRA_PANE_SHOWN, mPaneShown)
            mPaneClosed = savedInstanceState.getBoolean(EXTRA_PANE_CLOSED, mPaneClosed)
            mLoadingBeginTimestamp = savedInstanceState.getLong(EXTRA_LOADING_BEGIN_TIMESTAMP, mLoadingBeginTimestamp)
        }
    }

    override fun onStart() {
        super.onStart()
        if (revertAnimationOnStart) {
            revertAnimationOnStart = false
            getAnimatorSet(true, getAppearAnimationListener(), 0).start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (hideAnimationIsRunning) {
            revertAnimationOnStart = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCloseHood?.setOnClickListener(null)
        mFadingBackground?.setOnClickListener(null)
        onPreDrawListener = null
        mProgressBar = null
        mFadingBackground = null
        mWindowContainer = null
        mCloseHood = null
        mHeaderContainer = null
        mHeaderShadow = null
        mDivider = null
        mAcceptButton = null
        mContentContainer = null
        defaultHeaderBinding = null
    }


    /**
     * Метод для совместимости с измененным SelectionWindowShadowBehavior
     */
    override fun getContentViewId(): Int {
        return getContentScrollViewId()
    }

    /**
     * Возвращает идентификатор скроллируемой view с контентом
     */

    @Deprecated("Следует использовать метод getContentViewId() интерфейса ru.tensor.sbis.base_components.fragment.selection.ViewDependencyProvider")
    @IdRes
    protected abstract fun getContentScrollViewId(): Int

    companion object {
        @JvmStatic
        val EXTRA_BACKGROUND_ALPHA = "SelectionWindowFragment.BACKGROUND_ALPHA"

        @JvmStatic
        val EXTRA_PANE_SHOWN = "SelectionWindowFragment.PANE_SHOWN"

        @JvmStatic
        val EXTRA_PANE_CLOSED = "SelectionWindowFragment.PANE_CLOSED"

        @JvmStatic
        val EXTRA_LOADING_BEGIN_TIMESTAMP = "SelectionWindowFragment.LOADING_BEGIN_TIMESTAMP"

        @Suppress("unused")
        @JvmStatic
        val EXTRA_CLOSE_BUTTON_HIDDEN = "SelectionWindowFragment.CLOSE_BUTTON_HIDDEN"
    }

    /**
     * Интерфейс слушателя [SelectionWindowFragment].
     */
    interface Listener {

        /**
         * Обработать событие закрытия окна выбора.
         */
        fun onSelectionWindowClosed(fragment: SelectionWindowFragment<*, *>)
    }

}