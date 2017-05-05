package com.bortolan.iquadriv2.Fragments


import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.Adapters.AdapterMedie
import com.bortolan.iquadriv2.Interfaces.Average
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_registro.*

class Registro : Fragment() {
    internal var adapter: AdapterMedie? = null

    internal var periodo: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_registro, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        periodo = arguments.getInt("q")
        logout.setOnClickListener({ this.logout() })

        adapter = AdapterMedie(context, periodo)
        recycler.layoutManager = GridLayoutManager(context, 2)
        recycler.addItemDecoration(ItemOffsetDecoration(context, R.dimen.card_margin))
        recycler.adapter = adapter
        recycler.isNestedScrollingEnabled = false
    }

    fun logout() {
        val settings = context.getSharedPreferences("registro", MODE_PRIVATE)
        settings.edit().putBoolean("logged", false).apply()

        activity.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content, Login())
                .commit()
    }


    fun addSubjects(markSubjects: List<Average>) {
        if (markSubjects.isNotEmpty()) {
            adapter?.clear()
            adapter?.addAll(markSubjects)
        }
    }

    companion object {
        internal val TAG = "Registro"
    }
}
