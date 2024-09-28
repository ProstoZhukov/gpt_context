package ru.tensor.sbis.appdesign.title_view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.profile.person.PersonActivityStatus
import ru.tensor.sbis.design.profile.person.data.PersonData
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile.titleview.model.*

/**
 * Демо экран компонента шапка.
 *
 * @author ma.kolpakov
 */
internal class TitleViewActivity : AppCompatActivity() {

    private companion object {
        const val DEFAULT_TITLE = "Тут находится Очень важная информация, которая может занимать несколько строк"
        const val DEFAULT_SUBTITLE =
            "А вот тут уже находится менее важная информация, которая тоже может занимать несколько строк"

        val IMAGE_1 = PersonData(photoUrl = "https://pbs.twimg.com/media/DiUc_hyX0AEdY8x.jpg:large")
        val IMAGE_2 = PersonData(photoUrl = "https://klike.net/uploads/posts/2019-07/1564314090_3.jpg")
        val IMAGE_3 = PersonData(photoUrl = "https://steemitimages.com/DQmQZsTMBkNWRdh61k6AjdtBvNKM6KzTu6dkBM5x8rADTnL/12.jpg")
        val IMAGE_4 = PersonData(photoUrl = "https://sun9-25.userapi.com/c857624/v857624488/970ab/wfJ1pk_s3jw.jpg")

        val defaultContent = Default(DEFAULT_TITLE, DEFAULT_SUBTITLE, IMAGE_1.photoUrl!!)

        val personsContent = ListContent(
            listOf(
                TitleViewItem(IMAGE_1, "ФИО 1", "Доп информация к 1"),
                TitleViewItem(IMAGE_2, "ФИО 2"),
                TitleViewItem(IMAGE_3, "ФИО 3", "Доп информация к 3")
            )
        )

        val collageContent = ListContent(
            listOf(
                TitleViewItem(IMAGE_1, "Что-то 1"),
                TitleViewItem(IMAGE_2, "Что-то 2"),
                TitleViewItem(IMAGE_3, "Что-то 3")
            ),
            DEFAULT_TITLE,
            DEFAULT_SUBTITLE
        )

        val defaultContentWithoutSubtitle = Default(DEFAULT_TITLE, imageUrl = IMAGE_1.photoUrl!!)

        val onePerson = ListContent(listOf(TitleViewItem(IMAGE_1, "ФИО", "Доп информация")))
    }

    private lateinit var titleView: SbisTitleView
    private lateinit var contentType: TextView
    private lateinit var activityStatus: TextView
    private lateinit var singleLine: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_view)

        findViewById<Toolbar>(R.id.activity_title_view_toolbar).apply {
            leftIcon.setOnClickListener { finish() }
        }


        titleView = findViewById<SbisTitleView>(R.id.activity_title_view_toolbar_title).apply {
            setOnClickListener {
                Toast.makeText(this@TitleViewActivity, "OnTitleViewClick", Toast.LENGTH_SHORT).show()
            }
        }

        titleView.content = defaultContent

        contentType = findViewById(R.id.activity_title_view_content_type)
        activityStatus = findViewById(R.id.activity_title_view_activity_status)
        singleLine = findViewById(R.id.activity_title_view_single_line)

        updateInfo()
    }

    fun onSetDefaultContentClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.content = defaultContent
        updateInfo()
    }

    fun onSetPersonsContentClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.content = personsContent
        updateInfo()
    }

    fun onSetCollageContentClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.content = collageContent
        updateInfo()
    }

    fun onSetDefaultContentWithoutSubtitleClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.content = defaultContentWithoutSubtitle
        updateInfo()
    }

    fun onSetPersonWithStatusClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.content = onePerson
        titleView.activityStatus = PersonActivityStatus.ONLINE_WORK
        updateInfo()
    }

    fun onChangeSingleLineClick(@Suppress("UNUSED_PARAMETER") view: View) {
        titleView.singleLineTitle = titleView.singleLineTitle.not()
        updateInfo()
    }

    private fun updateInfo() {
        contentType.text = titleView.content::class.simpleName
        activityStatus.text = titleView.activityStatus.name
        singleLine.text = titleView.singleLineTitle.toString()
    }
}
