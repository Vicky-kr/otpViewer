package com.example.sms.adapters

import android.content.Context
import android.database.Cursor
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.example.sms.R
import com.example.sms.adapters.SingleGroupAdapter.MyViewHolder
import com.example.sms.utils.ColorGeneratorModified
import com.example.sms.utils.Helpers
import java.util.regex.Matcher
import java.util.regex.Pattern

class SingleGroupAdapter(
    private val context: Context,
    private var dataCursor: Cursor?,
    private var color: Int,
    private val savedContactName: String?
) : RecyclerView.Adapter<MyViewHolder>() {

    private lateinit var generator: ColorGeneratorModified

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.single_sms_detailed, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        dataCursor!!.moveToPosition(position)


        /**
         * This the the message body
         */

//        holder.message.text = dataCursor!!.getString(dataCursor!!.getColumnIndexOrThrow("body"))


        var MSG: String = dataCursor!!.getString(dataCursor!!.getColumnIndexOrThrow("body"))
        val OTP = getOtpFromMessage(MSG)
        MSG = MSG.replace(OTP, "<b> $OTP </b>")
//        Log.e("MESSAGE", MSG)
        holder.message.text = HtmlCompat.fromHtml(MSG, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val time = dataCursor!!.getLong(dataCursor!!.getColumnIndexOrThrow("date"))
        holder.time.text = Helpers.getDate(time)
        val name = dataCursor!!.getString(dataCursor!!.getColumnIndexOrThrow("address"))
        val firstChar = savedContactName?.get(0).toString() ?: name[0].toString()

        if (color == 0)
            color = generator.getColor(name)

        val drawable = TextDrawable.builder().buildRound(firstChar, color)
        holder.image.setImageDrawable(drawable)
    }

    fun swapCursor(cursor: Cursor?) {
        if (dataCursor === cursor) {
            return
        }
        dataCursor = cursor
        if (cursor != null) {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return if (dataCursor == null) 0 else dataCursor!!.count
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val image: ImageView = itemView.findViewById(R.id.smsImage)
        val time: TextView = itemView.findViewById(R.id.time)
    }

    init {
        if (color == 0) generator = ColorGeneratorModified.MATERIAL!!
    }

    /**
     * Algorithm to get OTP from a MESSAGE
     */

    private fun getOtpFromMessage(message: String): String {
        // This will match any 6 digit number in the message
        val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            return matcher.group(0)
        }
        return ""
    }
}