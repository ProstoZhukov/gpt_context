package ru.tensor.sbis.design_dialogs.movablepanel

import androidx.coordinatorlayout.widget.CoordinatorLayout

class BlockingUnderlyingViewsInteractionBehavior : CoordinatorLayout.Behavior<MovablePanel>() {

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: MovablePanel): Boolean {
        // тут нужно вернуть true, если панель сейчас отображается
        return child.peekHeight is MovablePanelPeekHeight.FitToContent
    }
}