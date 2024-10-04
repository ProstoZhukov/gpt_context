package ru.tensor.sbis.design.navigation.view.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleableRes
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.navigation.NavigationPlugin
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuFooterItemBinding
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuLogoHeaderBinding
import ru.tensor.sbis.design.navigation.view.adapter.NavMenuItemViewHelper
import ru.tensor.sbis.design.navigation.view.bindViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationCounters
import ru.tensor.sbis.design.navigation.view.model.NavigationFooterViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderData
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderViewModelImpl
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import ru.tensor.sbis.design.navigation.view.view.util.NavigationFooterBindingDelegate
import ru.tensor.sbis.design.navigation.view.view.util.setTypefaceAndSize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.loadEnum
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Боковое меню (известно как Аккордеон) с поддержкой стилизации и возможностью устанавливать
 * собственную "шапку" и "подвал".
 *
 * Для стилизации нужно установить тему в атрибут [R.attr.navStyle].
 *
 * @see TabNavView
 *
 * @author ma.kolpakov
 */
class NavView private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val delegate: AbstractNavViewDelegate,
) : FrameLayout(ThemeContextBuilder(context, R.attr.navStyle, R.style.NavView).build(), attrs, R.attr.navStyle),
    NavigationView by delegate {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, AbstractNavViewDelegate())

    private val navMenuSharedResources = NavViewSharedStyle(context)

    private val headerBackground = navMenuSharedResources.createBackground()

    private val footerBackground = navMenuSharedResources.createBackground()

    private val showOnInitialization: Boolean
    private var headerVm: NavigationHeaderViewModel? = null
    private var footerVm: NavigationFooterViewModel? = null
    private val _footerSelectionLiveData = MutableLiveData<Boolean>()
    private val _footerSelectionFlow = MutableSharedFlow<Boolean>(replay = 1)
    private val _footerLabelLiveData = MutableLiveData<NavigationItemLabel>()
    private lateinit var _onboardingCloseEventDisposable: Disposable
    private var footerBindingDelegate: NavigationFooterBindingDelegate<*>? = null

    /**
     * "Подвал", который установлен через атрибут [ru.tensor.sbis.design.navigation.R.styleable.NavView_footer]
     */
    private val footer: View?

    private val itemListView: NavListView
    private val scrollView: ScrollView by lazy { findViewById(listViewId) }

    private val name: String

    // lazy initialisation to avoid dangerous state inside of init block
    private val mainView: View by lazy {
        LayoutInflater.from(getContext()).inflate(layoutId, this, true)
    }

    private val listViewId: Int = R.id.navigation_list

    @SuppressWarnings("unchecked")
    private val viewHelper = NavMenuItemViewHelper(
        this.context,
        R.attr.navItemTheme,
        R.style.NavItem,
        navMenuSharedResources
    )

    /** @SelfDocumented */
    val layoutId: Int = R.layout.navigation_menu

    /**
     * "Шапка", которая установлена через атрибут [ru.tensor.sbis.design.navigation.R.styleable.NavView_header]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var header: View?
        private set

    /**
     * Состояние выделения шапки аккордеона.
     * События, инициированные вызовом [setHeaderSelected], не меняющие состояния выделения, не публикуются
     */
    val headerSelectionLiveData: LiveData<Boolean>
        get() = checkNotNull(headerVm) {
            "Default header is not configured"
        }.isSelected

    @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
    val headerSelectionFlow: Flow<Boolean>
        get() = checkNotNull(headerVm) {
            "Default header is not configured"
        }.selectionFlow

    /**
     * Состояние выделения "подвала" аккордеона.
     * События, инициированные вызовом [setFooterSelected], не меняющие состояния выделения, не публикуются
     */
    val footerSelectionLiveData: LiveData<Boolean> = _footerSelectionLiveData

    @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
    val footerSelectionFlow: Flow<Boolean> = _footerSelectionFlow

    init {
        isClickable = true
        var configuration: NavViewConfiguration
        // using of wrapped context from parent
        with(getContext().theme.obtainStyledAttributes(attrs, R.styleable.NavView, R.attr.navStyle, R.style.NavView)) {

            header = replaceStubFromAttribute(R.styleable.NavView_header, R.id.navigation_header_stub, "header")
            footer = replaceStubFromAttribute(R.styleable.NavView_footer, R.id.navigation_footer_stub, "footer")
            showOnInitialization = getBoolean(R.styleable.NavView_showOnInitialization, true)
            configuration =
                loadEnum(R.styleable.NavView_configuration, NavViewConfiguration.SCROLL, *NavViewConfiguration.values())
            recycle()
        }

        with(getContext().theme.obtainStyledAttributes(attrs, R.styleable.AbstractNavView, 0, 0)) {
            name = getString(R.styleable.AbstractNavView_name) ?: "${this@NavView.javaClass}_$id"
            setIsUsedNavigationIcons(getBoolean(R.styleable.AbstractNavView_isUsedNavigationIcons, false))
            recycle()
        }
        itemListView = NavListView(context, viewHelper, navMenuSharedResources).apply {
            this.configuration = configuration
        }
        itemListView.orientation = NavListView.Orientation.VERTICAL
        scrollView.addView(itemListView)
        viewHelper.sourceName = name
        delegate.init(viewHelper, itemListView, scrollView)
    }

    override fun setConfiguration(configuration: NavViewConfiguration) {
        itemListView.configuration = configuration
    }

    override fun setIsUsedNavigationIcons(isUsed: Boolean) {
        navMenuSharedResources.isUsedNavigationIcons = isUsed
        footer?.findViewById<SbisTextView>(R.id.icon)?.let {
            it.setTypefaceAndSize(isUsed)
            // TODO костыль для иконки в подвале аккордеона https://dev.sbis.ru/opendoc.html?guid=c9a72eb0-17de-47d1-a3a7-8e87248f0ac2&client=3
            it.text = resources.getString(
                if (isUsed) RDesign.string.design_nav_icon_setting
                else RDesign.string.design_mobile_icon_menu_settings_skinny
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        when (itemListView.configuration) {
            NavViewConfiguration.SCROLL -> super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            NavViewConfiguration.SECTION -> {
                itemListView.forceLayout()
                itemListView.measure(widthMeasureSpec, scrollView.measuredHeight)

                super.onMeasure(MeasureSpecUtils.makeExactlySpec(itemListView.measuredWidth), heightMeasureSpec)
            }
        }
    }

    override fun isUsedNavigationIcons() = navMenuSharedResources.isUsedNavigationIcons

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setNavViewDrawerScrimColor()
        _onboardingCloseEventDisposable = if (showOnInitialization)
            NavigationPlugin.onboardingCloseEventObservable.subscribe {
                showOnInitialization()
            }
        else Disposables.empty()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        _onboardingCloseEventDisposable.dispose()
    }

    /** @SelfDocumented */
    fun updateCounters(counters: Map<String, NavigationCounters>) {
        counters["notices"]?.let { counter ->
            headerVm?.updateCounters(counter)
        }
        counters["settings"]?.let { counter ->
            footerVm?.updateCounters(counter)
        }
    }

    /**
     * Настраивает стандартную шапку аккордеона.
     * Стандартная шапка может быть использована только если не задан атрибут [R.styleable.NavView_header].
     */
    fun configureDefaultHeader(
        data: NavigationHeaderData.LogoData,
        lifecycleOwner: LifecycleOwner
    ) {
        check(header == null) {
            "Cannot configure default header when custom layout is specified in header attribute"
        }
        header = replaceStub(R.id.navigation_header_stub, R.layout.navigation_menu_logo_header, "header")
        headerVm = NavigationHeaderViewModelImpl(data)
        NavigationMenuLogoHeaderBinding.bind(header!!).bindViewModel(headerVm!!, lifecycleOwner)
        header?.background = headerBackground
        // При установке InsetDrawable в ConstraintLayout он устанавливает паддинги, убираем их.
        header?.setLeftPadding(0)
    }

    /**
     * Устанавливает состояние выделения шапки.
     */
    fun setHeaderSelected(selected: Boolean) {
        checkNotNull(headerVm) {
            "Default header is not configured"
        }.setSelected(selected)
    }

    /**
     * Переключить доступность шапки для выбора.
     */
    @Suppress("unused")
    fun setHeaderEnabled(enabled: Boolean) {
        header?.isEnabled = enabled
    }

    /**
     * Переключить видимость счётчика в шапке.
     */
    @Suppress("unused")
    fun setHeaderCounterVisibility(enabled: Boolean) {
        checkNotNull(headerVm) {
            "Default header is not configured"
        }.updateCountersVisibility(enabled)
    }

    /**
     * Переключить видимость "подвала".
     */
    @Suppress("unused")
    fun setFooterVisible(visible: Boolean) {
        footer?.isVisible = visible
    }

    /**
     * Настраивает "подвал".
     * Для использования необходимо, чтобы был задан атрибут [R.styleable.NavView_footer], а параметр
     * [NavigationFooterBindingDelegate] соответствовал [ViewBinding] указанного макета.
     */
    fun configureFooter(
        bindingDelegate: NavigationFooterBindingDelegate<*>,
        lifecycleOwner: LifecycleOwner
    ) {
        val footer = checkNotNull(footer) {
            "NavView_footer attribute is not set"
        }
        footer.background = footerBackground
        // При установке InsetDrawable в ConstraintLayout он устанавливает паддинги, убираем их.
        footer.setLeftPadding(0)
        footerBindingDelegate = bindFooter(footer, bindingDelegate, lifecycleOwner)
    }

    override fun changeItemLabel(item: NavigationItem, label: NavigationItemLabel) {
        delegate.changeItemLabel(item, label)
        if (footerBindingDelegate?.getNavigationItem() == item) _footerLabelLiveData.value = label
    }

    /**
     * Устанавливает состояние выделения "Подвала".
     */
    fun setFooterSelected(selected: Boolean) {
        if (_footerSelectionLiveData.value != selected) {
            _footerSelectionLiveData.value = selected
            _footerSelectionFlow.tryEmit(selected)
        }
    }

    /** Открыть при первом входе в приложение. */
    fun showOnInitialization() {
        if (isAutotestsLaunch() && !shouldShowAccordionOnAutotestsLaunch()) return
        PreferenceManager.getDefaultSharedPreferences(context).let { prefs ->
            if (prefs.getBoolean(PREF_SHOW_DRAWER_ON_INITIALIZATION, true) && showOnInitialization) {
                prefs.edit().putBoolean(PREF_SHOW_DRAWER_ON_INITIALIZATION, false).apply()
                findViewParent<DrawerLayout>(this)
                    ?.apply {
                        // покажем аккордеон с анимацией после закрытия онбординга
                        val onboardingAnimationDuration = resources
                            .getInteger(RDesign.integer.animation_activity_translate_duration).toLong()
                        postDelayed({ openDrawer(GravityCompat.START) }, onboardingAnimationDuration)
                    }
                    ?: Timber.w("Unable to find DrawerLayout to open NavView")
            }
        }
    }

    private fun isAutotestsLaunch() = NavigationPlugin.autotestsLaunchStatusProvider?.get()?.isAutotestsLaunch == true

    private fun shouldShowAccordionOnAutotestsLaunch() =
        NavigationPlugin.autotestsParametersProvider?.get()?.showAccordionOnAutotestsLaunch ?: false

    private fun <VB : ViewBinding> bindFooter(
        footer: View,
        bindingDelegate: NavigationFooterBindingDelegate<VB>,
        lifecycleOwner: LifecycleOwner
    ) = bindingDelegate.apply {
        getViewBinding(footer).apply {
            setClickSelectionListener(this) {
                _footerSelectionLiveData.value = true
                _footerSelectionFlow.tryEmit(true)
            }
            setItemLabelSubscription(this, lifecycleOwner, _footerLabelLiveData)
            setSelectionLiveData(this, lifecycleOwner, _footerSelectionLiveData)
            bind(this)
            if (this is NavigationMenuFooterItemBinding) {
                footerVm = NavigationFooterViewModel()
                bindViewModel(footerVm!!, lifecycleOwner)
            }
        }
    }

    /**
     * Подвал и шапка размещаются в специальные контейнеры в разметке. Предварительно проверяется
     * наличие контейнеров.
     *
     * @throws AssertionError если в разметке нет нужного контейнера [ViewStub]
     */
    private fun TypedArray.replaceStubFromAttribute(
        @StyleableRes attrId: Int,
        @IdRes stubId: Int,
        label: String
    ): View? {
        return getResourceId(attrId, 0).takeIf { it != 0 }
            ?.let { replaceStub(stubId, it, label) }
    }

    private fun replaceStub(@IdRes stubId: Int, @LayoutRes layoutId: Int, label: String): View? {
        val stub = checkNotNull(mainView.findViewById<ViewStub>(stubId)) {
            "Navigation $label stub is absent in NavView layout"
        }
        return stub.apply {
            layoutResource = layoutId
        }.inflate()
    }

    private fun setNavViewDrawerScrimColor() {
        findViewParent<DrawerLayout>(this)?.setScrimColor(navMenuSharedResources.scrimColor)
            ?: Timber.w("Unable to find parent DrawerLayout of NavView")
    }

    companion object {
        private const val PREF_SHOW_DRAWER_ON_INITIALIZATION = "PREF_SHOW_DRAWER_ON_INITIALIZATION"
    }
}