package ru.tensor.sbis.design_dialogs.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import ru.tensor.sbis.design.design_dialogs.databinding.DesignDialogsBottomPanelWithListBinding

/**
 * Simple Wrapper for BottomSheet dialogs like simple selection of filter etc...
 * like [BottomSelectionPane] but with custom view!
 */
class CustomBottomSheetDialog : AppCompatDialog {

    companion object {

        @JvmStatic
        fun createBottomSheetDialog(
            context: Context,
            adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
        ): CustomBottomSheetDialog {
            return createBottomSheetDialog(context, adapter, true)
        }

        @JvmStatic
        fun createBottomSheetDialog(
            context: Context,
            adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>,
            isClosePanelEnabled: Boolean
        ): CustomBottomSheetDialog {
            val dialog = CustomBottomSheetDialog(context)
            val viewBinding =
                DesignDialogsBottomPanelWithListBinding.inflate(LayoutInflater.from(context), null, false)
            viewBinding.bottomPanelCloseIcon.setOnClickListener { dialog.dismiss() }
            viewBinding.bottomPanelClose.visibility = if (isClosePanelEnabled) View.VISIBLE else View.GONE
            val recyclerView = viewBinding.bottomPanelList
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            recyclerView.adapter = adapter
            dialog.setContentView(viewBinding.root)
            return dialog
        }

        private fun getThemeResId(context: Context, themeId: Int = 0): Int {
            var themedId = themeId
            if (themedId == 0) {
                // If the provided theme is 0, then retrieve the dialogTheme from our theme
                val outValue = TypedValue()
                themedId = if (context.theme.resolveAttribute(
                        com.google.android.material.R.attr.bottomSheetDialogTheme,
                        outValue,
                        true
                    )
                ) {
                    outValue.resourceId
                } else {
                    com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog
                }
            }
            return themedId
        }
    }

    private var mBehavior: BottomSheetBehavior<FrameLayout>? = null
    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(
            bottomSheet: View,
            @BottomSheetBehavior.State newState: Int
        ) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
                mBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // ignored
        }

    }

    private var mCancelable = true
    private var mCanceledOnTouchOutside = true
    private var mCanceledOnTouchOutsideSet: Boolean = false

    @JvmOverloads
    constructor(
        context: Context,
        @StyleRes theme: Int = 0
    ) :
        super(context, getThemeResId(context, theme)) {
            // We hide the title bar for any style configuration. Otherwise, there will be a gap
            // above the bottom sheet when it is expanded.
            this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        }

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        mCancelable = cancelable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        if (mCancelable != cancelable) {
            mCancelable = cancelable
            if (mBehavior != null) {
                mBehavior!!.isHideable = cancelable
            }
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !mCancelable) {
            mCancelable = true
        }
        mCanceledOnTouchOutside = cancel
        mCanceledOnTouchOutsideSet = true
    }

    @SuppressLint("PrivateResource")
    @Suppress("NAME_SHADOWING")
    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        var view = view
        val viewGroup =
            View.inflate(
                context,
                com.google.android.material.R.layout.design_bottom_sheet_dialog,
                null
            ) as ViewGroup
        if (layoutResId != 0 && view == null) {
            view = layoutInflater.inflate(layoutResId, viewGroup, false)
        }
        val bottomSheet = viewGroup.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet.background = null
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        @Suppress("DEPRECATION")
        mBehavior!!.setBottomSheetCallback(mBottomSheetCallback)
        mBehavior!!.isHideable = mCancelable
        mBehavior!!.state = STATE_EXPANDED
        mBehavior!!.skipCollapsed = true
        if (params == null) {
            bottomSheet.addView(view)
        } else {
            bottomSheet.addView(view, params)
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        viewGroup.findViewById<View>(com.google.android.material.R.id.touch_outside).setOnClickListener {
            if (mCancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                cancel()
            }
        }
        return viewGroup
    }

    private fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!mCanceledOnTouchOutsideSet) {
            val a = context.obtainStyledAttributes(
                intArrayOf(android.R.attr.windowCloseOnTouchOutside)
            )
            mCanceledOnTouchOutside = a.getBoolean(0, true)
            a.recycle()
            mCanceledOnTouchOutsideSet = true
        }
        return mCanceledOnTouchOutside
    }

}
