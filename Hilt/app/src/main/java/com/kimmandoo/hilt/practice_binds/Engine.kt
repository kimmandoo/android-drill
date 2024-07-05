package com.kimmandoo.hilt.practice_binds

import javax.inject.Inject

interface Engine {
}

class GasolineEngine @Inject constructor(): Engine // 생성자 바인딩까지
class DieselEngine @Inject constructor(): Engine