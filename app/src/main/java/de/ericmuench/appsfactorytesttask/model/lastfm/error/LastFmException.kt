package de.ericmuench.appsfactorytesttask.model.lastfm.error

class LastFmException(msg : String = "") : Exception(msg) {
    constructor(errorFromLastFm: ErrorFromLastFm) : this(errorFromLastFm.getLastFmErrorMessage())
}