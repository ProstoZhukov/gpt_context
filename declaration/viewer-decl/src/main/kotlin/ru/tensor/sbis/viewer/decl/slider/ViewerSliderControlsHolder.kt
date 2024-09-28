package ru.tensor.sbis.viewer.decl.slider

import io.reactivex.Observable

interface ViewerSliderControlsHolder {

    val toolbarViewArgs: Observable<ViewerSliderControlViewParams>
    val thumbnailListViewArgs: Observable<ViewerSliderControlViewParams>
}