package de.ericmuench.appsfactorytesttask.util.errorhandling

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