package ru.tensor.sbis.design.stubview.support

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.annotation.StringRes
import ru.tensor.sbis.design.text_span.SimpleInformationView

/**
 * Вспомогательный класс для сохранения состояния [StubView]
 *
 * @author ma.kolpakov
 * @since 06/05/2019
 */
@Suppress("KDocUnresolvedReference")
class StubViewState : View.BaseSavedState {

    private var visibilityState: Int = View.VISIBLE
    @StringRes
    private var headerResId: Int = 0
    @StringRes
    private var baseInfoResId: Int = 0
    @StringRes
    private var detailsResId: Int = 0

    constructor(superState: Parcelable?) : super(superState)
    constructor(parcel: Parcel) : super(parcel) {
        with(parcel) {
            visibilityState = readInt()
            headerResId = readInt()
            baseInfoResId = readInt()
            detailsResId = readInt()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.apply {
            writeInt(visibilityState)
            writeInt(headerResId)
            writeInt(baseInfoResId)
            writeInt(detailsResId)
        }
    }

    /**
     * Установить информацию для отображения
     */
    fun setContent(content: SimpleInformationView.Content?) {
        visibilityState = if (content == null) View.GONE else View.VISIBLE
        content?.let {
            headerResId = it.headerResId
            baseInfoResId = it.baseInfoResId
            detailsResId = it.detailsResId
        }
    }

    /** @SelfDocumented */
    fun getContent(context: Context) = if (visibilityState != View.VISIBLE) null else
        SimpleInformationView.Content(context, headerResId, baseInfoResId, detailsResId)

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<StubViewState> {
        override fun createFromParcel(parcel: Parcel): StubViewState = StubViewState(parcel)
        override fun newArray(size: Int): Array<StubViewState?> = arrayOfNulls(size)
    }
}