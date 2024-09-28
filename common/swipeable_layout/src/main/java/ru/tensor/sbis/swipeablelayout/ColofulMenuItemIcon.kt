package ru.tensor.sbis.swipeablelayout

import androidx.annotation.AttrRes
import ru.tensor.sbis.swipeable_layout.R

/**
 * Набор стандартных иконок свайп-меню, значок и цвет фона у которых заданы в атрибутах темы
 * http://axure.tensor.ru/MobileStandart8/#g=1&p=%D0%B8%D0%BA%D0%BE%D0%BD%D0%BA%D0%B8__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_02_
 *
 * @property iconAttr атрибут с значением иконки меню
 * @property colorAttr атрибут с цветом фона пункта меню
 *
 * @author us.bessonov
 */
@Deprecated(
    "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
    ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon")
)
sealed class ColorfulMenuItemIcon(@AttrRes val iconAttr: Int, @AttrRes val colorAttr: Int)
object DeleteIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_deleteIcon, R.attr.SwipeableLayout_deleteIconColor)
object VideoCallIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_videoCallIcon, R.attr.SwipeableLayout_videoCallIconColor)

object MessageIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_messageIcon, R.attr.SwipeableLayout_messageIconColor)
object CallIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_callIcon, R.attr.SwipeableLayout_callIconColor)
object ReadIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_readIcon, R.attr.SwipeableLayout_readIconColor)
object UnreadIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_unreadIcon, R.attr.SwipeableLayout_unreadIconColor)
object ExecuteIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_executeIcon, R.attr.SwipeableLayout_executeIconColor)
object MoveToFolderIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_moveToFolderIcon, R.attr.SwipeableLayout_moveToFolderIconColor)

object AdminIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_adminIcon, R.attr.SwipeableLayout_adminIconColor)
object AdminOffIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_adminOffIcon, R.attr.SwipeableLayout_adminOffIconColor)

object RenameIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_renameIcon, R.attr.SwipeableLayout_renameIconColor)
object AddFolderIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_addFolderIcon, R.attr.SwipeableLayout_addFolderIconColor)

object DownloadIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_downloadIcon, R.attr.SwipeableLayout_downloadIconColor)

object ShareIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_shareIcon, R.attr.SwipeableLayout_shareIconColor)
object FavoriteFilledIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_favoriteFilledIcon, R.attr.SwipeableLayout_favoriteFilledIconColor)

object FavoriteIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_favoriteIcon, R.attr.SwipeableLayout_favoriteIconColor)

object PinIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_pinIcon, R.attr.SwipeableLayout_pinIconColor)
object UnpinIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_unpinIcon, R.attr.SwipeableLayout_unpinIconColor)
object RecoverIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_recoverIcon, R.attr.SwipeableLayout_recoverIconColor)
object OutHotelIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_outHotelIcon, R.attr.SwipeableLayout_outHotelIconColor)

object LockIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_lockIcon, R.attr.SwipeableLayout_lockIconColor)
object UnlockIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_unlockIcon, R.attr.SwipeableLayout_unlockIconColor)
object MoreIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_moreIcon, R.attr.SwipeableLayout_moreIconColor)
object EditIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_editIcon, R.attr.SwipeableLayout_editIconColor)
object UnPublishIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_unPublishIcon, R.attr.SwipeableLayout_unPublishIconColor)

object SuccessfulDocumentIcon : ColorfulMenuItemIcon(
    R.attr.SwipeableLayout_successfulDocumentIcon,
    R.attr.SwipeableLayout_successfulDocumentIconColor
)

object SuccessfulNoneIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_successfulNoneIcon, R.attr.SwipeableLayout_successfulNoneIconColor)

object MasterIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_masterIcon, R.attr.SwipeableLayout_masterIconColor)
object CopyIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_copyIcon, R.attr.SwipeableLayout_copyIconColor)
object ShareDocumentIcon :
    ColorfulMenuItemIcon(R.attr.SwipeableLayout_shareDocumentIcon, R.attr.SwipeableLayout_shareDocumentIconColor)

object InfoIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_infoIcon, R.attr.SwipeableLayout_infoIconColor)
object SendIcon : ColorfulMenuItemIcon(R.attr.SwipeableLayout_sendIcon, R.attr.SwipeableLayout_sendIconColor)