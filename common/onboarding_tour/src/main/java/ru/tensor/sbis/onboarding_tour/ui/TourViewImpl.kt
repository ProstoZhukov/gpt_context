package ru.tensor.sbis.onboarding_tour.ui

import android.text.method.LinkMovementMethod
import android.view.View
import androidx.constraintlayout.helper.widget.Carousel
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.doOnAttach
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.databinding.OnboardingTourFragmentBinding
import ru.tensor.sbis.onboarding_tour.ui.TourView.Event
import ru.tensor.sbis.onboarding_tour.ui.TourView.Model
import ru.tensor.sbis.onboarding_tour.ui.views.CloudAnimationRunner
import ru.tensor.sbis.onboarding_tour.ui.views.TermsConverter
import ru.tensor.sbis.onboarding_tour.ui.views.TourCarouselAdapter
import ru.tensor.sbis.onboarding_tour.ui.views.createTourGradientDrawable
import ru.tensor.sbis.onboarding_tour.ui.views.removeOnSwipeLeftListener
import ru.tensor.sbis.onboarding_tour.ui.views.setBannerIconTypeface
import ru.tensor.sbis.onboarding_tour.ui.views.setClickableText
import ru.tensor.sbis.onboarding_tour.ui.views.setOnSwipeLeftListener
import ru.tensor.sbis.onboarding_tour.ui.views.setOptionalImage
import ru.tensor.sbis.onboarding_tour.ui.views.setOptionalText
import ru.tensor.sbis.onboarding_tour.ui.views.setTextAndLineCount
import ru.tensor.sbis.onboarding_tour.ui.views.setVisibility
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect.DYNAMIC
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect.GRADIENT
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect.NONE
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect.STATIC
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import kotlin.math.min

/**
 * Реализация вью [TourView].
 */
