package ru.tensor.sbis.design.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.Assert.assertNull
import kotlin.random.Random

/**
 * Тестирование правил работы с идентификаторами, которые записаны в параметр
 *
 * @author ma.kolpakov
 * Создан 9/28/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ViewAttributeUtilGetIdTest {

    /**
     * Для теста используются идентификаторы [R]. Идентификаторы записаны строками, что может привести к поломке теста
     * при удалении идентификаторов. Для восстановления нужно вписать в строку другие существующие идентификаторы.
     */
    private val attributeValue = "design_util_test_id"
    @StyleableRes
    private val attributeStyleId = 1

    @Mock
    private lateinit var attributes: TypedArray

    @Mock
    private lateinit var context: Context

    @Test
    fun `Nullable set if attribute is not defined`() {
        assertNull(attributes.getIdSet(context, attributeStyleId))
    }

    /**
     * @see isValidReferenceString
     */
    @Test(expected = IllegalArgumentException::class)
    fun `Exception on invalid parameter string`() {
        val invalidParameter = ""
        whenever(attributes.getString(attributeStyleId)).thenReturn(invalidParameter)

        attributes.getIdSet(context, attributeStyleId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception on duplicates in parameter string`() {
        val parameterStringWithDuplicates = "design_util_test_id,design_util_test_id"
        whenever(attributes.getString(attributeStyleId)).thenReturn(parameterStringWithDuplicates)

        attributes.getIdSet(context, attributeStyleId)
    }

    @Test
    fun `Attribute set for non empty parameter string`() {
        whenever(attributes.getString(attributeStyleId)).thenReturn(attributeValue)

        assertThat(attributes.getIdSet(context, attributeStyleId), hasItems(
            R.id.design_util_test_id
        ))
    }

    @Test
    fun `Get id from foreign resources`() {
        val idName = "testId"
        val idValue = Random.nextInt()
        val packageName = "test.package.name"
        val resources: Resources = mock()

        whenever(attributes.getString(attributeStyleId)).thenReturn(idName)
        whenever(context.resources).thenReturn(resources)
        whenever(context.packageName).thenReturn(packageName)
        whenever(resources.getIdentifier(idName, "id", packageName)).thenReturn(idValue)

        assertThat(attributes.getIdSet(context, attributeStyleId), hasItems(idValue))
        verify(resources, only()).getIdentifier(idName, "id", packageName)
    }

    @Test
    fun `Handler invoked on valid parameter string`() {
        val handler: (Set<Int>) -> Unit = mock()
        whenever(attributes.getString(attributeStyleId)).thenReturn(attributeValue)

        attributes.withIdSet(context, attributeStyleId, handler)

        verify(handler, only()).invoke(setOf(R.id.design_util_test_id))
    }

    @Test
    fun `Handler is not invoked if parameter is not defined`() {
        val handler: (Set<Int>) -> Unit = mock()

        attributes.withIdSet(context, attributeStyleId, handler)

        verifyNoMoreInteractions(handler)
    }
}