package com.croquis.mystore.configuration

import com.croquis.mystore.util.base64Decoder
import com.croquis.mystore.util.toJsonObject
import io.opentracing.util.GlobalTracer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class RequestFilter : Filter {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest) {
            val wrapper = CustomServletRequestWrapper(request)
            addRequestTagInDatadog(wrapper)
            chain?.doFilter(wrapper, response)
        } else {
            chain?.doFilter(request, response)
        }
    }

    private fun addRequestTagInDatadog(wrapper: CustomServletRequestWrapper) {
        if (wrapper.headerNames.asSequence().any { it.lowercase() == "croquis-session-data" }) {
            setDatadogTag(
                "graphql.request.headers.croquis-session-data",
                toSession(wrapper.getHeader("croquis-session-data"))
            )
        }
        if (wrapper.rawBody.isNotBlank()) {
            try {
                val body = wrapper.rawBody.toJsonObject(Any::class.java)
                if (body is Map<*, *>) {
                    body["operationName"]?.apply {
                        setDatadogTag("graphql.request.operation-name", this.toString())
                    }
                    body["variables"]?.apply {
                        if (this.toString().length <= 1024 * 10) {
                            setDatadogTag("graphql.request.body.variables", this.toString())
                        } else {
                            setDatadogTag("graphql.request.body.variables", "too-large")
                        }
                    }
                    body["query"]?.apply {
                        setDatadogTag("graphql.request.body.query", this.toString())
                    }
                }
            } catch (e: Exception) {
                logger.warn("not-json-body ${wrapper.rawBody}")
            }
        }
    }

    private fun setDatadogTag(key: String, value: String) {
        GlobalTracer.get().activeSpan()?.setTag(key, value)
    }

    private fun toSession(rawSession: String): String {
        return try {
            String(base64Decoder.decode(rawSession.trim()))
        } catch (e: Exception) {
            rawSession
        }
    }
}
