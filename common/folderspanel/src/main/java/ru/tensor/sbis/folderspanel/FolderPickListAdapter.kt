package ru.tensor.sbis.folderspanel

import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter

/**@SelfDocumented*/
class FolderPickListAdapter : ViewModelAdapter() {

    init {
        cell<FolderViewModel>(R.layout.folderspanel_folder_alert_list_item_layout, areItemsTheSame = { oldItem, newItem -> oldItem.uuid == newItem.uuid })
    }
}