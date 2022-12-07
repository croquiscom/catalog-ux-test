package com.croquis.mystore.log

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import org.springframework.util.PatternMatchUtils

class AccessLogFilter : Filter<IAccessEvent>() {
    private val excludeUrls = arrayOf("/hello", "/favicon.ico", "/graphql-schema")

    override fun decide(event: IAccessEvent): FilterReply {
        val isIntrospectionQuery = event.getAttribute("operationName") == "IntrospectionQuery"
        return if (PatternMatchUtils.simpleMatch(excludeUrls, event.requestURI) || isIntrospectionQuery) {
            FilterReply.DENY
        } else {
            FilterReply.ACCEPT
        }
    }
}
