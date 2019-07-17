package mmstq.com.wut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_survey.*

class Survey : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_survey)
      supportActionBar!!.hide()

      val fragmentAdapter = MyPageAdapter(supportFragmentManager)
      viewpager_main.adapter = fragmentAdapter
      tabs_main.setupWithViewPager(viewpager_main)
   }
}
