package ru.tensor.sbis.hallscheme.v2

import ru.tensor.sbis.hallscheme.v2.business.model.HallSchemeModel
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi

/**
 * Presenter схемы зала.
 * @author aa.gulevskiy
 */
internal class HallSchemeV2Presenter(private val view: HallSchemeV2Contract.View) :
    HallSchemeV2Contract.Presenter {

    private lateinit var schemeModel: HallSchemeModel

    private var scale: Float = 0.0f
    private var imageWidth = 0
    private var imageHeight = 0
    private var itemsInitiallyDrawn = false
    private var imageLoaded = false

    override fun setHallSchemeModel(hallSchemeModel: HallSchemeModel) {
        view.initHallSchemeView()

        itemsInitiallyDrawn = false
        imageLoaded = false
        imageWidth = 0
        imageHeight = 0

        this.schemeModel = hallSchemeModel

        redrawScheme()
    }

    override fun relayout() {
        if (!this::schemeModel.isInitialized) return

        view.clearScheme()
        view.initHallSchemeView()

        itemsInitiallyDrawn = false
        imageLoaded = false
        imageWidth = 0
        imageHeight = 0

        redrawScheme()
    }

    override fun imageLoadingSuccess(width: Int?, height: Int?) {
        imageLoaded = true
        imageWidth = width ?: 0
        imageHeight = height ?: 0
        setBackgroundSize()
    }

    override fun imageLoadingFailure() {
        imageLoaded = true
        setBackgroundSize()
    }

    private fun redrawScheme() {
        view.clearScheme()
        view.initHallSchemeView()
        drawItemsAndResetScale()
    }

    override fun setScale(scale: Float) {
        this.scale = scale
    }

    override fun drawItemsAndResetScale() {
        if (schemeModel.items.isEmpty()) return

        setViewsSizes()

        view.drawItems(schemeModel.items)
        prepareBackground()

        itemsInitiallyDrawn = true
    }

    private fun setViewsSizes() {
        view.setItemsLayoutSize(schemeModel.top, schemeModel.left, schemeModel.bottom, schemeModel.right)
    }

    private fun prepareBackground() {
        if (schemeModel.background.url.isNullOrBlank()) {
            view.showEmptyBackground()
            return
        }

        var url = schemeModel.background.url!!
        val isRemoteUrl = isRemoteUrl(url)
        if (!isRemoteUrl) url = setStaticResourceName(url)

        initializeBackgroundPosition(isRemoteUrl, url)
    }

    private fun isRemoteUrl(url: String): Boolean = url.startsWith("http", true)

    private fun setStaticResourceName(url: String): String = "hall_scheme_$url"

    private fun initializeBackgroundPosition(isRemoteUrl: Boolean, url: String) {
        val zoomBackgroundByZoom = schemeModel.zoomBackground.toFloat() / schemeModel.zoom.toFloat()
        val backgroundScale = schemeModel.zoomBackground.toFloat() / 100
        val backgroundZoom = if (schemeModel.pinTables) zoomBackgroundByZoom else backgroundScale
        val translate = if (schemeModel.pinTables) 1 else 0

        val background = schemeModel.background
        when {
            schemeModel.pinTables               -> view.showBackgroundIfTablesPinned(
                isRemoteUrl,
                url,
                schemeModel.left,
                schemeModel.top,
                translate,
                backgroundZoom
            )
            background.repeat == "1"            -> view.showRepeatedBackground(isRemoteUrl, url)
            background.size == "100%"           -> view.showStretchedBackground(isRemoteUrl, url)
            background.position == "center top" -> view.showCenterTopBackground(isRemoteUrl, url)
            background.position == "left top"   -> view.showLeftTopBackground(isRemoteUrl, url)
            background.position == "right top"  -> view.showRightTopBackground(isRemoteUrl, url)
            else                                -> view.showLeftTopBackground(isRemoteUrl, url)
        }
    }

    private fun setBackgroundSize() {
        when {
            schemeModel.background.repeat == "1"  -> view.setBackgroundViewSizeMatchParent()
            schemeModel.background.size == "100%" -> view.setBackgroundViewSizeMatchParent()
            else                                  -> view.setBackgroundViewSize(imageWidth, imageHeight)
        }
    }

    override fun redrawItems(changedItems: List<HallSchemeItemUi>) {
        if (itemsInitiallyDrawn) {
            val changedIds = changedItems.map { it.schemeItem.id }
            schemeModel.items.asSequence()
                .filter { it.schemeItem.id in changedIds }
                .forEach { it.removeView() }

            schemeModel.items.removeAll(changedItems)
            schemeModel.items.addAll(changedItems)

            view.drawItems(changedItems)
        }
    }
}