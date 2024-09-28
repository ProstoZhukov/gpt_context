package ru.tensor.sbis.link_opener.data

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.link_opener.domain.parser.LinkUriMapper
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import java.net.URLDecoder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@Suppress("SpellCheckingInspection")
internal class LinkUriMapperTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val mapper = LinkUriMapper()

    @Test
    fun `Create uri by preview test case 1`() {
        val preview = InnerLinkPreview(
            url = "https://online.sbis.ru/opendoc.html?guid=17d39976-afd3-4cd3-8c26-f21b8989d57d&client=3",
            docUuid = "17d39976-afd3-4cd3-8c26-f21b8989d57d",
            title = "",
            docType = DocType.DOCUMENT,
            docSubtype = LinkDocSubtype.INCOMING_PAYMENT
        )
        val uri = mapper.marshal(preview).toString()
        assertEquals(
            "sabylink://document.incoming_payment/details?uuid=17d39976-afd3-4cd3-8c26-f21b8989d57d" +
             "&href=https%3A%2F%2Fonline.sbis.ru%2Fopendoc.html%3Fguid%3D17d39976-afd3-4cd3-8c26-f21b8989d57d%26client%3D3",
            uri
        )
    }

    @Test
    fun `Create uri by preview test case 2`() {
        val preview = InnerLinkPreview(
            url = "https://online.sbis.ru/opendoc.html?guid=2752add2-3b77-4d95-bce5-638f2adf3296&client=3",
            docUuid = "2752add2-3b77-4d95-bce5-638f2adf3296",
            title = "",
            docType = DocType.ARTICLE
        )
        val uri = mapper.marshal(preview).toString()
        assertEquals(
            "sabylink://article/details?uuid=2752add2-3b77-4d95-bce5-638f2adf3296" +
             "&href=https%3A%2F%2Fonline.sbis.ru%2Fopendoc.html%3Fguid%3D2752add2-3b77-4d95-bce5-638f2adf3296%26client%3D3",
            uri
        )
    }

    @Test
    fun `Create uri by preview test case 3`() {
        val preview = InnerLinkPreview(
            url = "https://online.sbis.ru/person/45b13edc-a84a-6dce-ba7e-dba4a64345a1",
            docUuid = "45b13edc-a84a-6dce-ba7e-dba4a64345a1",
            title = "Карточка сотрудника",
            docType = DocType.PERSON,
            rawDocSubtype = "Поощрение"
        )
        val uri = mapper.marshal(preview).toString()
        assertEquals(
            "sabylink://person/details?uuid=45b13edc-a84a-6dce-ba7e-dba4a64345a1&rawSubtype=Поощрение" +
             "&title=Карточка сотрудника&href=https://online.sbis.ru/person/45b13edc-a84a-6dce-ba7e-dba4a64345a1",
            URLDecoder.decode(uri, "UTF-8")
        )
    }

    @Test
    fun `Create preview from uri test case 1`() {
        val uri = Uri.parse("sabylink://document.task_note/details" +
                "?uuid=99fdab7a-a481-42d7-a844-b796a827a201" +
                "&rawSubtype=%D0%A1%D0%BB%D1%83%D0%B6%D0%97%D0%B0%D0%BF" +
                "&title=%D0%9A%D0%B0%D0%BB%D0%B5%D0%BD%D0%B4%D0%B0%D1%80%D0%B5%D0%B2%20%D0%90.")
        val preview = mapper.unmarshal(uri)

        assertEquals("99fdab7a-a481-42d7-a844-b796a827a201", preview.docUuid)
        assertEquals(DocType.DOCUMENT, preview.docType)
        assertEquals(LinkDocSubtype.TASK_NOTE, preview.docSubtype)
        assertEquals("СлужЗап", preview.rawDocSubtype)
        assertEquals("Календарев А.", preview.title)
    }

    @Test
    fun `Create preview from uri test case 2`() {
        val uri = Uri.parse("sabylink://contractor/details" +
                "?uuid=99fdab7a-a481-42d7-a844-b796a827a201" +
                "&title=%D0%9A%D0%B0%D0%BB%D0%B5%D0%BD%D0%B4%D0%B0%D1%80%D0%B5%D0%B2%20%D0%90.")
        val preview = mapper.unmarshal(uri)

        assertEquals("99fdab7a-a481-42d7-a844-b796a827a201", preview.docUuid)
        assertEquals(DocType.CONTRACTOR, preview.docType)
        assertEquals(LinkDocSubtype.UNKNOWN, preview.docSubtype)
        assertEquals("", preview.rawDocSubtype)
        assertEquals("Календарев А.", preview.title)
    }

    @Test
    fun `Create preview from uri test case 3`() {
        val uri = Uri.parse("sabylink://.task_delivery_task/details" +
                "?uuid=848172-581daj-2813ha" +
                "&rawSubtype=IMAGE")
        val preview = mapper.unmarshal(uri)

        assertEquals("848172-581daj-2813ha", preview.docUuid)
        assertEquals(DocType.UNKNOWN, preview.docType)
        assertEquals(LinkDocSubtype.TASK_DELIVERY_TASK, preview.docSubtype)
        assertEquals("IMAGE", preview.rawDocSubtype)
        assertEquals("", preview.title)
    }

    @Test
    fun `Create preview from uri test case 4`() {
        val uri = Uri.parse("sabylink://group_discussion_question.event/details" +
                "?uuid=99fdab7a-a481-42d7-a844-b796a827a201")
        val preview = mapper.unmarshal(uri)

        assertEquals("99fdab7a-a481-42d7-a844-b796a827a201", preview.docUuid)
        assertEquals(DocType.GROUP_DISCUSSION_QUESTION, preview.docType)
        assertEquals(LinkDocSubtype.EVENT, preview.docSubtype)
        assertEquals("", preview.rawDocSubtype)
        assertEquals("", preview.title)
    }

    @Test
    fun `Create preview from uri test case 5`() {
        val uri = Uri.parse("sabylink://document/details" +
                "?uuid=99fdab7a-a481-42d7-a844-b796a827a201&href=https://tensor.ru")
        val preview = mapper.unmarshal(uri)

        assertEquals("99fdab7a-a481-42d7-a844-b796a827a201", preview.docUuid)
        assertEquals(DocType.DOCUMENT, preview.docType)
        assertEquals(LinkDocSubtype.UNKNOWN, preview.docSubtype)
        assertEquals("", preview.rawDocSubtype)
        assertEquals("https://tensor.ru", preview.href)
        assertEquals("", preview.title)
    }
}