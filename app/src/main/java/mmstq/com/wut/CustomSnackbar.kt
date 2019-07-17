package mmstq.com.wut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat

import com.google.android.material.snackbar.BaseTransientBottomBar


class CustomSnackbar
/**
 * Constructor for the transient bottom bar.
 *
 * @param parent The parent for this transient bottom bar.
 * @param content The content view for this transient bottom bar.
 * @param callback The content view callback for this transient bottom bar.
 */
private constructor(parent: ViewGroup, content: View, callback: ContentViewCallback) : BaseTransientBottomBar<CustomSnackbar>(parent, content, callback) {

   fun setText(text: CharSequence): CustomSnackbar {
      val textView = getView().findViewById<View>(R.id.snackbar_text) as TextView
      textView.text = text
      return this
   }

   fun setAction(text: CharSequence, listener: View.OnClickListener): CustomSnackbar {
      val actionView = getView().findViewById<View>(R.id.snackbar_action) as Button
      actionView.text = text
      actionView.visibility = View.VISIBLE
      actionView.setOnClickListener { view ->
         listener.onClick(view)
         // Now dismiss the Snackbar
         dismiss()
      }
      return this
   }

   private class ContentViewCallback(private val content: View) : BaseTransientBottomBar.ContentViewCallback {

      override fun animateContentIn(delay: Int, duration: Int) {
         ViewCompat.setScaleY(content, 1f)
         ViewCompat.animate(content).scaleY(1f).setDuration(duration.toLong()).startDelay = delay.toLong()
      }

      override fun animateContentOut(delay: Int, duration: Int) {
         ViewCompat.setScaleY(content, 1f)
         ViewCompat.animate(content).scaleY(1f).setDuration(duration.toLong()).startDelay = delay.toLong()
      }
   }

   companion object {

      fun make(parent: ViewGroup, @BaseTransientBottomBar.Duration duration: Int): CustomSnackbar {
         val inflater = LayoutInflater.from(parent.context)
         val content = inflater.inflate(R.layout.snackbar_layout, parent, false)
         val viewCallback = ContentViewCallback(content)
         val customSnackbar = CustomSnackbar(parent, content, viewCallback)

         customSnackbar.getView().setPadding(0, 0, 0, 0)
         customSnackbar.duration = duration
         return customSnackbar
      }
   }
}