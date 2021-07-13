package dev.pratyush.gfsidchecker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.pratyush.gfsidchecker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var views : ActivityMainBinding
    private lateinit var clipboardManager : ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        views = ActivityMainBinding.inflate(layoutInflater);
        setContentView(views.root)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        views.gsfID.setOnClickListener {
           copyToClipBoard()
        }
        views.copy.setOnClickListener {
            copyToClipBoard()
        }
    }

    private fun copyToClipBoard(){
        clipboardManager.setPrimaryClip(ClipData.newPlainText("GSF ID",gsfId()))
        Toast.makeText(this,"Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        views.gsfID.text = gsfId()
    }

    private val sUri = Uri.parse("content://com.google.android.gsf.gservices")

    private fun gsfId(): String? {
        return try {
            val query: Cursor =
                contentResolver?.query(sUri, null, null, arrayOf("android_id"), null)
                    ?: return "Not found"
            if (!query.moveToFirst() || query.columnCount < 2) {
                query.close()
                return "Not found"
            }
            val toHexString = java.lang.Long.toHexString(query.getString(1).toLong())
            query.close()
            "GSF ID : " + toHexString.uppercase().trim { it <= ' ' }
        } catch (e: SecurityException) {
            e.printStackTrace()
            e.localizedMessage
        } catch (e2: Exception) {
            e2.printStackTrace()
            e2.localizedMessage
        }
    }
}