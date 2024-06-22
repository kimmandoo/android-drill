package com.kimmandoo.hilt.practice_binding

import javax.inject.Inject

class Foo @Inject constructor(val bar: Bar) { // 생성자 주입
}