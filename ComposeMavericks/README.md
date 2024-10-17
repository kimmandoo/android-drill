# MVI 입문하기

1. MVI 패턴의 구성 요소

MVI는 Model, View, Intent로 구성된 패턴이야. 각각의 역할을 통해 상태 기반 UI 업데이트와 사용자 이벤트를 처리하는 방식이야. 이 구조는 다음과 같이 나뉘어져 있어:

	•	Model: 애플리케이션의 상태를 나타냄 (예: 데이터를 로드 중인지, 에러가 발생했는지, 또는 데이터를 가지고 있는지).
	•	View: 상태를 기반으로 UI를 그리는 부분.
	•	Intent: 사용자의 상호작용, 이벤트를 ViewModel에 전달하여 상태를 변경하는 역할.

2. MVI 적용 방식 설명

이제 코드를 보면서 MVI 패턴이 어떻게 적용되었는지 구체적으로 설명해 줄게.

1) Model (상태)

Model은 애플리케이션의 상태를 정의하고 유지하는 객체야. 이 코드에서는 MyState가 Model의 역할을 수행해. 상태는 애플리케이션이 현재 어떤 상태에 있는지 설명하는 데이터 집합을 포함하고 있어.

```kotlin
   data class MyState(
      val isLoading: Boolean = false,
      val data: List<String> = emptyList(),
      val errorMessage: String? = null
   ) : MavericksState
```

	•	isLoading: 데이터를 로드 중인 상태를 나타냄.
	•	data: 로드된 데이터를 저장하는 리스트.
	•	errorMessage: 에러가 발생했을 때 에러 메시지를 저장.

Model은 불변 객체이며, 변경할 때마다 새로운 상태를 복사해 반환하도록 되어 있어. 이는 상태가 변경되면 UI가 다시 렌더링되는 근거가 돼.

2) View (화면 UI)

View는 사용자가 실제로 보는 UI를 정의하는 부분이야. Jetpack Compose에서는 컴포저블이 그 역할을 하지.

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = mavericksViewModel()) {
// 상태를 구독
val state by viewModel.collectAsState()

    // 로딩 중일 때
    if (state.isLoading) {
        CircularProgressIndicator()
    }
    // 에러가 발생했을 때
    else if (state.errorMessage != null) {
        Text("Error: ${state.errorMessage}")
    }
    // 데이터가 로드되었을 때
    else {
        LazyColumn {
            items(state.data) { item ->
                Text(item)
            }
        }
    }
}
```

	•	MyScreen은 상태에 따라 UI를 다르게 표시해. 로딩 중일 때는 로딩 스피너를 보여주고, 에러가 발생하면 에러 메시지를 표시하며, 데이터를 성공적으로 로드하면 리스트를 그려.
	•	collectAsState()를 사용하여 ViewModel에서 관리하는 상태를 구독해. 상태가 변경될 때마다 UI가 자동으로 다시 렌더링되므로, 상태 기반의 UI를 쉽게 구현할 수 있어.

3) Intent (사용자 이벤트)

Intent는 사용자 상호작용이나 이벤트를 ViewModel에 전달하여 상태를 변경하는 역할을 해. 이 코드에서 loadData()가 일종의 Intent 역할을 수행해.
```kotlin 
LaunchedEffect(Unit) {
viewModel.loadData()
}

```
	•	LaunchedEffect 내에서 viewModel.loadData()를 호출하여 데이터를 로드하도록 요청해. loadData()는 사용자가 페이지를 열거나 특정 시점에 호출될 수 있는 Intent와 같아. 이 함수는 상태를 변경시켜 UI에 반영되도록 함.
	•	Intent는 상태를 변경하는 이벤트이고, 여기서는 데이터를 로드하는 Intent를 처리하고 있어.

4) ViewModel (Intent와 Model을 연결)

ViewModel은 사용자 이벤트(Intent)를 처리하고, 상태(Model)를 관리하며 View에 전달하는 역할을 해. MavericksViewModel을 사용해 상태를 관리하고, setState()로 상태를 변경하는 방식이 MVI의 핵심이야.

```kotlin
class MyViewModel(initialState: MyState) : MavericksViewModel<MyState>(initialState) {

    // 데이터를 로드하는 메서드
    fun loadData() {
        setState { copy(isLoading = true) }
        
        // 예시 데이터를 로드했다고 가정
        val newData = listOf("Item 1", "Item 2", "Item 3")
        
        setState {
            copy(isLoading = false, data = newData)
        }
    }

