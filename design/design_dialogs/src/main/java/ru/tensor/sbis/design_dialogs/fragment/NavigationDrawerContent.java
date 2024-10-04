package ru.tensor.sbis.design_dialogs.fragment;

 /** SelfDocumented */
public interface NavigationDrawerContent {

    enum ToolbarState {
        TITLE,
        TITLE_WITH_SUBTITLE,
        SPINNER,
        TITLE_WITH_SPINNER,
        CUSTOM_VIEW,
        TABS,
        HIDE
    }

    /** SelfDocumented */
    @SuppressWarnings("unused")
    ToolbarState getToolbarState();
}