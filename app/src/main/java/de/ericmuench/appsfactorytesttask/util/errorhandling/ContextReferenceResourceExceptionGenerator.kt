package de.ericmuench.appsfactorytesttask.util.errorhandling

import android.content.Context
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * This class defines a concrete implementation of a ResourceThrowableGenerator that uses a
 * WeakReference-Object of type Context to return an Exception.
 * */
class ContextReferenceResourceExceptionGenerator(
    context: Context
): ResourceThrowableGenerator {

    //region Fields
    private val contextRef = WeakReference<Context>(context)
    //endregion

    //region Functions from Interfaces
    override fun createThrowable(resMsgId: Int): Throwable {
        val msgString = contextRef
            .get()
            ?.getString(resMsgId)
            ?: "unknown error"

        return Exception(msgString)
    }
    //endregion
}