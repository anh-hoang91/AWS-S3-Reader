package com.anhhoang.aws.core.coroutines.executors

import javax.inject.Qualifier

/** Dispatcher's qualifier for the main context. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainContext

/**
 * Dispatcher's qualifier for the IO context. To be used for IO-bound tasks that are not
 * computationally expensive.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BlockingContext

/**
 * Dispatcher's qualifier for the default context. To be used for computational tasks that won't
 * make the IO operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LightweightContext