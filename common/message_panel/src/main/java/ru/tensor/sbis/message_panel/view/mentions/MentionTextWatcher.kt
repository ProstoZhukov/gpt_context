package ru.tensor.sbis.message_panel.view.mentions

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.apache.commons.lang3.StringUtils

/**
 * API TextWatcher-а, используемого для создания упоминаний.
 *
 * @author dv.baranov
 */
internal interface MentionTextWatcher : TextWatcher {

    /**
     * Текущая позиция каретки поля ввода.
     */
    var carriagePosition: Int

    /**
     * Подписка на показ шторки.
     */
    val needShowMentionSelection: Observable<Boolean>

    /**
     * Подписка для инициализации шторки.
     */
    val needStartMentionSelection: Observable<Boolean>

    /**
     * Подписка на текст вводимого упоминания.
     */
    val mentionSearchQuery: Observable<String>

    /**
     * Подписка на список текущих упоминаний.
     */
    val currentMentions: Observable<List<MentionSpan>>

    /**
     * Сбросить watcher к начальному состоянию.
     */
    fun reset()

    /**
     * Выполнить действия перед созданием упоминания.
     */
    fun onBeforeMentionInsertion()

    /**
     * Выполнить действия после создания упоминания.
     */
    fun onMentionInserted(text: Editable?)

    /**
     * Вызвать принудительно обновление поиска по упоминаниям.
     */
    fun updateSearchQuery(text: Editable?)

    /**
     * Получить текущее вводимое упоминание (определяется исходя из позиции каретки).
     */
    fun getEnterMention(text: Editable): EnterMentionSpan?

    /**
     * Принудительно ограничить вводимое упоминание (ввод больше трех слов через пробелы, неудачный поиск).
     */
    fun limitEnterMention(text: Editable)

    /**
     * Установить упоминания из драфта.
     */
    fun setMentionsFromDraft(mentions: List<MentionSpan>)

    /**
     * Установить текст сообщения из драфта.
     */
    fun setDraftMessage(text: String)
}

/**
 * TextWatcher, следящий за изменениями поля ввода панели сообщений, по которым он определяет:
 * 1) Нужно ли показать шторку выбора персоны для создания упоминания (Ввод символа @).
 * 2) Нужно ли отменить выбор персоны для упоминания (Стирание символа @).
 * 3) Какие упоминания нужно удалить после пользовательского ввода (Любое изменение текста в готовом упоминании).
 *
 * @author dv.baranov
 */
internal class MentionTextWatcherImpl(private val context: Context) : MentionTextWatcher {

    override var carriagePosition: Int = DEFAULT_POSITION

    private var ignoreTextChanges: Boolean = false
    private var oldText: String = StringUtils.EMPTY

    private var brokenMentions: Set<MentionSpan> = emptySet()
    private var needDeleteBrokenMentions: Boolean = false
    private var mentionsFromDraft: List<MentionSpan> = emptyList()
    private var draftText: String = StringUtils.EMPTY

