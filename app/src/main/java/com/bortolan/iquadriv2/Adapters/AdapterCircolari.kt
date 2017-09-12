package com.bortolan.iquadriv2.Adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bortolan.iquadriv2.Activities.ActivityArticle
import com.bortolan.iquadriv2.Interfaces.Circolare
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Pattern


class AdapterCircolari(private val activity: Activity, private val mode: Int) : RecyclerView.Adapter<AdapterCircolari.CircolariHolder>() {
    private val data: MutableList<Circolare>
    var loading = false
    var snackBar: Snackbar = Snackbar.make(activity.findViewById<CoordinatorLayout>(R.id.activity_main), "Download in corso...", Snackbar.LENGTH_INDEFINITE)

    init {
        data = ArrayList<Circolare>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircolariHolder {
        return CircolariHolder(LayoutInflater.from(activity).inflate(R.layout.adapter_circolari, parent, false))
    }

    override fun onBindViewHolder(holder: CircolariHolder, position: Int) {
        val row: Circolare = data[position]

        holder.title.text = row.title.trim { it <= ' ' }
        holder.description.text = row.content.trim { it <= ' ' }

        holder.surface.setOnClickListener {
            if (!loading) {
                if (mode == MODE_QDS)
                    activity.startActivity(Intent(activity, ActivityArticle::class.java).putExtra("url", row.link).putExtra("title", row.title))
                else {
                    downloadCircolare(row.link)
                }
            }
        }

    }

    private fun downloadCircolare(url: String) {
        loading = true
        snackBar.view.minimumHeight = Methods.dpToPx(48 + 56f).toInt()
        snackBar.show()
        DownloadLink {
            res: String ->
            DownloadDocument {
                snackBar.dismiss()
                loading = false
            }.execute(res)
        }.execute(url)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: List<Circolare>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    inner class CircolariHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById<TextView>(R.id.title)
        val description: TextView = itemView.findViewById<TextView>(R.id.description)
        val surface: View = itemView.findViewById<View>(R.id.surface)
    }

    companion object {
        val MODE_QDS = 1
        val MODE_CIRCOLARE = 0
    }

    inner class DownloadLink(val ex: (res: String) -> Unit) : AsyncTask<String, Void, String?>() {
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

    inner class DownloadDocument(val ex: () -> Unit) : AsyncTask<String, Int, Unit>() {
        var file = File(activity.externalCacheDir.absolutePath + "/iQuadri/Circolari/", "circolare.pdf")
        lateinit var outputFile: FileOutputStream

        override fun doInBackground(vararg urls: String?) {
            var input: InputStream? = null
            var connection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                connection = url.openConnection() as HttpURLConnection
                with(connection) {
                    doOutput = true
                    connectTimeout = 7000
                    connect()
                }


                if (connection.responseCode != HttpURLConnection.HTTP_OK) return

                input = connection.inputStream
                if (file.exists()) file.delete()
                if (!file.parentFile.isDirectory) file.parentFile.mkdirs()
                Log.d("DOWNLOAD", "FILE " + if (file.createNewFile()) "CREATED" else "NOT CREATED")
                outputFile = FileOutputStream(file)

                var temp: Int
                val tempBytes = ByteArray(2048)
                while (true) {
                    temp = input.read(tempBytes)
                    if (temp == -1) break

                    outputFile.write(tempBytes, 0, temp)
                }

                outputFile.flush()
                outputFile.close()
            } catch (e: Exception) {
                with(activity) {
                    runOnUiThread {
                        Toast.makeText(this, "Non è possibile scaricare la circolare", Toast.LENGTH_SHORT).show()
                    }
                }
                e.printStackTrace()
                cancel(true)
            } finally {
                input?.close()
                connection?.disconnect()
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            val intent = Intent(Intent.ACTION_VIEW)
            with(intent) {
                if (Build.VERSION.SDK_INT >= 24) {
                    setDataAndType(FileProvider.getUriForFile(activity, activity.applicationContext.packageName + ".getCache", file), "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    setDataAndType(Uri.fromFile(file), "application/pdf")
                }
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                activity.startActivity(this)
            }
            ex.invoke()
        }
    }
}
