package org.tensorflow.lite.examples.videoclassification.dataSource

import org.tensorflow.lite.examples.videoclassification.BuildConfig

enum class KeyBoolean(val key: String) {
    IS_LOGIN("${BuildConfig.APPLICATION_ID}.is_login"),
    IS_TIPS("${BuildConfig.APPLICATION_ID}.IS_TIPS"),
}

enum class KeyString(val key: String) {
    TOKEN("${BuildConfig.APPLICATION_ID}.token"),
    LANGUAGE("${BuildConfig.APPLICATION_ID}.language"),
    DeviceToken("${BuildConfig.APPLICATION_ID}.DeviceToken"),
    FIREBASE_ID("${BuildConfig.APPLICATION_ID}.firebase_id"),
}


