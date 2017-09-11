package com.bortolan.iquadriv2.Activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bortolan.iquadriv2.R
import kotlinx.android.synthetic.main.activity_circolari.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ActivityCircolari : AppCompatActivity() {

    var lastOffset = 0f

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        pdfView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        if (!(supportActionBar?.isShowing ?: true))
            supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_circolari)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        DownloadLink(applicationContext) {
            res: String ->
            DownloadDocument(applicationContext).execute(res)
        }.execute(intent.getStringExtra("url"))

        pdfView.setOnClickListener { toggle() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        if (supportActionBar?.isShowing ?: true)
            supportActionBar?.hide()
        mVisible = false

        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        pdfView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

    inner class DownloadLink(val context: Context, val ex: (res: String) -> Unit) : AsyncTask<String, Void, String?>() {
        var pat = Pattern.compile("(<p class=\"gde-text\"><a href=\")(.+)(\" class=\"gde-link\">Download)")

        override fun doInBackground(vararg urls: String?): String? {
            var result: String?
            try {
                val u = URL(urls[0])
                val conn = u.openConnection() as HttpURLConnection
                conn.readTimeout = 70000 //milliseconds
                conn.connectTimeout = 7000 // milliseconds
                conn.requestMethod = "GET"

                conn.connect()

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {

                    val reader = BufferedReader(InputStreamReader(
                            conn.inputStream, "iso-8859-1"), 8)
                    val sb = StringBuilder()
                    var line: String?
                    while (true) {
                        line = reader.readLine()
                        if (line == null) break

                        sb.append(line + "\n")

                    }
                    result = sb.toString()

                    val matcher = pat.matcher(result)

                    if (matcher.find()) {
                        return matcher.group(2)
                    }

                } else {
                    return null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "error"
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            ex.invoke(result.orEmpty())
        }

    }

    inner class DownloadDocument(val context: Context) : AsyncTask<String, Int, ByteArray?>() {
        override fun doInBackground(vararg urls: String?): ByteArray? {
            var input: InputStream? = null
            var connection: HttpURLConnection? = null
            var output: ByteArray? = null
            try {
                val url = URL(urls[0])
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.connectTimeout = 7000
                connection.connect()

                // download the file
                input = connection.inputStream
                output = ByteArray(connection.contentLength)
                var i = 0
                for (myByte in input.buffered().iterator()) {
                    output[i] = myByte
                    i++
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Non è possibile scaricare la circolare", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                finish()
            } finally {
                input?.close()
                connection?.disconnect()
                return output
            }
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            pdfView.fromBytes(result)
                    .enableSwipe(true)
                    .spacing(3)
                    .onPageScroll({
                        _, positionOffset ->
                        if (lastOffset < positionOffset) hide()
                        else if (lastOffset > positionOffset) {
                            show()
                        }
                        lastOffset = positionOffset
                    })
                    .enableAntialiasing(true)
                    .enableDoubletap(true).load()
        }
    }
}
