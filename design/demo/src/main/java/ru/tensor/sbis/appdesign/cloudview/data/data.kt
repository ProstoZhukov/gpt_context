package ru.tensor.sbis.appdesign.cloudview.data

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.core.content.ContextCompat
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.design.cloud_view.content.attachments.AttachmentClickListener
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import ru.tensor.sbis.design.cloud_view.content.certificate.DefaultSignature
import ru.tensor.sbis.design.cloud_view.content.quote.DefaultQuote
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.design.cloud_view.content.signing.SigningActionListener
import ru.tensor.sbis.design.cloud_view.model.*
import ru.tensor.sbis.design.profile.person.data.InitialsStubData
import ru.tensor.sbis.design.profile.person.data.PersonData
import ru.tensor.sbis.richtext.span.BlockQuoteSpan
import java.util.*
import kotlin.random.Random


private val ME = DefaultPersonModel(PersonData(UUID.randomUUID(), null, InitialsStubData.createByFullName("Зорькина Е.")), "Зорькина Е.")
private val PERSON_A = DefaultPersonModel(PersonData(UUID.randomUUID(), null, InitialsStubData.createByFullName("Зайцева М.")), "Зайцева М.")
private val PERSON_B = DefaultPersonModel(PersonData(UUID.randomUUID(), null, InitialsStubData.createByFullName("Соловьёва К.")), "Соловьёва К.")
private val PERSON_C = DefaultPersonModel(PersonData(UUID.randomUUID(), null, InitialsStubData.createByFullName("Катышев П.")), "Катышев П.")

private val previewUrls = listOf(
    "https://images.unsplash.com/photo-1604741866917-3108bcef5288?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80",
    "https://images.unsplash.com/photo-1552083375-1447ce886485?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
    "https://images.unsplash.com/photo-1560700059-dae0f5daa00e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1989&q=80",
    "https://images.unsplash.com/photo-1612361805155-7e4d6502ea13?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=717&q=80",
    "https://images.unsplash.com/photo-1612354266182-7d6ed4454bfc?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=562&q=80",
    "https://images.unsplash.com/photo-1612387364884-396d95cbd15e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
    "https://images.unsplash.com/photo-1612455897608-ea374d1e87f2?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1355&q=80",
    "https://images.unsplash.com/photo-1612454001981-ec4f7eed5cc8?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=637&q=80",
    "https://images.unsplash.com/photo-1593161499316-0cd67918ab6a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1981&q=80",
    "https://images.unsplash.com/photo-1613556817902-a1e2b15ebf22?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"
)

private var currentData = DemoData.SCENARIO_1

private enum class DemoData {
    SCENARIO_1, SCENARIO_5, SCENARIO_6, SCENARIO_7;

    fun next() = values()[(ordinal + 1) % values().size]
}

internal fun switchDemoData(context: Context): List<DemoCloudViewUserData> {
    currentData = currentData.next()
    return generateDemoData(context)
}

/**
 * @author ma.kolpakov
 */
internal fun generateDemoData(context: Context): List<DemoCloudViewUserData> = when(currentData) {
    DemoData.SCENARIO_1 -> SCENARIO_1
    DemoData.SCENARIO_5 -> scenario5(context)
    DemoData.SCENARIO_6 -> scenario6(context)
    DemoData.SCENARIO_7 -> scenario7(context)
}

/**
 * Диалог с одним адресатом
 *
 * TODO: 8/6/2020 подключить вложения https://online.sbis.ru/opendoc.html?guid=093c8d3c-454f-4351-9ecd-e6c0d48d4ecf
 */
private val SCENARIO_1: List<DemoCloudViewUserData>
    get() {
        val calendar = Calendar.getInstance()
        var id = 0
        return mutableListOf<DemoCloudViewUserData>().apply {
            val incomeTemplate = DemoIncomeCloudViewUserData(
                id++,
                null,
                calendar.time,
                null,
                null,
                DemoCloudViewData("Привет!"),
                false,
                false
            )
            add(incomeTemplate.copy(date = calendar.time))
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Ты же еще занимаешься тортами?")
                )
            )

            val outcomeTemplate = DemoOutcomeCloudViewUserData(
                id++,
                null,
                calendar.time,
                null,
                null,
                DemoCloudViewData("Привет!) Занимаюсь иногда, есть такое дело) Хочешь заказать к празднику?"),
                false,
                SendingState.IS_READ
            )
            calendar.add(Calendar.MINUTE, 2)
            add(outcomeTemplate)

            calendar.add(Calendar.MINUTE, 1)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Да, у меня у мамы день рождения)")
                )
            )

            calendar.add(Calendar.MINUTE, 1)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Да, у меня у мамы день рождения)")
                )
            )

            calendar.add(Calendar.MINUTE, 1)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Хотелось бы что-то типа такого:")/*, AttachmentCloudContent(emptyList())*/
                )
            )

            calendar.add(Calendar.MINUTE, 5)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Ой, или такого:")/*, AttachmentCloudContent(emptyList())*/
                )
            )

            calendar.add(Calendar.MINUTE, 7)
            add(
                outcomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Ну, внешний вид - это дело решаемое. Давай с начинкой определимся сначала))")
                )
            )

            calendar.add(Calendar.MINUTE, 1)
            add(
                outcomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Бисквит может быть цветным, шоколадным, или вообще красный бархат, и любые комбинации начинок")
                        /*, AttachmentCloudContent(emptyList())*/
                )
            )

            calendar.add(Calendar.MINUTE, 39)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("А можно вот такой? (внутри по первой фотке, а снаружи как в файле)")
                        /*, AttachmentCloudContent(emptyList())*/
                )
            )

            calendar.add(Calendar.MINUTE, 20)
            add(
                incomeTemplate.copy(
                    id = id++,
                    data = DemoCloudViewData("Смотри, какое видео нашла на youTube")/*, AttachmentCloudContent(emptyList())*/
                )
            )

            calendar.add(Calendar.MINUTE, 20)
            outcomeTemplate.copy(
                id = id++,
                data = DemoCloudViewData("В папке варианты наборов с начинками и тестом - глянь, как время будет")
                    /*, AttachmentCloudContent(emptyList())*/
            )
        }
    }

