package ru.tensor.sbis.order_message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.data.mapper.base.toBaseItem
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.CompanyNomenclatures
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.DifferentNomTaxes
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.NomenclaturesChangedPrice
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.NomenclaturesNotPublished
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.QueueNomenclatures
import ru.tensor.sbis.order_message.OrderMessageNomenclatures.StopListNomenclature
import ru.tensor.sbis.order_message.adapter.OrderMessageAdapter
import ru.tensor.sbis.order_message.databinding.OrderMessageFragmentBinding

/**
 * Экран для вывода сообщения со списком номенклатур
 */
class OrderMessageFragment : BaseFragment(), DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    companion object {

        /**@SelfDocumented */
        fun newInstance(params: OrderMessageParams): OrderMessageFragment =
            OrderMessageFragment().withArgs { putParcelable(ORDER_MESSAGE_PARAMS_KEY, params) }

        private const val ORDER_MESSAGE_PARAMS_KEY = "ORDER_MESSAGE_PARAMS_KEY"
    }

    private var _binding: OrderMessageFragmentBinding? = null
    private val binding: OrderMessageFragmentBinding
        get() = _binding!!

    private val hasNotPublishedAndChangedPriceMessage: Boolean by lazy {
        orderMessageParams.run {
            nomenclatures.size > 1 && nomenclatures.any { it is NomenclaturesNotPublished } &&
                nomenclatures.any { it is NomenclaturesChangedPrice }
        }
    }

    private val orderMessageParams: OrderMessageParams by lazy {
        requireArguments().getParcelableUniversally(ORDER_MESSAGE_PARAMS_KEY)!!
    }

    private val orderMessageAdapter: OrderMessageAdapter = OrderMessageAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = OrderMessageFragmentBinding.inflate(inflater, container, false)
        initView()
        initInsetListener(
            DefaultViewInsetDelegateParams(
                listOf(ViewToAddInset(binding.root, listOf(IndentType.PADDING to Position.TOP)))
            )
        )
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        parentFragmentManager.popBackStack()
        return true
    }

    private fun initView() {
        initList()
        binding.orderMessageButton.apply { model = model.copy(clickListener = { onButtonClick() }) }
        binding.orderMessageCross.setOnClickListener { onBackPressed() }
    }

    private fun initList() {
        binding.orderMessageList.adapter = orderMessageAdapter
        orderMessageAdapter.setContent(orderMessageParams.nomenclatures.toBaseItem())
        orderMessageAdapter.withImage = isWithImage()
    }

    private fun onButtonClick() {
        onBackPressed()
        val bundle = Bundle().apply {
            putParcelable(
                orderMessageParams.resultKey,
                OrderMessageResult(orderMessageParams.nomenclatures)
            )
        }
        parentFragmentManager.setFragmentResult(orderMessageParams.resultKey, bundle)
    }

    private fun List<OrderMessageNomenclatures>.toBaseItem(): ArrayList<BaseItem<Any>> {
        val contentList = arrayListOf<BaseItem<Any>>()

        contentList.add(toBaseItem(R.id.order_message_title, run { getTitle() to getMessage() }))
        forEachIndexed { index, item ->
            item.getHeaderItem(index)?.let { contentList.add(it) }
            contentList.addNomenclatures(item)
        }
        return contentList
    }

    private fun ArrayList<BaseItem<Any>>.addNomenclatures(nomenclatures: OrderMessageNomenclatures) {
        val id =
            if (nomenclatures is StopListNomenclature) R.id.order_message_stop_list_nomenclature
            else R.id.order_message_nomenclature
        nomenclatures.nomenclatures.forEach { add(toBaseItem(id, it)) }
    }

    private fun OrderMessageNomenclatures.getHeaderItem(index: Int): BaseItem<Any>? =
        when {
            this is CompanyNomenclatures -> toBaseItem(R.id.order_message_text, companyName)
            this is QueueNomenclatures -> toBaseItem(R.id.order_message_queues, queue)
            this is DifferentNomTaxes -> toBaseItem(
                R.id.order_message_text, getString(R.string.order_message_different_nom_taxes_order_number, (index + 1))
            )

            this is StopListNomenclature -> toBaseItem(R.id.order_message_stop_list_available, Unit)
            hasNotPublishedAndChangedPriceMessage && this is NomenclaturesChangedPrice -> {
                toBaseItem(
                    R.id.order_message_text,
                    getString(R.string.order_message_nomenclature_price_has_changed_title)
                )
            }

            hasNotPublishedAndChangedPriceMessage && this is NomenclaturesNotPublished -> {
                toBaseItem(
                    R.id.order_message_text,
                    getString(R.string.order_message_nomenclature_not_available_for_order_title)
                )
            }

            else -> null
        }

    private fun getTitle(): String =
        with(orderMessageParams.nomenclatures.first()) {
            getString(
                when {
                    this is CompanyNomenclatures -> R.string.order_message_company_title
                    this is QueueNomenclatures -> R.string.order_message_queue_title
                    this is StopListNomenclature -> R.string.order_message_stop_list_title
                    this is DifferentNomTaxes -> R.string.order_message_different_nom_taxes_title
                    hasNotPublishedAndChangedPriceMessage -> R.string.order_message_nomenclature_has_changes_title
                    this is NomenclaturesChangedPrice -> R.string.order_message_nomenclature_changed_price_title
                    this is NomenclaturesNotPublished ->
                        if (isSalon) R.string.order_message_salon_nomenclature_not_published_title
                        else R.string.order_message_nomenclature_not_published_title

                    else -> 0
                }
            )
        }

    private fun getMessage(): String =
        with(orderMessageParams.nomenclatures.first()) {
            getString(
                when {
                    this is CompanyNomenclatures -> R.string.order_message_company_description
                    this is QueueNomenclatures -> R.string.order_message_queue_description
                    this is StopListNomenclature -> R.string.order_message_stop_list_description
                    this is DifferentNomTaxes -> R.string.order_message_different_nom_taxes_description
                    hasNotPublishedAndChangedPriceMessage -> R.string.order_message_nomenclature_has_changes_description
                    this is NomenclaturesChangedPrice ->
                        if (isSalon) R.string.order_message_salon_nomenclature_changed_price_description
                        else R.string.order_message_nomenclature_changed_price_description

                    this is NomenclaturesNotPublished -> R.string.order_message_nomenclature_not_published_description
                    else -> 0
                }
            )
        }

    private fun isWithImage(): Boolean =
        orderMessageParams.nomenclatures.any { item -> item.nomenclatures.any { it.imageUrl.isNotBlank() } }
}