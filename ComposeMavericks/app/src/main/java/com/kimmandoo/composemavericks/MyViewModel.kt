package com.kimmandoo.composemavericks

import com.airbnb.mvrx.MavericksViewModel

class MyViewModel(initialState: MyState) : MavericksViewModel<MyState>(initialState) {

    fun loadData() {
        setState { copy(isLoading = true) }

        // 예시 데이터 로드 (비동기 작업이 될 수도 있음)
        val newData = listOf("Item 1", "Item 2", "Item 3")

        // 데이터를 가져온 후 상태 업데이트
        setState {
            copy(isLoading = false, data = newData)
        }
    }

    fun showError(message: String) {
        setState {
            copy(isLoading = false, errorMessage = message)
        }
    }
}