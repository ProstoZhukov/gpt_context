package ru.tensor.sbis.appdesign.listheader.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.joda.time.LocalDateTime
import java.util.*

/**
 * @author ra.petrov
 */
class NewsViewModel : ViewModel() {

    private val _news = MutableLiveData<List<NewsModel>>().apply {
        val list = mutableListOf<NewsModel>()
        for (i in 0..2) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now()
                )
            )
        }
        for (i in 0..5) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now().minusDays(3)
                )
            )
        }
        for (i in 0..5) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now().minusDays(5)
                )
            )
        }
        for (i in 0..5) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now().minusDays(90)
                )
            )
        }
        for (i in 0..5) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now().minusMonths(13)
                )
            )
        }
        for (i in 0..5) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    LocalDateTime.now().minusMonths(15)
                )
            )
        }
        for (i in 0..10) {
            list.add(
                NewsModel(
                    generateText(10),
                    generateText(50),
                    null
                )
            )
        }
        postValue(list)
    }

    val news: LiveData<List<NewsModel>> = _news

    private fun generateText(targetLength: Int): String {
        val builder = StringBuilder(targetLength)
        val leftLimit = 'а'
        val rightLimit = 'я'
        val averageWordLength = 7
        val minWordLength = 4
        val maxWordLength = 9
        val wordsCount = targetLength / averageWordLength

        val random = Random()
        for (wordIndex in 0..wordsCount) {
            val wordLength = minWordLength + random.nextInt(maxWordLength - minWordLength)
            for (i in 0..wordLength) {
                builder.appendCodePoint(random.nextInt(rightLimit - leftLimit - 1) + leftLimit.toInt())
            }
            builder.append(" ")
        }
        return builder.toString().trim()
    }
}