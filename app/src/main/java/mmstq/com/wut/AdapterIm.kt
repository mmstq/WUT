package mmstq.com.wut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdapterIm(private val im: List<Im_Notice>) : RecyclerView.Adapter<AdapterIm.DataHolder>() {
   internal var view: View? = null

   override fun onBindViewHolder(holder: DataHolder, i: Int) {
      val model = im[i]
      holder.textTop.text = model.name
      holder.textMid.text = model.text
   }

   inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      var textTop: TextView = itemView.findViewById(R.id.upperText)
      var textMid: TextView = itemView.findViewById(R.id.lowertext)

   }

   override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DataHolder {
      val v = LayoutInflater.from(viewGroup.context)
              .inflate(R.layout.cv_msg_notice, viewGroup, false)
      return DataHolder(v)
   }

   override fun getItemCount(): Int {
      return im.size
   }
}

