package ru.tensor.sbis.appdesign.context_menu.cloud_view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.cloudview.data.DemoCloudViewData
import ru.tensor.sbis.appdesign.cloudview.data.DemoCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.DemoIncomeCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.generateDemoData
import ru.tensor.sbis.appdesign.cloudview.list.DemoCloudViewAdapter
import ru.tensor.sbis.appdesign.cloudview.resources.DemoMessageResourcesHolder
import ru.tensor.sbis.appdesign.context_menu.ContextMenuPagerFragment
import ru.tensor.sbis.appdesign.databinding.FragmentContextMenuCloudViewBinding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithScreenAlignment
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Fragment для демонстрации работы контекстного меню в диалоге c облаками
 * @author ma.kolpakov
 */
class CloudViewFragment : ContextMenuPagerFragment(R.layout.fragment_context_menu_cloud_view, "Облачка") {
    private val disposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentContextMenuCloudViewBinding.bind(view)

        val ctx = requireContext()
        val viewPool = MessagesViewPool(ctx, DemoMessageResourcesHolder(ctx))
        val demoCloudViewAdapter = DemoCloudViewAdapter(viewPool)

        viewBinding.list.adapter = demoCloudViewAdapter
        val demoData = generateDemoData(ctx) as MutableList<DemoCloudViewUserData>
        demoCloudViewAdapter.submitList(demoData)
        demoCloudViewAdapter.itemLongClickListener = { clickedItem ->
            val income = clickedItem.findViewById<View>(R.id.cloud_view_background_income)
            val outcome = clickedItem.findViewById<View>(R.id.cloud_view_background_outcome)
            val sbisMenu = SbisMenu(
                title = "Операции",
                children = listOf(
                    MenuItem(
                        "Удалить",
                        discoverabilityTitle = "Это значит стереть из памяти",
                        destructive = true,
                        image = SbisMobileIcon.Icon.smi_delete
                    ) {
                        Toast.makeText(context, "Удалить строку ", Toast.LENGTH_SHORT).show()
                    },
                    MenuItem(
                        "Переслать",
                        image = SbisMobileIcon.Icon.smi_send
                    ) {
                        Toast.makeText(context, "Переслать строку", Toast.LENGTH_SHORT).show()
                    },
                )
            )

            if (income != null) {
                sbisMenu.showMenuWithScreenAlignment(
                    childFragmentManager,
                    income,
                    HorizontalAlignment.LEFT
                )
            } else {
                sbisMenu.showMenuWithScreenAlignment(
                    childFragmentManager,
                    outcome,
                    HorizontalAlignment.RIGHT
                )
            }
        }
        //Имитация изменившегося сообщения
        disposable.add(Flowable.interval(1, TimeUnit.SECONDS)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe {
                               updateData(demoData, demoCloudViewAdapter)
                           })
    }

    //region service methods

    //Обновления данных сообщения
    private fun updateData(
        demoData: MutableList<DemoCloudViewUserData>,
        adapter: DemoCloudViewAdapter
    ) {
        val newData = mutableListOf<DemoCloudViewUserData>()
        newData.addAll(demoData)
        newData[0] = DemoIncomeCloudViewUserData(
            demoData[0].id,
            demoData[0].date,
            demoData[0].time,
            demoData[0].author,
            demoData[0].receiverInfo,
            DemoCloudViewData(
                if (Random.nextBoolean()) {
                    "Коротко"
                } else {
                    "Очень длинное сообщение, очень очень длинное. Автор старался передать всю гамму чувств"
                }
            ),
            demoData[0].edited,
            (demoData[0] as DemoIncomeCloudViewUserData).isPersonal,
        )
        adapter.submitList(newData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }
    //endregion
}