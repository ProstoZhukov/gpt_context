package ru.tensor.sbis.appdesign.listheader.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.list_header.DateViewMode
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.ListDateFormatter

/**
 * @author ra.petrov
 */
class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private var headerViewMode: DateViewMode = DateViewMode.DATE_TIME

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var header: HeaderDateView

    fun setUp(formatter: ListDateFormatter, layoutManager: RecyclerView.LayoutManager) {
        header.dateViewMode = headerViewMode
        val listDateViewUpdater = ListDateViewUpdater(formatter)

        recyclerView.apply {
            this.layoutManager = layoutManager
            clearOnScrollListeners()
            val list = newsViewModel.news.value?.sortedByDescending { newsModel -> newsModel.date }!!

            adapter = if (formatter == ListDateFormatter.DateTime(requireContext())) {
                NewsAdapterWithSeparatedDateTime(list, listDateViewUpdater)

            } else NewsAdapter(list, listDateViewUpdater)

            adapter = NewsAdapter(list, listDateViewUpdater)

            listDateViewUpdater.bind(this, header)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        newsViewModel =
            ViewModelProvider(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.date_header_fragment_recycler_view, container, false)
        header = root.findViewById(R.id.header)

        recyclerView = root.findViewById(R.id.recycler_view)

        newsViewModel.news.observe(viewLifecycleOwner){
            setUp(formatter = ListDateFormatter.DateTime(requireContext()), layoutManager = LinearLayoutManager(activity))
        }

        return root
    }
}