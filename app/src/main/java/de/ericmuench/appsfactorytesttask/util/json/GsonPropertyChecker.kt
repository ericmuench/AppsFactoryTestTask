package de.ericmuench.appsfactorytesttask.util.json

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.lang.Exception
import kotlin.reflect.KClass

/**
 * This class is able to check, if all properties of json string are contained in a certain class.
 * For this, JSONObject and Reflection will be used.
 *
 * CAUTION: This class only checks the existence of the properties, but not their type
 *
 */
class GsonPropertyChecker {

    //functions
    /**
     * This function checks whether all properties of @see json are also available in an Instance
     * of @see clazz.
     * @param clazz The class to search in for properties
     * @param json Json-Object as a String to be checked if all its members are also available in an Instance
     * of clazz.
     *
     * CAUTION: This method only checks the existence of the properties, but not their type
     * @return Whether all properties of @see json are also available in @see clazz
     * */
    fun <T : Any> jsonObjectContainsAllPropertiesOf(clazz: KClass<T>, json: String) : Boolean{
        //Note: fields contains ONLY public properties
        val clazzPropertyNames = clazz.java.declaredFields.mapNotNull { it?.name }.also { println(it) }
        val gsonPropertyNames =
            clazz.java.declaredFields.mapNotNull { it?.getAnnotation(SerializedName::class.java)?.value }.also { println(it) }
        return try {
            val jsonObject = JSONObject(json)
            jsonObject
                .keys()
                .asSequence()
                .filterNotNull()
                .all {
                    clazzPropertyNames.contains(it) || gsonPropertyNames.contains(it)
                }
        }
        catch (ex : Exception){
            ex.printStackTrace()
            false
        }
    }
}