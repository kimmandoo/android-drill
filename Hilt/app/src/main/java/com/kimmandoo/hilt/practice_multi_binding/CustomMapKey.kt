package com.kimmandoo.hilt.practice_multi_binding

import dagger.MapKey



@MapKey
annotation class CustomMapKey(val value: Keys)

enum class Keys {
    ONE,
    TWO,
    THREE
}
