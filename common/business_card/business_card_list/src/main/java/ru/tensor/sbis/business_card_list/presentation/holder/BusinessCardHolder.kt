package ru.tensor.sbis.business_card_list.presentation.holder

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.business_card_list.databinding.BusinessCardListItemBinding
import ru.tensor.sbis.business_card_list.presentation.view.ClicksWrapper
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.toDrawable
import ru.tensor.sbis.design.view_ext.MaskShape
import ru.tensor.sbis.design.view_ext.Source
import ru.tensor.sbis.design.view_ext.setImage
import ru.tensor.sbis.fresco_view.ShapedDraweeView
import ru.tensor.sbis.person_decl.profile.model.VisibilityStatus
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.GridSpaceItemDecorationTwoRows

/** Холдер визитки из реестра визиток */
internal class BusinessCardHolder(
    binding: BusinessCardListItemBinding,
    private val clicksWrapper: ClicksWrapper,
    private val scope: LifecycleCoroutineScope
) : AbstractViewHolder<BusinessCard>(binding.root) {

    constructor(parent: ViewGroup, clicksWrapper: ClicksWrapper, scope: LifecycleCoroutineScope) : this(
        BusinessCardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        clicksWrapper,
        scope
    )

    private val subcategoryListAdapter: ContactsAdapter by lazy { ContactsAdapter() }

    private val headerTitle: SbisTextView = binding.businessCardItemTitle
    private val unpinButton: SbisTextView = binding.businessCardItemIconPinned
    private val pinButton: SbisTextView = binding.businessCardItemButtonPinned
    private val linkButton: SbisTextView = binding.businessCardItemButtonLink
    private val photoView: ShapedDraweeView = binding.businessCardItemPhoto
    private val nameView: SbisTextView = binding.businessCardItemName
    private val jobTitleView: SbisTextView = binding.businessCardItemJobTitle
    private val contactsList: RecyclerView = binding.businessCardItemContacts
    private val generalContainer: FrameLayout = binding.businessCardItemsGeneralContainer
    private val container: ConstraintLayout = binding.businessCardItemsContainer

    init {
        contactsList.apply {
            addItemDecoration(
                GridSpaceItemDecorationTwoRows(dp(12), dp(6))
            )
        }
    }

    override fun bind(dataModel: BusinessCard) {
        super.bind(dataModel)
        headerTitle.text = dataModel.title
        nameView.text = dataModel.personName
        jobTitleView.text = dataModel.personRole
        dataModel.style?.properties?.background.toDrawable()?.let { generalContainer.background = it }

        container.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0,
                        0,
                        container.measuredWidth,
                        container.measuredHeight,
                        CARD_RADIUS_DP.toFloat()
                    )
                }
            }
            elevation = dp(CARD_ELEVATION_DP).toFloat()
        }

        val contacts = dataModel.contacts.filter { it.visibility == VisibilityStatus.FOR_ALL }

        contactsList.apply {
            layoutManager = GridLayoutManager(itemView.context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == contacts.size - 1 && contacts.size % 2 != 0) {
                            SINGLE_ELEMENT
                        } else {
                            PAIR_ELEMENTS
                        }
                    }
                }
            }
            isGone = contacts.isEmpty()
            adapter = subcategoryListAdapter.also {
                it.setContent(contacts)
            }
        }

        setupImage(dataModel)
        setupButtons(dataModel)
        itemView.setOnClickListener { scope.launch { clicksWrapper.businessCardClicks.emit(dataModel) } }
    }

    private fun setupImage(dataModel: BusinessCard) {
        photoView.doOnLayout { _ ->
            val snapshotUrl = dataModel.personPhoto?.let {
                PreviewerUrlUtil.replacePreviewerUrlPartWithCheck(
                    it,
                    PHOTO_IMAGE_SIZE_DP,
                    PHOTO_IMAGE_SIZE_DP,
                    PreviewerUrlUtil.ScaleMode.RESIZE
                )
            } ?: ""
            photoView.setImage(Source.Image(snapshotUrl), shape = MaskShape.Circle)
        }
    }

    private fun setupButtons(dataModel: BusinessCard) {
        setupLinkButton(dataModel)
        setupPinButtons(dataModel)
    }

    private fun setupLinkButton(dataModel: BusinessCard) {
        linkButton.setOnClickListener {
            scope.launch { clicksWrapper.linkShareClicks.emit(dataModel) }
        }
    }

    private fun setupPinButtons(dataModel: BusinessCard) {
        pinButton.isInvisible = dataModel.pinned
        unpinButton.isVisible = dataModel.pinned

        pinButton.setOnClickListener {
            togglePinState(dataModel, true)
        }

        unpinButton.setOnClickListener {
            togglePinState(dataModel, false)
        }
    }

    private fun togglePinState(
        dataModel: BusinessCard,
        newPinnedState: Boolean
    ) {
        scope.launch { clicksWrapper.businessCardPinnedClicks.emit(dataModel.id to newPinnedState) }
        dataModel.style?.properties?.background.toDrawable()?.let { generalContainer.background = it }
    }

    companion object {
        private const val PHOTO_IMAGE_SIZE_DP = 72
        private const val CARD_RADIUS_DP = 12
        private const val CARD_ELEVATION_DP = 5
        private const val SINGLE_ELEMENT = 2
        private const val PAIR_ELEMENTS = 1
    }
}