internal class TourViewImpl(root: View) :
    BaseMviView<Model, Event>(),
    TourView {

    private val binding = OnboardingTourFragmentBinding.bind(root)
    private val resource = root.resources

    /** Текущая позиция в карусели. */
    private val carouselPosition get() = binding.onboardingTourCarousel.currentIndex

    /** Предыдущая позиция в карусели. Требуется для определения направления последнего перехода. */
    private var lastCarouselPosition = carouselPosition

    /** Доступное кол-во строк на позиции к которой перемещаемся. */
    private var affordableLineCount = 0

    /** Кол-во элементов в карусели. */
    private val carouselCount get() = binding.onboardingTourCarousel.count

    /** Карусель пуста, т.е. никакая модель к ней еще не применялась. */
    private val isCarouselEmpty get() = carouselCount == 0

    /** Маркер-предохранитель уже запланированной транзакции на карусели. */
    private var isScheduledCarouselMovement = false
    private val isOnLastPage get() = carouselPosition == carouselCount - 1
    private val carouselAdapter: TourCarouselAdapter
    private val termsConverter = TermsConverter()
    private lateinit var animationRunner: CloudAnimationRunner

    private val previousPage get() = binding.onboardingTourContent0
    private val visiblePage get() = binding.onboardingTourContent1
    private val nextPage get() = binding.onboardingTourContent2
    private val transitionForward = R.id.onboarding_tour_forward
    private val transitionBackward = R.id.onboarding_tour_backward
    private val Model.isVisible get() = position == carouselPosition

    init {
        binding.onboardingTourContainer.doOnAttach { motionView ->
            motionView as MotionLayout
            motionView.loadLayoutDescription(R.xml.onboarding_tour_carousel_scene)
        }
        carouselAdapter = TourCarouselAdapter(
            onPopulateItem = { _, modelToPopulate ->
                if (modelToPopulate.isVisible) {
                    render(modelToPopulate)
                } else {
                    renderCarouselContent(modelToPopulate)
                }
            },
            onItemSelected = { position ->
                isScheduledCarouselMovement = false
                binding.onboardingTourIndicators.currentItem = position
                dispatch(Event.OnPageChanged(position))
            }
        )
        binding.onboardingTourCarousel.setAdapter(carouselAdapter)
    }

    override val renderer: ViewRenderer<Model> = diff {
        diff(get = Model::bannerButton) {
            with(binding.onboardingTourClose) {
                setOptionalText(it.caption)
                setBannerIconTypeface(it)
            }
        }
        diff(get = Model::buttonTitle) {
            binding.onboardingTourButton.setOptionalText(it, true)
        }
        diff(get = Model::buttonIcon) {
            binding.onboardingTourButton.setIcon(it)
        }
        diff(get = Model::buttonStyle) { style ->
            style?.let { binding.onboardingTourButton.style = it }
        }
        diff(get = Model::buttonTitlePosition) {
            binding.onboardingTourButton.model.let { model ->
                if (model.title?.position == it) return@let
                val title = model.title?.copy(position = it)
                binding.onboardingTourButton.model = model.copy(title = title)
            }
        }
    }

    override fun render(model: Model) {
        setBackgroundEffect(model)
        setIndicators(model)
        setForwardClick(model)
        setupMessageWidth()
        setupCarousel(model)
        renderCarousel(model)
        if (model.isVisible) {
            super.render(model)
            binding.onboardingTourLogo.apply {
                visibility = if (model.bannerLogo is SbisLogoType.Empty) View.INVISIBLE else View.VISIBLE
                type = model.bannerLogo
            }
            setTerms(model)
        }
    }

    private fun setBackgroundEffect(model: Model) {
        binding.apply {
            when (model.backgroundEffect) {
                DYNAMIC -> if (!::animationRunner.isInitialized) {
                    onboardingTourGradient.visibility = View.GONE
                    onboardingTourBlurShapes.visibility = View.VISIBLE
                    animationRunner = CloudAnimationRunner(binding) { onboardingTourBlurShapes }
                }

                GRADIENT -> {
                    onboardingTourBlurShapes.visibility = View.GONE
                    onboardingTourGradient.visibility = View.VISIBLE
                    onboardingTourGradient.background = createTourGradientDrawable(root.context)
                }

                STATIC -> {
                    onboardingTourBlurShapes.visibility = View.VISIBLE
                    onboardingTourGradient.visibility = View.GONE
                }

                NONE -> setVisibility(View.GONE, onboardingTourBlurShapes, onboardingTourGradient)
            }
        }
    }

    private fun setTerms(model: Model) = binding.onboardingTourTerms.apply {
        if (model.terms == ID_NULL || model.termsLinks.isEmpty()) {
            visibility = View.INVISIBLE
            return@apply
        }
        movementMethod = LinkMovementMethod.getInstance()
        val (caption, textAndLinks) = termsConverter.calculateCaptionAndLinkedText(
            context = context,
            captionResId = model.terms,
            links = model.termsLinks
        )
        setClickableText(caption, textAndLinks) { url ->
            dispatch(Event.OnLinkClick(url))
        }
        visibility = View.VISIBLE
    }

    /** Отображаем индикатор страниц тура. */
    private fun setIndicators(model: Model) = binding.onboardingTourIndicators.apply {
        if (model.count != itemCount) {
            itemCount = model.count
        }
        if (isCarouselEmpty && !model.isVisible) {
            currentItem = model.position
        }
        visibility = if (itemCount > 1) View.VISIBLE else View.INVISIBLE
    }

    /** Устанавливаем обработчик кнопки навигации вперед. */
    private fun setForwardClick(model: Model) {
        if (model.position != carouselPosition) return
        binding.onboardingTourButton.apply {
            setOnClickListener {
                tryMoveToNextPage(model)
            }
        }
    }

    /** Устанавливаем карусель. */
    private fun setupCarousel(model: Model) {
        if (model.isEmpty || carouselCount == model.count) {
            return
        }
        if (model.count != 1) {
            val prePopulatedItem = model.copy(position = carouselPosition.inc())
            renderCarouselContent(prePopulatedItem)
        }
        // карусель пуста, но позиция уже смещена от начальной - т.е. требуется восстановление состояния карусели (например после смены ориентации экрана)
        if (isCarouselEmpty && !model.isVisible) {
            carouselAdapter.setModel(model)
            moveCarouselTo(model.position, true)
        }
    }

    /** Настраиваем карусель для актуальной полученной вью-модели [model]. */
    private fun renderCarousel(model: Model) {
        if (model.isEmpty) return
        carouselAdapter.setModel(model)
        renderCarouselContent(model)
        if (model.isVisible) {
            if (!model.arePermissionsChecked && model.isTransitionBlocked) {
                dispatch(Event.CheckPermissions(carouselPosition, model.permissions))
            }
            binding.onboardingTourContainer.apply {
                if (model.count == 1 || !model.isSwipeSupported) {
                    doOnAttach {
                        disableSwipe()
                    }
                } else {
                    val isNotLastPosition = carouselPosition != carouselCount - 1
                    val isForwardSwipeable = !model.isTransitionBlocked && isNotLastPosition
                    if (isForwardSwipeable) {
                        enableTransition(transitionForward, true)
                        removeOnSwipeLeftListener()
                    } else {
                        enableTransition(transitionForward, false)
                        if (isNotLastPosition || model.isSwipeClosable || model.requirePermissions) {
                            setOnSwipeLeftListener(model.position == 0) {
                                tryMoveToNextPage(model)
                            }
                        } else {
                            removeOnSwipeLeftListener()
                        }
                    }
                    val isInFirstPosition = model.position == 0
                    if (model.isTransitionBlocked && !isInFirstPosition) {
                        // явно указываем переход назад если переход вперед был отключен,
                        // "особенность" работы MotionScene: при отключении первой транзакции в xml последующие также не будут выполнены.
                        setTransition(transitionBackward)
                    }
                    enableTransition(transitionBackward, !isInFirstPosition)
                }
            }
            setBannerButtonAction(model)
        } else {
            moveCarouselTo(model.position)
        }
    }

    /** Запрос на изменение страницы по клику на кнопку "Далее". */
    private fun tryMoveToNextPage(model: Model) =
        if (model.requirePermissions) {
            dispatch(
                Event.RequestPermissions(
                    position = model.position,
                    permissions = model.permissions,
                    rationaleCommand = model.rationaleCommand
                )
            )
        } else if (model.transitionCommand != null) {
            dispatch(
                Event.OnCommandClick(
                    position = model.position,
                    command = model.transitionCommand,
                    isLastPage = isOnLastPage
                )
            )
        } else if (isOnLastPage) {
            dispatch(Event.OnCloseClick)
        } else {
            moveCarouselTo(model.position.inc())
        }

    /**
     * Сдвинуть карусель к позиции [position].
     * @param immediately true если требуется немедленное смещение позиции без анимации
     */
    private fun moveCarouselTo(position: Int, immediately: Boolean = false) {
        if (!immediately) {
            /** пропускаем наслаивающиеся транзакции, иначе из-за особенностей реализации [Carousel] возможны неприятные сайд-эффекты */
            if (isScheduledCarouselMovement) return
            isScheduledCarouselMovement = true
        }
        binding.onboardingTourCarousel.doOnAttach { carousel ->
            carousel as Carousel
            if (carousel.currentIndex == position) return@doOnAttach
            if (immediately) {
                carousel.jumpToIndex(position)
            } else {
                binding.onboardingTourClose.setOnClickListener(null)
                carousel.transitionToIndex(position, resource.getInteger(R.integer.onboarding_tour_swipe_anim_duration))
            }
        }
    }

    /** Отображаем видимый или скрытый скролируемый контен на карусели */
    private fun renderCarouselContent(model: Model) = model.run {
        val lines = keepAffordableLineCountInSwipeDirection()
        // контент справа
        if (position > carouselPosition) {
            nextPage.onboardingTourMainImage.setOptionalImage(imageResId)
            nextPage.onboardingTourTitle.setOptionalText(titleResId)
            nextPage.onboardingTourMessage.setTextAndLineCount(messageResId)
        }
        // контент слева
        else if (position < carouselPosition) {
            previousPage.onboardingTourMainImage.setOptionalImage(imageResId)
            previousPage.onboardingTourTitle.setOptionalText(titleResId)
            previousPage.onboardingTourMessage.setTextAndLineCount(messageResId)
        }
        // текущий просматриваемый контент
        else {
            visiblePage.onboardingTourMainImage.setOptionalImage(imageResId)
            visiblePage.onboardingTourTitle.setOptionalText(titleResId)
            visiblePage.onboardingTourMessage.setTextAndLineCount(messageResId, lines)
        }
    }

    /**
     * Задаем ширину элемента с сообщением относительно ширины [MotionLayout] за вычетом [Offset.X3L].
     * Исправляет ошибку со смещением прижатого к правому краю текста сообщения при навигации скроллом,
     * когда [MotionLayout] меняет ширину управляемых контейнеров на 1-2px.
     */
    private fun setupMessageWidth() = binding.run {
        val applyWidth = {
            val amendedParentWidth = onboardingTourContainer.measuredWidth - Offset.X3L.getDimenPx(root.context)
            val maxWidth = resource.getDimensionPixelSize(R.dimen.onboarding_tour_max_text_width)
            val newWidth = min(amendedParentWidth, maxWidth)
            if (previousPage.onboardingTourMessage.width != newWidth) {
                previousPage.onboardingTourMessage.width = newWidth
            }
            if (visiblePage.onboardingTourMessage.width != newWidth) visiblePage.onboardingTourMessage.width = newWidth
            if (nextPage.onboardingTourMessage.width != newWidth) nextPage.onboardingTourMessage.width = newWidth
        }
        if (onboardingTourContainer.measuredWidth != 0) {
            applyWidth()
        } else {
            onboardingTourContainer.post(applyWidth)
        }
    }

    /** Сохраняем доступное кол-во строк перед перемещением (свайпом) на новую позицию. */
    private fun keepAffordableLineCountInSwipeDirection(): Int {
        if (carouselPosition != lastCarouselPosition) {
            affordableLineCount = if (lastCarouselPosition < carouselPosition) {
                nextPage.onboardingTourMessage.lineCount
            } else {
                previousPage.onboardingTourMessage.lineCount
            }
            lastCarouselPosition = carouselPosition
        }
        return affordableLineCount
    }

    private fun setBannerButtonAction(model: Model) =
        if (model.bannerButton == BannerButtonType.NONE) {
            binding.onboardingTourClose.setOnClickListener(null)
        } else {
            binding.onboardingTourClose.setOnClickListener {
                model.bannerCommand?.invoke()
                if (model.bannerButton == BannerButtonType.CLOSE || isOnLastPage) {
                    dispatch(Event.OnCloseClick)
                } else if (model.bannerButton == BannerButtonType.SKIP) {
                    moveCarouselTo(model.position.inc())
                }
            }
        }

    /** Отключение анимации. Из-за какого-то неявного поведения MotionLayout стабильно работает только после удаления свайпа. */
    private fun MotionLayout.disableSwipe() {
        getTransition(transitionBackward).apply {
            isEnabled = false
            setOnSwipe(null)
        }
        getTransition(transitionForward).apply {
            isEnabled = false
            setOnSwipe(null)
        }
    }
}