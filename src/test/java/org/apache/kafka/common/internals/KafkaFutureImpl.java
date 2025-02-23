/*
 * Copyright (c) 2023 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.common.internals;

import org.apache.kafka.common.KafkaFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaFutureImpl<T> extends KafkaFuture<T> {
    private static void wrapAndThrow(Throwable t) throws InterruptedException, ExecutionException {
        if (t instanceof CancellationException)
            throw (CancellationException) t;
        if (t instanceof InterruptedException)
            throw (InterruptedException) t;
        throw new ExecutionException(t);
    }

    private static class Applicant<A, B> implements BiConsumer<A, Throwable> {
        private final BaseFunction<A, B> function;

        private final KafkaFutureImpl<B> future;

        Applicant(BaseFunction<A, B> function, KafkaFutureImpl<B> future) {
            this.function = function;
            this.future = future;
        }

        public void accept(A a, Throwable exception) {
            if (exception != null) {
                this.future.completeExceptionally(exception);
            } else {
                try {
                    B b = this.function.apply(a);
                    this.future.complete(b);
                } catch (Throwable t) {
                    this.future.completeExceptionally(t);
                }
            }
        }
    }

    private static class SingleWaiter<R> implements BiConsumer<R, Throwable> {
        private R value = null;

        private Throwable exception = null;

        private boolean done = false;

        public synchronized void accept(R newValue, Throwable newException) {
            this.value = newValue;
            this.exception = newException;
            this.done = true;
            notifyAll();
        }

        synchronized R await() throws InterruptedException, ExecutionException {
            while (true) {
                if (this.exception != null)
                    KafkaFutureImpl.wrapAndThrow(this.exception);
                if (this.done)
                    return this.value;
                wait();
            }
        }

        R await(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            long startMs = System.currentTimeMillis();
            long waitTimeMs = unit.toMillis(timeout);
            long delta = 0L;
            synchronized (this) {
                while (true) {
                    if (this.exception != null)
                        KafkaFutureImpl.wrapAndThrow(this.exception);
                    if (this.done)
                        return this.value;
                    if (delta >= waitTimeMs)
                        throw new TimeoutException();
                    wait(waitTimeMs - delta);
                    delta = System.currentTimeMillis() - startMs;
                }
            }
        }

        private SingleWaiter() {
        }
    }

    private boolean done = false;

    private T value = null;

    private Throwable exception = null;

    private List<BiConsumer<? super T, ? super Throwable>> waiters = new ArrayList<>();

    public <R> KafkaFuture<R> thenApply(BaseFunction<T, R> function) {
        KafkaFutureImpl<R> future = new KafkaFutureImpl<>();
        addWaiter(new Applicant<>(function, future));
        return future;
    }

    public <R> void copyWith(KafkaFuture<R> future, BaseFunction<R, T> function) {
        KafkaFutureImpl<R> futureImpl = (KafkaFutureImpl<R>) future;
        futureImpl.addWaiter(new Applicant<>(function, this));
    }

    public <R> KafkaFuture<R> thenApply(Function<T, R> function) {
        return thenApply((BaseFunction<T, R>) function);
    }

    private static class WhenCompleteBiConsumer<T> implements BiConsumer<T, Throwable> {
        private final KafkaFutureImpl<T> future;

        private final BiConsumer<? super T, ? super Throwable> biConsumer;

        WhenCompleteBiConsumer(KafkaFutureImpl<T> future, BiConsumer<? super T, ? super Throwable> biConsumer) {
            this.future = future;
            this.biConsumer = biConsumer;
        }

        public void accept(T val, Throwable exception) {
            try {
                if (exception != null) {
                    this.biConsumer.accept(null, exception);
                } else {
                    this.biConsumer.accept(val, null);
                }
            } catch (Throwable e) {
                if (exception == null)
                    exception = e;
            }
            if (exception != null) {
                this.future.completeExceptionally(exception);
            } else {
                this.future.complete(val);
            }
        }
    }

    public KafkaFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> biConsumer) {
        KafkaFutureImpl<T> future = new KafkaFutureImpl<>();
        addWaiter(new WhenCompleteBiConsumer<>(future, biConsumer));
        return future;
    }

    protected synchronized void addWaiter(BiConsumer<? super T, ? super Throwable> action) {
        if (this.exception != null) {
            action.accept(null, this.exception);
        } else if (this.done) {
            action.accept(this.value, null);
        } else {
            this.waiters.add(action);
        }
    }

    public synchronized boolean complete(T newValue) {
        List<BiConsumer<? super T, ? super Throwable>> oldWaiters;
        synchronized (this) {
            if (this.done)
                return false;
            this.value = newValue;
            this.done = true;
            oldWaiters = this.waiters;
            this.waiters = null;
        }
        for (BiConsumer<? super T, ? super Throwable> waiter : oldWaiters)
            waiter.accept(newValue, null);
        return true;
    }

    public boolean completeExceptionally(Throwable newException) {
        List<BiConsumer<? super T, ? super Throwable>> oldWaiters;
        synchronized (this) {
            if (this.done)
                return false;
            this.exception = newException;
            this.done = true;
            oldWaiters = this.waiters;
            this.waiters = null;
        }
        for (BiConsumer<? super T, ? super Throwable> waiter : oldWaiters)
            waiter.accept(null, newException);
        return true;
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        return completeExceptionally(new CancellationException()) || this.exception instanceof CancellationException;
    }

    public T get() throws InterruptedException, ExecutionException {
        SingleWaiter<T> waiter = new SingleWaiter<>();
        addWaiter(waiter);
        return waiter.await();
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        SingleWaiter<T> waiter = new SingleWaiter<>();
        addWaiter(waiter);
        return waiter.await(timeout, unit);
    }

    public synchronized T getNow(T valueIfAbsent) throws InterruptedException, ExecutionException {
        if (this.exception != null)
            wrapAndThrow(this.exception);
        if (this.done)
            return this.value;
        return valueIfAbsent;
    }

    public synchronized boolean isCancelled() {
        return this.exception instanceof CancellationException;
    }

    public synchronized boolean isCompletedExceptionally() {
        return this.exception != null;
    }

    public synchronized boolean isDone() {
        return this.done;
    }

    public String toString() {
        return String.format("KafkaFuture{value=%s,exception=%s,done=%b}", new Object[]{this.value, this.exception, Boolean.valueOf(this.done)});
    }
}
