package ru.tensor.sbis.info_decl.knowledge_ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Поставщик фрагмента базы знаний
 *
 * @author sr.golovkin on 30.08.2020
 */
interface ArticleListActivityProvider : Feature {

    /**
     * Предоставить интент для открытия конкретной базы знаний
     */
    fun getArticleListIntent(context: Context, knowledgeBaseUuid: UUID): Intent

    /**
     * Предоставить фрагмент конкретной базы знаний
     */
    fun getArticleListFragment(knowledgeBaseUuid: UUID): Fragment

    /**
     * Предоставить интент для открытия статьи
     */
    fun getSingleArticleIntent(context: Context, articleUuid: UUID): Intent

    /**
     * Предоставить фрагмент для показа статьи
     */
    fun getSingleArticleFragment(articleUuid: UUID): Fragment
}