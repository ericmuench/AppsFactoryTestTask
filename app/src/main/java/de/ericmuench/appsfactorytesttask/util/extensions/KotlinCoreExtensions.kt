package de.ericmuench.appsfactorytesttask.util.extensions

import kotlinx.coroutines.coroutineScope

/**
 * This file defines some extensions for Kotlin
 *
 * This code was coped from my KotlinUtils-Project: https://github.com/ericmuench/KotlinUtils
 * */

//functional extensions for generic types
/**This extension can be used to execute certain actions on an object of type T? if it is not null**/
fun <T> T?.notNull(func : (T) -> Unit){
    if(this != null){
        func.invoke(this)
    }
}

/**This extension does the same as notNull-Extension but allows Coroutine-Calls**/
suspend fun <T> T?.notNullSuspending(func : suspend (T) -> Unit) = coroutineScope {
    if(this@notNullSuspending != null){
        func.invoke(this@notNullSuspending)
    }
}

/**This function does the oposite of notNull by executing some code it an object of Type T? is null**/
fun <T> T?.isNull(func : () -> Unit) {
    if (this == null){
        func.invoke()
    }
}

/**
 * This extension handles casting in a functional way. If an object has the given type then the given callback is
 * executed with it casted to the given type.
 * **/
fun <T : Any> Any?.castedAs(func : (T) -> Unit){
    val casted = this as? T
    casted.notNull {
        func.invoke(it)
    }
}

/**
 * This extension does the same as the other castedAs but additionally can return a Result or null if the
 * object that should be casted is not of type T.
 * **/
fun <T : Any,Res> Any?.castedAs(func : (T) -> Res) : Res?{
    val casted = this as? T

    return if(casted == null){
        null
    }
    else{
        func.invoke(casted)
    }

}

//String extensions
/**This extension takes a string, splits it by  spaces and capitalizes each Word of it**/
fun String.capitalizeEachWord() : String{
    return this.split(Regex(" ")).joinToString(" ") { it.capitalize() }
}

/**This extension does the same as capitalizeEachWord but can exclude certain Words**/
fun String.capitalizeEachWordExcept(vararg except: String) : String{
    return this.split(Regex(" ")).joinToString(" ") {
        if(!(except.contains(it))){
            it.capitalize()
        }
        else{
            it
        }
    }
}