    // 에러를 처리하는 메서드
    fun showError() {
        setState {
            copy(isLoading = false, errorMessage = "Something went wrong")
        }
    }
}
```

	•	loadData()는 상태를 변경하는 Intent 역할을 하며, 상태가 업데이트되면 View는 이를 감지하고 UI를 다시 렌더링해.
	•	setState { copy(...) }는 현재 상태를 복사하여 새로운 상태를 생성하고, 이를 Mavericks가 관리하여 자동으로 상태 변화를 UI에 반영하게 해.

3. 코드의 흐름

    1.	View에서 초기 Intent 발생: LaunchedEffect가 초기 렌더링 시 loadData()를 호출하여 데이터를 로드하도록 요청해.
    2.	Intent를 처리: loadData()는 데이터를 로드한 후 상태를 setState를 통해 변경함.
    3.	Model 변경: 상태(Model)가 변경되고, 이를 구독하고 있는 View는 UI를 업데이트해.
    4.	View는 상태 기반으로 렌더링: 상태에 따라 로딩, 에러, 또는 데이터 리스트를 보여줌.

4. 왜 이 코드가 MVI인가?

   •	Model: MyState는 앱의 상태를 정의하는 모델로, 상태에 대한 모든 정보를 가지고 있음.
   •	View: MyScreen 컴포저블은 Model에 의해 결정된 상태를 렌더링하는 UI임. 사용자가 보게 되는 화면이자, 상태 변화에 따라 다른 UI를 보여주는 역할을 함.
   •	Intent: LaunchedEffect 내에서 데이터를 로드하거나, 사용자의 상호작용에 따라 loadData()와 같은 Intent를 전달해 상태를 변경함.

이처럼 MVI 패턴에서는 상태 변화가 View에 반영되고, Intent에 따라 상태가 업데이트되는 방식으로 UI와 로직을 분리하면서도 일관성 있게 동작하게 만들어 줘.

# 그럼 왜 Mavericks인가??

Mavericks를 사용함으로써 Jetpack Compose에서 MVI 패턴을 적용할 때 편해진 부분을 몇 가지 설명해줄게.

1. 상태 관리의 단순화

Mavericks는 상태 관리와 관련된 복잡한 작업을 간소화해. 일반적으로 상태를 관리할 때는 직접적으로 StateFlow나 LiveData 같은 도구를 사용하여 상태를 업데이트하고 UI를 다시 렌더링하는 로직을 작성해야 하지만, Mavericks는 이 과정을 상당히 단순화해줘.

Mavericks 사용 전:

	•	MutableStateFlow를 직접 관리해야 하고, 상태가 변경될 때마다 상태 흐름을 구독하는 로직을 만들어야 해.
	•	상태 복사와 변경을 수동으로 처리해야 하며, 이를 위해 상태가 변경될 때마다 UI를 수동으로 갱신해야 함.

Mavericks 사용 후:

	•	**setState { copy(...) }**를 사용해 간단하게 상태를 복사하고 업데이트할 수 있어.
	•	Mavericks는 자동으로 상태 변화를 구독하고 관리해 주기 때문에, UI에서 상태 변화를 신경 쓰지 않아도 됨.

```kotlin
setState { copy(isLoading = true) }  // 간단하게 상태를 변경하고 Mavericks가 자동으로 처리
```

2. 자동 상태 구독 및 ViewModel 생명 주기 관리

Mavericks는 MavericksViewModel을 통해 상태를 관리하고, collectAsState()로 View에서 간단하게 상태를 구독할 수 있어. 이를 통해 수동으로 상태를 구독하고 생명주기를 관리할 필요가 없어졌어.

Mavericks 사용 전:

	•	상태 흐름을 수동으로 구독하거나, ViewModel과 생명 주기를 관리해야 함.
	•	StateFlow나 LiveData를 직접 사용하면서, 상태 관리와 구독을 수동으로 처리해야 함.

Mavericks 사용 후:

	•	collectAsState()를 통해 상태를 간편하게 구독하고, Mavericks가 상태의 생명 주기를 관리해 줘서 이를 신경 쓸 필요가 없어.

```kotlin
val state by viewModel.collectAsState()  // 상태 변화를 자동으로 구독하고 UI 업데이트
```

3. 명확한 상태 모델링

Mavericks는 상태를 명확하게 모델링할 수 있도록 도와줘. 모든 상태는 MavericksState 인터페이스를 구현하여 불변 객체로 관리되며, copy()를 통해 상태를 변경할 때마다 새로운 상태를 반환하도록 권장해. 이렇게 하면 상태를 추적하고 디버깅하는 것이 더 쉬워져.

Mavericks 사용 전:

	•	상태를 직접 관리할 때 실수로 상태가 변경되는 등의 사이드 이펙트를 방지하기가 어려움.
	•	상태가 명확하게 정의되지 않으면 상태 변경이 복잡해질 수 있음.

Mavericks 사용 후:

	•	MavericksState를 통해 모든 상태가 명확하게 정의되고, 불변 객체로 관리됨. 상태를 변경할 때마다 새로운 객체를 생성하는 방식으로 유지됨.

```kotlin
data class MyState(
   val isLoading: Boolean = false, 
   val data: List<String> = emptyList()
) : MavericksState

```

4. 의존성 주입(Hilt와의 통합)

Mavericks는 Hilt와 통합이 잘 되어 있어서 ViewModel에 대한 의존성 주입을 쉽게 처리할 수 있어. Mavericks는 Hilt를 사용해 의존성 주입을 자동으로 처리해 주므로, ViewModel을 생성할 때의 복잡한 설정을 줄일 수 있어.

Mavericks 사용 전:

	•	ViewModel에 의존성을 주입하려면 복잡한 설정이 필요했을 수 있음.

Mavericks 사용 후:

	•	Hilt와의 통합 덕분에 ViewModel 생성 및 의존성 주입이 매우 간단해짐.
```kotlin
@HiltViewModel
   class MyViewModel @Inject constructor(
   initialState: MyState
) : MavericksViewModel<MyState>(initialState)
```

5. 상태 변환 및 액션의 구조화

Mavericks는 상태를 간편하게 변환하고 액션을 구조화할 수 있게 도와줘. MavericksViewModel 내에서 상태 변경이 일어날 때마다 상태를 불변으로 유지하면서 관리할 수 있어, 이로 인해 로직이 명확해지고 실수를 줄일 수 있어.

요약

Mavericks를 사용하면 상태 관리가 단순해지고, 다음과 같은 장점들이 생겨:

	•	상태의 자동 구독 및 생명 주기 관리.
	•	상태 복사를 통한 불변성 유지로 디버깅이 쉬워짐.
	•	setState를 사용해 상태를 간단하게 업데이트할 수 있음.
	•	Hilt와의 통합으로 의존성 주입이 간단해짐.

따라서, Mavericks는 MVI 패턴을 Jetpack Compose에서 훨씬 더 편리하게 구현하고 유지 보수할 수 있도록 도와주는 라이브러리야.