/**
 * Переписка по задаче
 */
private val SCENARIO_2: List<DemoCloudViewUserData>
    get() =
        TODO("TODO: 8/4/2020 https://online.sbis.ru/opendoc.html?guid=ac46b066-6935-437f-a04d-3312cd51ed08")

/**
 * Переписка по замечанию
 */
private val SCENARIO_3: List<DemoCloudViewUserData>
    get() =
        TODO("TODO: 8/4/2020 https://online.sbis.ru/opendoc.html?guid=ac46b066-6935-437f-a04d-3312cd51ed08")

/**
 * Диалог с несколькими адресатами
 */
private val SCENARIO_4: List<DemoCloudViewUserData>
    get() =
        TODO("TODO: 8/4/2020 https://online.sbis.ru/opendoc.html?guid=ac46b066-6935-437f-a04d-3312cd51ed08")

/**
 * Диалог с несколькими адресатами
 */
private fun scenario5(context: Context): List<DemoCloudViewUserData> = mutableListOf<DemoCloudViewUserData>().apply {
    // просто текст
    var id = 0
    val incomeTemplate = DemoIncomeCloudViewUserData(
        id++,
        createDate(4, 10, 2019),
        createTime(11, 28),
        PERSON_A,
        ReceiverInfo(ME, 2),
        DemoCloudViewData("Демо текста, который должен разместиться на несколько строк так как он достаточно длинный для этого"),
        true,
        false
    )
    add(incomeTemplate)

    // цитата
    val quoteColor = ContextCompat.getColor(context, R.color.text_color_link_2)
    val text = SpannableStringBuilder("Начало текста Цитата продолжение текста").apply {
        setSpan(BlockQuoteSpan(5, 2F, quoteColor), 14, 20, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
    val outcomeTemplate = DemoOutcomeCloudViewUserData(
        id++,
        null,
        createTime(14, 41),
        ME,
        ReceiverInfo(PERSON_A),
        DefaultCloudViewData(text, listOf(
            QuoteCloudContent(
                DefaultQuote(
                    UUID.randomUUID(),
                    UUID.randomUUID()
                ), DemoQuoteClickListener(context)
            )
        )),
        false,
        SendingState.IS_READ
    )
    add(outcomeTemplate)

    // сервисное сообщение
    val serviceColor = ContextCompat.getColor(context, R.color.text_color_link_2)
    add(incomeTemplate.copy(
        id = id++,
        date = createDate(13, 6, 2020),
        time = createTime(9, 11),
        data = DemoCloudViewData("Пример сервисного сообщения",
                                 ServiceCloudContent(
                                     "Приглашение к диалогу",
                                     serviceColor
                                 )
        )
    ))

    // подписи
    add(outcomeTemplate.copy(
        id = id++,
        date = null,
        time = createTime(12, 30),
        data = DemoCloudViewData("Пример подписей",
                                 SignatureCloudContent(
                                     DefaultSignature(
                                         "Заголовок моей подписи",
                                         true
                                     )
                                 ),
                                 SignatureCloudContent(
                                     DefaultSignature(
                                         "Заголовок чужой подписи",
                                         false
                                     )
                                 )
        )
    ))

    // кнопки подписей
    add(incomeTemplate.copy(
        id = id++,
        date = Date(),
        time = createTime(11, 11),
        data = DemoCloudViewData("Пример кнопок подписания",
                                 SigningButtonsCloudContent(
                                     DemoSigningActionListener(context)
                                 )
        )
    ))

    // вложенность
    add(outcomeTemplate.copy(
        id = id++,
        date = null,
        time = createTime(18, 45),
        data = DemoCloudViewData("Пример вложенного содержимого",
                                 ContainerCloudContent(
                                     listOf(
                                         1
                                     )
                                 ),
                                 SignatureCloudContent(
                                     DefaultSignature(
                                         "Заголовок чужой подписи",
                                         false
                                     )
                                 )
        )
    ))
}

private fun scenario6(context: Context): List<DemoCloudViewUserData> {
    val template = DemoOutcomeCloudViewUserData(0, null, Date(), null, null, DefaultCloudViewData(), false, SendingState.IS_READ)
    val attachmentClickListener = DemoAttachmentClickListener(context)
    val attachments = previewUrls.mapIndexed { i, it ->
        AttachmentCloudContent(
            DemoMessageAttachment(i, it), false, attachmentClickListener
        )
    }
    var id = 0
    return mutableListOf<DemoCloudViewUserData>().apply {
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Одно вложение",
                    attachments.shuffled().take(1)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Два вложения",
                    attachments.shuffled().take(2)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Три вложения",
                    listOf(
                        ServiceCloudContent(
                            "Документы подписаны (3 шт.)",
                            ContextCompat.getColor(context, R.color.color_accent_3)
                        )
                    ) + attachments.shuffled().take(3)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Четыре вложения",
                    attachments.shuffled().take(4)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Пять вложений",
                    attachments.shuffled().take(5)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Много вложений",
                    attachments.shuffled()
                )
            )
        )
    }
}

