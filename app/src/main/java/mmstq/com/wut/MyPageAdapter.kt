package mmstq.com.wut

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
   override fun getItem(position: Int): Fragment {
      return when (position) {
         0 -> {
            CSurvey()
         }
         else -> {
            GoSurvey()
         }
      }
   }

   override fun getCount(): Int {
      return 2
   }

   override fun getPageTitle(position: Int): CharSequence? {
      return when (position) {
         0 -> {
            "Submit"
         }
         else -> {
            "Vote"
         }
      }
   }
}