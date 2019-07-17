package mmstq.com.wut

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar

internal object SnackbarHelper {


   fun configSnackBar(context: Context, snackBar: Snackbar, run: Boolean) {
      addMargins(snackBar, run)
      setRoundBordersBg(context, snackBar, run)
      ViewCompat.setElevation(snackBar.view, 1f)
   }

   private fun setRoundBordersBg(context: Context, snackBar: Snackbar, run: Boolean) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         if (run) {
            snackBar.view.background = context.getDrawable(R.drawable.snackbar)

         } else {
            snackBar.view.background = context.getDrawable(R.drawable.widget_border)

         }
      }
   }

   private fun addMargins(snackBar: Snackbar, run: Boolean) {
      val params =
              snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
      if (run) params.setMargins(15, 10, 670, 15)
      else params.setMargins(15, 10, 15, 15)
      snackBar.view.layoutParams = params
   }

}
