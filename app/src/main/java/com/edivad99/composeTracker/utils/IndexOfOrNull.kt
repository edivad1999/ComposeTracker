package com.edivad99.composeTracker.utils

public fun <T> Iterable<T>.indexOfOrNull(element: T): Int? = indexOf(element).takeIf { it >= 0 }
