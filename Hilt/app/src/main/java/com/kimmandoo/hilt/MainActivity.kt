package com.kimmandoo.hilt

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kimmandoo.hilt.practice_binding.Bar
import com.kimmandoo.hilt.practice_binding.Foo
import com.kimmandoo.hilt.practice_binding.TestQualifier
import com.kimmandoo.hilt.practice_binds.Car
import com.kimmandoo.hilt.practice_binds.FooT
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import java.util.Optional
import javax.inject.Inject
import javax.inject.Provider

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var foo: Lazy<Foo> // Lazy
    @Inject
    lateinit var fooProvider: Provider<Foo>

    @Inject
    lateinit var bar: Bar

    lateinit var fooFun: Foo

    @Inject
    lateinit var test1: Test

//    @TestQualifier
//    @Inject
//    lateinit var test2: Test

    @Inject // method 주입
    fun provideFoo(foo: Foo){ // super.onCreate 이후에 Inject되어 fooFun은 초기화 된 상태로 접근가능하다.
        fooFun = foo
    }

//    @Inject // method 주입
//    fun injectTest(@TestQualifier test: Test){
//        testQualifier = test
//    }


    @Inject
    lateinit var car: Car // 이거만 하면 MissingBinding 오류 뜸 Car에서 Engine은 따로 바인딩 된게 없어서

    @Inject
    lateinit var foot: Optional<FooT>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ${car.engine}")
        
        assert(foo.get().bar != null)
        assert(fooProvider.get().bar != null)

        assert(this::foo.isInitialized)
        assert(this::bar.isInitialized)
        assert(this::fooFun.isInitialized)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}