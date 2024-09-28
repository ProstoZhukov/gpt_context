package ru.tensor.sbis.fresco_view.util

import androidx.annotation.GuardedBy
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.internal.Supplier
import com.facebook.datasource.AbstractDataSource
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.datasource.RetainingDataSourceSupplier
import java.util.Collections
import java.util.WeakHashMap

/**
 * Копия стандартного [RetainingDataSourceSupplier], но на Kotlin, и с уведомлением о неудачной загрузке изображения.
 * Событие ошибки фактического источника данных будет доставлено подписчикам поставляемого источника данных
 * посредством [DataSubscriber.onFailure], и будут доступны соответствующие результаты [DataSource.hasFailed] и
 * [DataSource.getFailureCause].
 *
 * @author us.bessonov
 */
class RetainingDataSourceSupplier<T> : Supplier<DataSource<T>> {

    private val mDataSources = Collections.newSetFromMap(WeakHashMap<RetainingDataSource<T>, Boolean>())

    private var mCurrentDataSourceSupplier: Supplier<DataSource<T>>? = null

    override fun get(): DataSource<T> {
        val dataSource = RetainingDataSource<T>()
        dataSource.setSupplier(mCurrentDataSourceSupplier)
        mDataSources.add(dataSource)
        return dataSource
    }

    /**
     * Заменяет текущий поставщик [DataSource]
     */
    fun replaceSupplier(supplier: Supplier<DataSource<T>>) {
        mCurrentDataSourceSupplier = supplier
        for (dataSource in mDataSources) {
            if (!dataSource.isClosed) {
                dataSource.setSupplier(supplier)
            }
        }
    }

    private class RetainingDataSource<T> : AbstractDataSource<T>() {

        @GuardedBy("RetainingDataSource.this")
        private var mDataSource: DataSource<T>? = null

        fun setSupplier(supplier: Supplier<DataSource<T>>?) {
            // early return without calling {@code supplier.get()} in case we are closed
            if (isClosed) {
                return
            }
            var oldDataSource: DataSource<T>?
            val newDataSource = supplier?.get()
            synchronized(this@RetainingDataSource) {
                if (isClosed) {
                    closeSafely(newDataSource)
                    return
                } else {
                    oldDataSource = mDataSource
                    mDataSource = newDataSource
                }
            }
            newDataSource?.subscribe(InternalDataSubscriber(), CallerThreadExecutor.getInstance())
            closeSafely(oldDataSource)
        }

        @Synchronized
        override fun getResult(): T? = mDataSource?.result

        @Synchronized
        override fun hasResult(): Boolean = mDataSource?.hasResult() == true

        override fun close(): Boolean {
            var dataSource: DataSource<T>?
            synchronized(this@RetainingDataSource) {

                // it's fine to call {@code super.close()} within a synchronized block because we don't
                // implement {@link #closeResult()}, but perform result closing ourselves.
                if (!super.close()) {
                    return false
                }
                dataSource = mDataSource
                mDataSource = null
            }
            closeSafely(dataSource)
            return true
        }

        private fun onDataSourceNewResult(dataSource: DataSource<T>) {
            if (dataSource === mDataSource) {
                setResult(null, false, dataSource.extras)
            }
        }

        // region Modified
        private fun onDataSourceFailed(dataSource: DataSource<T>) {
            if (dataSource == mDataSource) {
                setFailure(dataSource.failureCause, dataSource.extras)
            }
        }
        // endregion

        private fun onDatasourceProgress(dataSource: DataSource<T>) {
            if (dataSource === mDataSource) {
                progress = dataSource.progress
            }
        }

        private inner class InternalDataSubscriber : DataSubscriber<T> {
            override fun onNewResult(dataSource: DataSource<T>) {
                if (dataSource.hasResult()) {
                    onDataSourceNewResult(dataSource)
                } else if (dataSource.isFinished) {
                    // region Modified
                    onDataSourceFailed(dataSource)
                    // endregion
                }
            }

            override fun onFailure(dataSource: DataSource<T>) {
                // region Modified
                onDataSourceFailed(dataSource)
                // endregion
            }

            override fun onCancellation(dataSource: DataSource<T>) = Unit

            override fun onProgressUpdate(dataSource: DataSource<T>) {
                onDatasourceProgress(dataSource)
            }
        }

        override fun hasMultipleResults(): Boolean = true

        companion object {
            private fun <T> closeSafely(dataSource: DataSource<T>?) {
                dataSource?.close()
            }
        }
    }

}