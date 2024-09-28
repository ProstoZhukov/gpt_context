/*
  Copyright (c) 2016-present, RxJava Contributors.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software distributed under the License is
  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
  the License for the specific language governing permissions and limitations under the License.
 */
package ru.tensor.sbis.pushnotification.repository;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.*;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.*;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Аналог оператора {@link Observable#throttleLast(long, TimeUnit, Scheduler)}
 * Основное отличие - не запускает при подписке периодических задач, которые будут выполнятся
 * бесконечно, пока не произойдет отписка от источника.
 * Текущую реализацию следует использовать в синглтонах, где отписка от источника не происходит.
 *
 * @author am.boldinov
 */
final class ObservableThrottleLastTimed<T> extends Observable<T> implements HasUpstreamObservableSource<T> {

    private final ObservableSource<T> source;
    private final long timeout;
    private final TimeUnit unit;
    private final Scheduler scheduler;

    public ObservableThrottleLastTimed(ObservableSource<T> source, long timeout,
                                       TimeUnit unit, Scheduler scheduler) {
        this.source = source;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override
    public void subscribeActual(Observer<? super T> t) {
        source.subscribe(new DebounceTimedObserver<T>(
                new SerializedObserver<T>(t),
                timeout, unit, scheduler.createWorker()));
    }

    @Override
    public ObservableSource<T> source() {
        return source;
    }

    private static final class DebounceTimedObserver<T>
            extends AtomicReference<Disposable>
            implements Observer<T>, Disposable, Runnable {
        private static final long serialVersionUID = 786994795061867455L;

        private final Observer<? super T> downstream;
        private final long timeout;
        private final TimeUnit unit;
        private final Scheduler.Worker worker;

        private Disposable upstream;

        private volatile boolean gate;

        private boolean done;

        private final AtomicReference<T> last = new AtomicReference<>();

        DebounceTimedObserver(Observer<? super T> actual, long timeout, TimeUnit unit, Worker worker) {
            this.downstream = actual;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
        }

        @Override
        public void onSubscribe(@NotNull Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                downstream.onSubscribe(this);
            }
        }

        @Override
        public void onNext(@NotNull T t) {
            if (!done) {
                last.lazySet(t);
            }
            if (!gate && !done) {
                gate = true;

                Disposable d = get();
                if (d != null) {
                    d.dispose();
                }
                DisposableHelper.replace(this, worker.schedule(this, timeout, unit));
            }
        }

        @Override
        public void run() {
            emitLast(); // эмитим последний элемент
            gate = false;
        }

        @Override
        public void onError(@NotNull Throwable t) {
            if (done) {
                RxJavaPlugins.onError(t);
            } else {
                done = true;
                downstream.onError(t);
                worker.dispose();
            }
        }

        @Override
        public void onComplete() {
            if (!done) {
                done = true;
                emitLast();
                downstream.onComplete();
                worker.dispose();
            }
        }

        @Override
        public void dispose() {
            upstream.dispose();
            worker.dispose();
            last.set(null);
        }

        @Override
        public boolean isDisposed() {
            return worker.isDisposed();
        }

        private void emitLast() {
            final T value = last.getAndSet(null);
            if (value != null) {
                downstream.onNext(value);
            }
        }

    }
}