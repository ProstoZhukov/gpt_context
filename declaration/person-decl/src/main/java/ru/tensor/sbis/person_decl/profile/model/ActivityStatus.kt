package ru.tensor.sbis.person_decl.profile.model

enum class ActivityStatus {
    /**
     * Online (в сети, на работе)
     */
    ONLINE_WORK,

    /**
     * Offline (вне сети, на работе)
     */
    OFFLINE_WORK,

    /**
     * Online (в сети, вне работы)
     */
    ONLINE_HOME,

    /**
     * Offline (вне сети, вне работы)
     */
    OFFLINE_HOME,

    /**
     * Статус активности не определён
     */
    UNKNOWN;
}