private fun scenario7(context: Context): List<DemoCloudViewUserData> {
    val template = DemoIncomeCloudViewUserData(0, null, Date(), null, null, DefaultCloudViewData(), false, false)
    val attachmentClickListener = DemoAttachmentClickListener(context)
    val signingActionListener = DemoSigningActionListener(context)

    val templateName = "This is file name, name is a four letter word, word is a combination of letters"
    val extensions = listOf(".doc", ".pdf", ".ppt", ".xml", ".rar", ".mp3", ".mp4", ".url", ".jpg", ".woof")
    val attachments = previewUrls.zip(extensions).mapIndexed { i, (url, ext) ->
        val usedUrl = url.takeIf { i % 2 == 0 }
        val signedByMe = i % 3 == 0
        val foreignSignsCount = if (i > 4) Random.nextInt(1, 11) else 0
        val name = templateName.take(Random.nextInt(1, templateName.length + 1)) + ext
        AttachmentCloudContent(
            DemoMessageAttachment(
                i,
                usedUrl,
                FileUtil.detectFileTypeByExtension(ext),
                name,
                signedByMe,
                foreignSignsCount
            ),
            false,
            attachmentClickListener
        )
    }

    var id = 0
    return mutableListOf<DemoCloudViewUserData>().apply {
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Одно вложение",
                    listOf(
                        ContainerCloudContent(listOf(1, 2, 3)),
                        ServiceCloudContent("Запрос подписи", getAccentColor(context))
                    ) + attachments.shuffled().take(1)
                            + SigningButtonsCloudContent(signingActionListener),
                    rootElements = listOf(0)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Два вложения",
                    listOf(
                        ContainerCloudContent(listOf(1, 2, 3)),
                        ServiceCloudContent("Запрос подписи", getAccentColor(context))
                    ) + attachments.shuffled().take(2),
                    rootElements = listOf(0),
                    isDisabledStyle = true
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Три вложения",
                    listOf(
                        ContainerCloudContent(listOf(1, 2, 3, 4, 5)),
                        ServiceCloudContent("Запрос подписи", getAccentColor(context))
                    ) + attachments.shuffled().take(3)
                            + SigningButtonsCloudContent(signingActionListener),
                    rootElements = listOf(0)
                )
            )
        )
        add(
            template.copy(
                id = id++,
                data = DefaultCloudViewData(
                    "Много вложений",
                    listOf(
                        ContainerCloudContent(listOf(1, 2, 3)),
                        ServiceCloudContent("Запрос подписи", getAccentColor(context))
                    ) + attachments.shuffled(),
                    rootElements = listOf(0),
                    isDisabledStyle = true
                )
            )
        )
    }
}


private fun createDate(day: Int, month: Int, year: Int): Date {
    return Calendar.getInstance()
        .apply { set(year, month + 1, day) }
        .time
}

private fun createTime(hour: Int, minute: Int): Date {
    return Calendar.getInstance()
        .apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        .time
}

private fun getAccentColor(context: Context) = ContextCompat.getColor(context, R.color.color_accent_3)

private class DemoQuoteClickListener(
    private val context: Context
) : QuoteClickListener {

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        Toast.makeText(context, "Quote clicked", Toast.LENGTH_SHORT).show()
    }
}

private class DemoSigningActionListener(
    private val context: Context
) : SigningActionListener {

    override fun onAcceptClicked() {
        Toast.makeText(context, "Accept button clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDeclineClicked() {
        Toast.makeText(context, "Reject button clicked", Toast.LENGTH_SHORT).show()
    }
}

private class DemoAttachmentClickListener(
    private val context: Context
) : AttachmentClickListener {

    override fun onAttachmentClicked(context: Context, attachment: MessageAttachment, cloudViewData: CloudViewData) {
        Toast.makeText(context, "Attachment ${attachment.id.localId} clicked", Toast.LENGTH_SHORT).show()
    }
}