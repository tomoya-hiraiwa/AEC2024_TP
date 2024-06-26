package jp.com.aec2024_moduleb

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import jp.com.aec2024_moduleb.databinding.ActivityMainBinding
val gson = Gson()
class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.setDecorFitsSystemWindows(false)

        b.apply {
        }
    }
}