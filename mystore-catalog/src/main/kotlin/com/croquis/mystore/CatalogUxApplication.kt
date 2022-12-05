package com.croquis.mystore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CatalogUxApplication

fun main(args: Array<String>) {
    runApplication<CatalogUxApplication>(*args)
}
