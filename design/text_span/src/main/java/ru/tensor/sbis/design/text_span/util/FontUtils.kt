package ru.tensor.sbis.design.text_span.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan

/**
 * @author am.boldinov
 */

/**
 * Обернуть исходную строку в шрифт Roboto Regular.
 */
fun asRoboto(context: Context, text: CharSequence?) = setTypeface(text, TypefaceManager.getRobotoRegularFont(context))

/**
 * Обернуть исходную строку в шрифт Roboto Bold.
 */
fun asRobotoBold(context: Context, text: CharSequence?) = setTypeface(text, TypefaceManager.getRobotoBoldFont(context))

/**
 * Обернуть исходную строку в шрифт Roboto Light.
 */
fun asRobotoLight(context: Context, text: CharSequence?) =
    setTypeface(text, TypefaceManager.getRobotoLightFont(context))

/**
 * Обернуть исходную строку в шрифт CbucIcon.
 */
@Suppress("unused")
fun asSbisIcon(context: Context, text: CharSequence?) = setTypeface(text, TypefaceManager.getCbucIconTypeface(context))

/**
 * Обернуть исходную строку в шрифт SbisMobileIcon.
 */
@Suppress("unused")
fun asMobileIcon(context: Context, text: CharSequence?) =
    setTypeface(text, TypefaceManager.getSbisMobileIconTypeface(context))

/**
 * Обернуть исходную строку в указанный шрифт.
 */
fun setTypeface(context: Context, text: CharSequence?, fontResId: Int) =
    setTypeface(text, typeface(context, fontResId))

/**
 * Обернуть исходную строку в указанный шрифт.
 */
fun setTypeface(text: CharSequence?, typeface: Typeface?) =
    text?.let {
        SpannableString(text)
            .apply {
                typeface?.let {
                    fullSpan(CustomTypefaceSpan(typeface))
                }
            }
    }

/**
 * Задать span на весь [Spannable].
 */
fun Spannable.fullSpan(span: Any) =
    setSpan(span, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

/**
 * Получить шрифт из ресурсов по указанному идентификатору.
 */
private fun typeface(context: Context, fontResId: Int) =
    ResourcesCompat.getFont(context, fontResId)