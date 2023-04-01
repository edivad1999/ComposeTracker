package com.edivad_99.compose_tracker.utils

public fun <T> Iterable<T>.indexOfOrNull(element: T): Int? = indexOf(element).takeIf { it >= 0 }
