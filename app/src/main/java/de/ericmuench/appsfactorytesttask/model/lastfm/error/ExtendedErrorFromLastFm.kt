package de.ericmuench.appsfactorytesttask.model.lastfm.error


import com.google.gson.annotations.SerializedName

data class ExtendedErrorFromLastFm(
    @SerializedName("error")
    val error: Int,
    @SerializedName("links")
    val links: List<Any>,
    @SerializedName("message")
    val message: String
): ErrorFromLastFm{
    override fun getLastFmErrorMessage(): String = "$error: $message"
}