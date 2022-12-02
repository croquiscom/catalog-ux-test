package com.croquis.mystore.log

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.contrib.json.JsonLayoutBase
import com.croquis.mystore.util.base64Decoder

class AccessLogJsonLayout : JsonLayoutBase<IAccessEvent>() {

    override fun doLayout(event: IAccessEvent?): String {
        return "[ZzL]" + super.doLayout(event)
    }

    override fun toJsonMap(iAccessEvent: IAccessEvent): Map<String, Any?> {
        return mapOf(
            "service_name" to System.getProperty("dd.service.name"),
            "raw" to iAccessEvent.request.getAttribute("raw"),
            "@timestamp" to formatTimestamp(iAccessEvent.timeStamp),
            "@e_idx" to getIndex(),
            "logType" to "access",
            "tag" to System.getProperty("tag", "N/A"),
            "profile" to getSpringActiveProfile(),
            "method" to iAccessEvent.method,
            "uri" to iAccessEvent.requestURI,
            "status" to iAccessEvent.response.status,
            "operationName" to iAccessEvent.request.getAttribute("operationName"),
            "variableString" to iAccessEvent.request.getAttribute("variables"),
            "query" to iAccessEvent.request.getAttribute("query"),
            "queryString" to iAccessEvent.queryString,
            "contentLength" to iAccessEvent.contentLength,
            "referer" to extractHeader(iAccessEvent, "referer"),
            "userAgent" to extractHeader(iAccessEvent, "user-agent"),
            "headers" to iAccessEvent.requestHeaderMap,
            "croquis-session-data" to toSession(iAccessEvent),
            "elapsedTime" to iAccessEvent.elapsedTime
        ).filter { it.value != null }
    }

    private fun toSession(iAccessEvent: IAccessEvent): String? {
        val rawSession = extractHeader(iAccessEvent, "croquis-session-data")
        return try {
            if (rawSession.isNullOrBlank()) {
                rawSession
            } else {
                String(base64Decoder.decode(rawSession.trim()))
            }
        } catch (e: Exception) {
            rawSession
        }
    }

    private fun getIndex(): String {
        return "service-catalog"
    }

    private fun extractHeader(iAccessEvent: IAccessEvent, key: String): String? {
        return iAccessEvent.getRequestHeader(key)
    }

    private fun getSpringActiveProfile(): String {
        return System.getProperty("spring.profiles.active")
    }
}
