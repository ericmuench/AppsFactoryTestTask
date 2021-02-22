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
        "\\" to "%2F",
        "`" to "%60",
        "+" to "%2B",
        "\$" to "%24",
        "#" to "%23",
        "@" to "%40",
        ";" to "%3B",
        ":" to "%3A",
        "\"" to "%22",
        "<" to "%3C",
        "=" to "%3D",
        ">" to "%3E",
        "?" to "%3F",
        "[" to "%5B",
        "]" to "%5D",
        "^" to "%5E",
        "{" to "%7B",
        "|" to "%7C",
        "}" to "%7D",
        "~" to "%7E",
        "“" to "%22",
        "‘" to "%27",
        "," to "%2C"

    )

    //functions
    fun formatParamForUrl(param: String) : String {
        //format % first due to the fact that this symbol has a specific role
        var paramModified = param.replace("%","%25")

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

        //unformat % at last due to the fact that this symbol has a specific role
        return paramModified.replace("%25","%")
    }
}