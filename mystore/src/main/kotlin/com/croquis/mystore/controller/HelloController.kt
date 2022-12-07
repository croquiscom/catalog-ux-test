package com.croquis.mystore.controller

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping

class HelloController(
    private val env: Environment
) {

    @GetMapping("/hello")
    fun hello(): Map<String, Any> {
        return mapOf(
            "status" to "ok",
            "tag" to System.getProperty("tag", ""),
            "profile" to env.activeProfiles,
        )
    }
}
