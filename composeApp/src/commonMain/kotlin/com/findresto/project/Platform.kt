package com.findresto.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform