package org.tensorflow.lite.examples.videoclassification

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IOCoroutineContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainCoroutineContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultCoroutineContext