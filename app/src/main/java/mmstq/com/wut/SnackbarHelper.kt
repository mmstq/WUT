package mmstq.com.wut

import android.content.Context
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.view.ViewGroup

internal object SnackbarHelper {


   fun configSnackbar(context: Context, snackbar: Snackbar, run: Boolean) {
      addMargins(snackbar, run)
      setRoundBordersBg(context, snackbar, run)
      ViewCompat.setElevation(snackbar.view, 1f)
   }

   private fun setRoundBordersBg(context: Context, snackbar: Snackbar, run: Boolean) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         if (run) {
            snackbar.view.background = context.getDrawable(R.drawable.snackbar)

         } else {
            snackbar.view.background = context.getDrawable(R.drawable.widget_border)

         }
      }
   }

   private fun addMargins(snackbar: Snackbar, run: Boolean) {
      val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
      if (run) params.setMargins(15, 10, 670, 15)
      else params.setMargins(15, 10, 15, 15)
      snackbar.view.layoutParams = params
   }

}
