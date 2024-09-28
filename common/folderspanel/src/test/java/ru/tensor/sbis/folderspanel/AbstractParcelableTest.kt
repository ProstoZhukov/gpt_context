package ru.tensor.sbis.folderspanel

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.CallSuper
import org.junit.Assert.assertNotSame

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
abstract class AbstractParcelableTest<T : Parcelable> {

    protected abstract val parcelableCreator: Parcelable.Creator<T>

    protected fun saveAndRestore(model: T): T {
        return packInParcelAndExtract(model, parcelableCreator)
    }

    @CallSuper
    protected fun checkAfterRestore(originVm: T, restoredVm: T) {
        assertNotSame(originVm, restoredVm)
    }

    private fun <T> packInParcelAndExtract(originParcelable: Parcelable, parcelableCreator: Parcelable.Creator<T>): T {
        val parcel = Parcel.obtain()

        originParcelable.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        return parcelableCreator.createFromParcel(parcel)
    }
}
