package de.ericmuench.appsfactorytesttask.util.formatter

/**
 * This class escapes Parameter that need to be set into an URL and vice versa
 */
class UrlParameterFormatter {

    //fields
    private val escapeMapping = mapOf<String,String>(
        "&" to "%26",
        "\'" to "%27",
        " " to "%20",
    )

    //functions
    fun formatParamForUrl(param: String) : String {
        var paramModified = param
        escapeMapping.entries.forEach {
            paramModified = paramModified.replace(it.key,it.value)
        }
        return paramModified
    }

    fun unFormatParamForUrl(param: String) : String{
        var paramModified = param
        escapeMapping.entries.forEach {
            paramModified = paramModified.replace(it.value,it.key)
        }
        return paramModified
    }
}