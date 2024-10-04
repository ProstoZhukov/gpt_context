package ru.tensor.sbis.appdesign.listheader

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.listheader.ui.news.NewsFragment
import ru.tensor.sbis.design.list_header.format.ListDateFormatter

/**
 * @author ra.petrov
 */
class DateHeaderDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_header)
        val fragment: NewsFragment = supportFragmentManager.findFragmentById(R.id.news_fragment) as NewsFragment

        findViewById<Button>(R.id.date_header_dateTime).setOnClickListener {
            fragment.setUp(formatter = ListDateFormatter.DateTime(this), layoutManager = LinearLayoutManager(this))
        }

        findViewById<Button>(R.id.date_header_dateTimeWithToday).setOnClickListener {
            fragment.setUp(
                formatter = ListDateFormatter.DateTimeWithToday(this),
                layoutManager = LinearLayoutManager(this)
            )
        }

        findViewById<Button>(R.id.date_header_dateWithMonth).setOnClickListener {
            fragment.setUp(
                formatter = ListDateFormatter.DateWithMonth(),
                layoutManager = LinearLayoutManager(this)
            )
        }

        findViewById<Button>(R.id.date_header_dateTimeReversed).setOnClickListener {
            fragment.setUp(
                formatter = ListDateFormatter.DateTime(this),
                layoutManager = LinearLayoutManager(this).apply {
                    reverseLayout = true
                }
            )
        }
    }
}