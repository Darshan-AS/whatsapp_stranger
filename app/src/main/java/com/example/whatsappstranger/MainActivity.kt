package com.example.whatsappstranger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val tag = MainActivity::class.qualifiedName

    object WhatsappApiInfo {
        const val SCHEME = "https"
        const val BASE_URL = "api.whatsapp.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mobileNumberEditText.requestFocus()
        openWhatsappButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.openWhatsappButton -> {
                val countryCode: String
                val mobileNumber: String
                try {
                    countryCode = "91"
                    mobileNumber = getMobileNumber()
                } catch (e: IllegalStateException) {
                    Snackbar.make(view, "Invalid Mobile number!", Snackbar.LENGTH_SHORT).show()
                    return
                }

                val url: String = buildUrl(countryCode, mobileNumber)
                launchWhatsapp(url, this)
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun getMobileNumber(): String {
        val mobileNumber: String = mobileNumberEditText.text.toString()
        Log.i(tag, "Mobile number entered: $mobileNumber")

        if (!Pattern.compile("[0-9]{10}").matcher(mobileNumber).matches())
            throw IllegalStateException("Invalid mobile number. Value = $mobileNumber")

        return mobileNumber
    }

    private fun buildUrl(countryCode: String, mobileNumber: String): String {
        val url: String = Uri.Builder()
            .scheme(WhatsappApiInfo.SCHEME)
            .authority(WhatsappApiInfo.BASE_URL)
            .appendPath("send")
            .appendQueryParameter("phone", countryCode + mobileNumber)
            .build()
            .toString()
        Log.i(tag, "URL: $url")
        return url
    }

    private fun launchWhatsapp(url: String, context: Context) {
        val webUri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webUri)

        if (intent.resolveActivity(packageManager) != null) {
            Log.i(tag, "Launching Whatsapp")
            context.startActivity(intent)
        }
    }
}

