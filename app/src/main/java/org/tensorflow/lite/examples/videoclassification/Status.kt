package org.tensorflow.lite.examples.videoclassification

sealed class Status<out T> {

    object Loading : Status<Nothing>()

    data class Success<T>(val data: T) : Status<T>()

    sealed class Error : Status<Nothing>() {
        data class Exceptions(val ex: Throwable?) : Error()
        data class Message(val message: String?) : Error()
    }

    fun loading() = this is Loading
    fun data(): T? = if (this is Success) this.data else null
    fun error(): Error? = if (this is Error) this else null

}