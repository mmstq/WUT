package mmstq.com.wut

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {




   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      runBlock()


   }
   fun runBlock(){
      print("hellow")
      runBlocking {
         delay(2000L)
      }
      print("hiyh")

   }
}
