package com.bortolan.iquadriv2.Fragments


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.Activities.ActivitySettings
import com.bortolan.iquadriv2.Activities.OrarioActivity
import com.bortolan.iquadriv2.Databases.FavouritesDB
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem
import com.bortolan.iquadriv2.R
import com.vansuita.gaussianblur.GaussianBlur
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.IOException
import java.io.InputStream

class Home : Fragment() {
    internal var itemList: MutableList<GitHubItem> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmap = getBitmapFromAsset(context, "bg.jpeg")

        val catBitmap = GaussianBlur.with(context).radius(15).noScaleDown(false).render(bitmap)
        image.setImageBitmap(catBitmap)
        settings.setOnClickListener { _ -> startActivity(Intent(context, ActivitySettings::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        itemList = FavouritesDB.getInstance(context).all
        if (!itemList.isEmpty()) {
            favourite.visibility = View.VISIBLE
            favourite.setOnClickListener { _ -> startActivity(Intent(context, OrarioActivity::class.java).putExtra("name", itemList[0].name).putExtra("url", itemList[0].url)) }
        } else {
            favourite.visibility = View.GONE
        }
    }

    companion object {

        fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
            val assetManager = context.assets

            val istr: InputStream
            var bitmap: Bitmap? = null
            try {
                istr = assetManager.open(filePath)
                bitmap = BitmapFactory.decodeStream(istr)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bitmap
        }
    }
}
