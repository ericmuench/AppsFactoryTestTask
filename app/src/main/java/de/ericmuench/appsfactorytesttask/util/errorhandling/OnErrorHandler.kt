package de.ericmuench.appsfactorytesttask.util.errorhandling

/**
 * This function defines a Handler for a throwable Error. Additionally for the functionality of
 * executing the "On Error"-Action it provides features like counting how often the "On Error"-Action
 * was executed or if it is currently executing.
 * */
class OnErrorHandler(private val handler : (Throwable) -> Unit) {
    var isExecuting : Boolean = false
        private set
    var executingCount : Int = 0
        private set

    val wasAlreadyExecuted : Boolean
    get() = executingCount > 0

    operator fun invoke(throwable: Throwable){
        isExecuting = true
        handler.invoke(throwable)
        executingCount++
        isExecuting = false
    }

    fun resetExecutionCount(){
        executingCount = 0
    }
}