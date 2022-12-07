package com.croquis.mystore.configuration

import com.croquis.mystore.util.toJsonObject
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class CustomServletRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    val rawBody: String = request.inputStream?.let { inputStream -> String(inputStream.readAllBytes()) } ?: ""

    init {
        if (rawBody.isNotEmpty()) {
            try {
                val body = rawBody.toJsonObject(Any::class.java)
                if (body is Map<*, *>) {
                    body["operationName"]?.apply { request.setAttribute("operationName", this) }
                    body["variables"]?.apply { request.setAttribute("variables", this.toString()) }
                    body["query"]?.apply { request.setAttribute("query", this.toString()) }
                    request.setAttribute("raw", rawBody)
                }
            } catch (e: Exception) {
                logger.warn("not-json-body $rawBody")
            }
        }
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(rawBody.toByteArray())
        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {}

            override fun read(): Int {
                return byteArrayInputStream.read()
            }
        }
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(this.inputStream))
    }
}
