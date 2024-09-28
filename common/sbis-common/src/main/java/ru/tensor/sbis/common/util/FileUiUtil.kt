@file:JvmName("FileUiUtil")

package ru.tensor.sbis.common.util

import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Получение иконки, соответствующей типу файла
 *
 * @param type тип файла
 * @return иконка
 */
fun getNewFileIconByType(type: FileUtil.FileType): SbisMobileIcon.Icon =
    when (type) {
        FileUtil.FileType.ARCHIVE -> SbisMobileIcon.Icon.smi_zip
        FileUtil.FileType.AUDIO   -> SbisMobileIcon.Icon.smi_sound
        FileUtil.FileType.VIDEO   -> SbisMobileIcon.Icon.smi_video
        FileUtil.FileType.DOC,
        FileUtil.FileType.DOT,
        FileUtil.FileType.DOTH,
        FileUtil.FileType.DOTM    -> SbisMobileIcon.Icon.smi_word
        FileUtil.FileType.IMAGE   -> SbisMobileIcon.Icon.smi_img
        FileUtil.FileType.PDF     -> SbisMobileIcon.Icon.smi_pdf
        FileUtil.FileType.PPT     -> SbisMobileIcon.Icon.smi_ppt
        FileUtil.FileType.XLS     -> SbisMobileIcon.Icon.smi_xls
        FileUtil.FileType.URL,
        FileUtil.FileType.LINK    -> SbisMobileIcon.Icon.smi_link
        FileUtil.FileType.FOLDER  -> SbisMobileIcon.Icon.smi_PlayVideoCall
        FileUtil.FileType.XML,
        FileUtil.FileType.XHTML,
        FileUtil.FileType.HTML,
        FileUtil.FileType.HTM,
        FileUtil.FileType.MHT,
        FileUtil.FileType.MHTML,
        FileUtil.FileType.ODC,
        FileUtil.FileType.ODF,
        FileUtil.FileType.OTH,
        FileUtil.FileType.OTF,
        FileUtil.FileType.OTC     -> SbisMobileIcon.Icon.smi_DocumentXml
        FileUtil.FileType.SABYDOC -> SbisMobileIcon.Icon.smi_Sabydoc
        else                      -> getNewUnknownFileIcon()
    }

/**
 * Получение новой (нарисованной для диска, см. axure.tensor.ru/MobileAPP/#p=иконки__версия_02_&g=1) иконки неизвестного типа файла.
 *
 * @return иконка неизвестного типа файла
 */
fun getNewUnknownFileIcon(): SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_other

/**
 * Получение цвета иконки, соответствующей типу файла
 *
 * @param type тип файла
 * @return идентификатор ресурса
 */
@ColorRes
fun getNewFileIconColorResByType(type: FileUtil.FileType): Int =
    when (type) {
        FileUtil.FileType.ARCHIVE   -> R.color.file_type_archive
        FileUtil.FileType.AUDIO     -> R.color.file_type_audio
        FileUtil.FileType.VIDEO     -> R.color.file_type_video
        FileUtil.FileType.DOC,
        FileUtil.FileType.DOT,
        FileUtil.FileType.DOTH,
        FileUtil.FileType.DOTM,
        FileUtil.FileType.TXT       -> R.color.file_type_doc
        FileUtil.FileType.IMAGE     -> R.color.file_type_image
        FileUtil.FileType.PDF       -> R.color.file_type_pdf
        FileUtil.FileType.PPT       -> R.color.file_type_ppt
        FileUtil.FileType.XLS       -> R.color.file_type_xls
        FileUtil.FileType.URL,
        FileUtil.FileType.LINK      -> R.color.file_type_url
        FileUtil.FileType.FOLDER    -> R.color.file_type_folder
        else                        -> R.color.file_type_unknown
    }

@AttrRes
fun getNewFileIconColorAttrByType(type: FileUtil.FileType): Int =
    when (type) {
        // Синие
        FileUtil.FileType.DOC,
        FileUtil.FileType.DOT,
        FileUtil.FileType.DOTH,
        FileUtil.FileType.DOTM,
        FileUtil.FileType.TXT -> R.attr.secondaryIconColor
        // Красные
        FileUtil.FileType.PDF,
        FileUtil.FileType.PPT -> R.attr.dangerIconColor
        // Зеленые
        FileUtil.FileType.AUDIO,
        FileUtil.FileType.VIDEO,
        FileUtil.FileType.XLS -> R.attr.successIconColor
        // Серые
        else -> getNewUnknownFileIconColorAttr()
    }

@ColorRes
fun getNewUnknownFileIconColorRes(): Int = R.color.file_type_unknown

@AttrRes
fun getNewUnknownFileIconColorAttr(): Int = R.attr.unaccentedIconColor