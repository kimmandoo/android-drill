package com.kimmandoo.hilt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kimmandoo.hilt.practice_binding.Bar
import com.kimmandoo.hilt.practice_binding.Foo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var foo: Foo // field 주입
    @Inject
    lateinit var bar: Bar

    lateinit var fooFun: Foo

    @Inject // method 주입
    fun provideFoo(foo: Foo){ // super.onCreate 이후에 Inject되어 fooFun은 초기화 된 상태로 접근가능하다.
        fooFun = foo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assert(this::foo.isInitialized)
        assert(this::bar.isInitialized)
        assert(this::fooFun.isInitialized)
        assert(foo.bar != null)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}