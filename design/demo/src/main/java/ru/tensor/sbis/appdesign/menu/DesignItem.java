package ru.tensor.sbis.appdesign.menu;

import androidx.annotation.StringRes;
import ru.tensor.sbis.appdesign.R;

/**
 * Created by da.pavlov1 on 15.11.2017.
 */

public enum DesignItem {
    ITEM_FOLDERS(R.string.folders_view),
    ITEM_FOLDERS_VIEWMODEL(R.string.folders_viewmodel),
    ITEM_SHARE_MULTISELECTION(R.string.combined_multiselection),
    ITEM_SCROLL_TO_TOP(R.string.scroll_to_top),
    ITEM_CONTEXT_MENU(R.string.context_menu),
    ITEM_CONTAINER(R.string.sbis_container),
    ITEM_HALL_SCHEME(R.string.hall_scheme),
    STUB_VIEW(R.string.stub_view),
    STUB_VIEW_NESTED(R.string.stub_view_nested),
    ITEM_INPUT_TEXT_BOX(R.string.input_text_box),
    ITEM_SINGLE_SELECTION(R.string.single_selection),
    ITEM_MULTI_SELECTION(R.string.multi_selection),
    ITEM_SINGLE_RECIPIENT_SELECTION(R.string.single_recipient_selection),
    ITEM_MULTI_RECIPIENT_SELECTION(R.string.multi_recipient_selection),
    ITEM_MULTI_RECIPIENT_SELECTION_COMMON_API(R.string.multi_recipient_selection_common_api),
    ITEM_SELECTION_PREVIEW(R.string.selection_preview),
    ITEM_NAVIGATION(R.string.navigation),
    ITEM_APP_BAR(R.string.app_bar),
    ITEM_FOLDER_VIEW(R.string.current_folder_view),
    ITEM_CLOUD_VIEW(R.string.cloud_view),
    ITEM_PIN_CODE(R.string.pin_code),
    ITEM_SKELETON_VIEW(R.string.skeleton_view),
    ITEM_TITLE_VIEW(R.string.title_view),
    ITEM_DATE_HEADER(R.string.title_date_header),
    ITEM_INPUT_VIEW(R.string.title_input_view);

    private final int idRes;

    DesignItem(int idRes) {
        this.idRes = idRes;
    }

    @StringRes
    public int getIdRes() {
        return idRes;
    }
}