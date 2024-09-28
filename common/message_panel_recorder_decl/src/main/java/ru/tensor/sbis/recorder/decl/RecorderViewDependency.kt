package ru.tensor.sbis.recorder.decl

/**
 * Перечень инструментов, необходимых для работы RecorderView
 *
 * @author vv.chekurda
 * Создан 8/7/2019
 */
interface RecorderViewDependency {

    val recorderService: RecorderService

    val permissionMediator: RecordPermissionMediator

    val recordingListener: RecorderViewListener?

    val recorderView: RecorderView
}