    override val needShowMentionSelection = BehaviorSubject.createDefault(false)
    override val needStartMentionSelection = BehaviorSubject.createDefault(false)
    override val mentionSearchQuery = BehaviorSubject.createDefault(StringUtils.EMPTY)
    override val currentMentions = BehaviorSubject.createDefault(emptyList<MentionSpan>())

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        if (count != 0 && after == 0) {
            (text as? Editable)?.clearEnterMentions(start, start + count)
        }
        oldText = text?.toString() ?: StringUtils.EMPTY
    }

    private fun Editable.clearEnterMentions(start: Int, end: Int) {
        val mentions = getSpans(start, end, EnterMentionSpan::class.java)
        for (mention in mentions) {
            if (mention.data.start in start..end) {
                removeSpan(mention)
            }
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        if (text == null || ignoreTextChanges || text.toString() == oldText) return
        if (canStartMentionSelection(text.toString(), start, count)) {
            needStartMentionSelection.onNext(true)
            needShowMentionSelection.onNext(true)
            (text as? Editable)?.addEnterMention(start)
        }
        if (!currentMentions.value.isNullOrEmpty()) {
            brokenMentions = getBrokenMentions(
                start,
                start + before
            )
            if (brokenMentions.isNotEmpty()) {
                needDeleteBrokenMentions = true
            }
        }
    }

    /**
     * Проверить можно ли поднять шторку для создания упоминания на основе изменений текста.
     * Шторка поднимается, когда:
     * 1. @ пишется в начале строки.
     * 2. если перед @ есть пробел.
     */
    private fun canStartMentionSelection(
        currentText: String,
        startOfTextChanges: Int,
        countOfNewChars: Int
    ): Boolean = when {
        currentText.isEmpty() || currentText.isBlank() || countOfNewChars != 1 -> false
        currentText[startOfTextChanges] == '@' -> {
            if (startOfTextChanges == 0) {
                true
            } else {
                val beforeAtSign = currentText[startOfTextChanges - 1]
                beforeAtSign.isWhitespace() || beforeAtSign == '\n'
            }
        }

        else -> false
    }

    private fun Editable.addEnterMention(start: Int) {
        if (getSpans(start, start + 1, EnterMentionSpan::class.java).isEmpty()) {
            setSpan(EnterMentionSpan(context), start, start + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }

    private fun getBrokenMentions(
        startOfTextChanges: Int,
        endOfTextChanges: Int
    ): Set<MentionSpan> {
        val brokenMentions = mutableListOf<MentionSpan>()
        currentMentions.value?.forEach {
            val mentionRange = it.data.start..it.data.end
            val isStartChangesIn = startOfTextChanges in mentionRange
            val isEndChangesIn = endOfTextChanges in mentionRange
            if (isStartChangesIn || isEndChangesIn) {
                brokenMentions.add(it)
            }
        }
        return brokenMentions.toSet()
    }

    override fun afterTextChanged(text: Editable?) {
        when {
            text == null -> return
            text.isBlank() -> {
                currentMentions.onNext(emptyList())
                needShowMentionSelection.onNext(false)
            }

            mentionsFromDraft.isNotEmpty() && mentionsFromDraft.last().data.end <= text.length &&
                draftText == text.toString() -> {
                text.insertMentionsFromDraft()
            }

            else -> {
                if (mentionsFromDraft.isNotEmpty()) { resetDraftInfo() }
                val mentionsAddedToText = text.getSpans(0, text.length, MentionSpan::class.java).toList()
                mentionsAddedToText.updateBounds(text)
                currentMentions.onNext(mentionsAddedToText)
                if (brokenMentions.isNotEmpty() && mentionsAddedToText.isNotEmpty()) {
                    text.removeBrokenMentions(mentionsAddedToText)
                }
                updateSearchQuery(text)
            }
        }
    }

    private fun Editable.insertMentionsFromDraft() {
        for (mention in mentionsFromDraft) {
            setSpan(mention, mention.data.start, mention.data.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (draftText.length == mention.data.end) insert(mention.data.end, StringUtils.SPACE)
        }
        currentMentions.onNext(mentionsFromDraft)
        mentionsFromDraft = emptyList()
        draftText = StringUtils.EMPTY
    }

    private fun List<MentionSpan>.updateBounds(text: Editable) {
        for (mentionSpan in this) {
            val start = text.getSpanStart(mentionSpan)
            val end = text.getSpanEnd(mentionSpan)
            mentionSpan.setBounds(start, end)
        }
    }

    private fun Editable.removeBrokenMentions(mentions: List<MentionSpan>) {
        var startPositionForRemove = Int.MAX_VALUE
        var endPositionForRemove = Int.MIN_VALUE
        for (mentionSpan in mentions) {
            if (brokenMentions.contains(mentionSpan)) {
                with(mentionSpan.data) {
                    if (start < startPositionForRemove) startPositionForRemove = start
                    if (end > endPositionForRemove) endPositionForRemove = end
                    removeSpan(mentionSpan)
                }
            }
        }
        if (startPositionForRemove == Int.MAX_VALUE || endPositionForRemove == Int.MIN_VALUE || !needDeleteBrokenMentions) return
        replace(startPositionForRemove, endPositionForRemove, StringUtils.EMPTY)
        needDeleteBrokenMentions = false
    }

    private fun resetDraftInfo() {
        mentionsFromDraft = emptyList()
        draftText = StringUtils.EMPTY
    }

    override fun reset() {
        brokenMentions = emptySet()
        needStartMentionSelection.onNext(false)
        needShowMentionSelection.onNext(false)
        mentionSearchQuery.onNext(StringUtils.EMPTY)
        currentMentions.onNext(emptyList())
        resetDraftInfo()
    }

    override fun onBeforeMentionInsertion() {
        ignoreTextChanges = true
    }

    /**
     * Выполнить действия после создания упоминания.
     */
    override fun onMentionInserted(text: Editable?) {
        ignoreTextChanges = false
        needShowMentionSelection.onNext(false)
        afterTextChanged(text)
    }

    override fun updateSearchQuery(text: Editable?) {
        val enterMentionSpan = text?.let { getEnterMention(text) }
        if (enterMentionSpan != null) {
            if (enterMentionSpan.data.end > text.length || enterMentionSpan.data.start == enterMentionSpan.data.end) return
            if (enterMentionSpan.needLimit(text.toString())) {
                limitEnterMention(text)
                return
            }
            if (needStartMentionSelection.value == false) {
                needStartMentionSelection.onNext(true)
            }
            mentionSearchQuery.onNext(getMentionSearchQuery(text.toString(), enterMentionSpan))
            needShowMentionSelection.onNext(true)
        } else {
            mentionSearchQuery.onNext(StringUtils.EMPTY)
            needShowMentionSelection.onNext(false)
        }
    }

    override fun getEnterMention(text: Editable): EnterMentionSpan? {
        val endPos = if (carriagePosition <= 0) 1 else carriagePosition
        val currentEnterMentions = text.getSpans(endPos - 1, endPos, EnterMentionSpan::class.java)
        return currentEnterMentions.lastOrNull()?.apply {
            setBounds(text.getSpanStart(this), text.getSpanEnd(this))
        }
    }

    private fun EnterMentionSpan.needLimit(text: String): Boolean = when {
        text.length < 2 -> false
        text[data.end - 1].isWhitespace() -> text.subSequence(data.start, data.end - 1)
            .find { it.isWhitespace() } != null

        else -> false
    }

    private fun getMentionSearchQuery(messageText: String, enterMentionSpan: EnterMentionSpan): String =
        messageText.subSequence(enterMentionSpan.data.start + 1, enterMentionSpan.data.end).toString().trim()

    override fun limitEnterMention(text: Editable) {
        val mention = getEnterMention(text) ?: return
        text.removeSpan(mention)
        text.setSpan(mention, mention.data.start, mention.data.end - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    }

    override fun setMentionsFromDraft(mentions: List<MentionSpan>) {
        mentionsFromDraft = mentions
    }

    override fun setDraftMessage(text: String) {
        draftText = text
    }
}

private const val DEFAULT_POSITION = 0
