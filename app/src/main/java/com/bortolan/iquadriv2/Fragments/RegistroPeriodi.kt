package com.bortolan.iquadriv2.Fragments


import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bortolan.iquadriv2.API.SpaggiariREST.APIClient
import com.bortolan.iquadriv2.Databases.RegistroDB
import com.bortolan.iquadriv2.Interfaces.models.Grade
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods.isNetworkAvailable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_registro_periodi.*
import java.util.*

class RegistroPeriodi : Fragment() {
    internal lateinit var periodiAdapter: PeriodiPager
    internal lateinit var context: Context

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_registro_periodi, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = getContext()
        periodiAdapter = PeriodiPager(childFragmentManager)
        pager.adapter = periodiAdapter
        pager.offscreenPageLimit = 2
        toolbar.setupWithViewPager(pager)

        //update data
        load()
        download()
    }

    private fun download() {
        if (isNetworkAvailable(context)) {
            APIClient.create(context).getGrades(PreferenceManager.getDefaultSharedPreferences(context).getString("spaggiari-id", ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ save(it.grades); load() }, { it.printStackTrace() })
        } else {
            Snackbar.make(coordinator_layout, R.string.nointernet, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun save(marks: List<Grade>) {
        RegistroDB.getInstance(context).addMarks(marks)
    }

    private fun load() {
        (periodiAdapter.instantiateItem(pager, 0) as Registro).addSubjects(RegistroDB.getInstance(context).getAverages(RegistroDB.Period.FIRST, "ORDER BY subject"))
        (periodiAdapter.instantiateItem(pager, 1) as Registro).addSubjects(RegistroDB.getInstance(context).getAverages(RegistroDB.Period.SECOND, "ORDER BY subject"))
        (periodiAdapter.instantiateItem(pager, 2) as Registro).addSubjects(RegistroDB.getInstance(context).getAverages(RegistroDB.Period.ALL, "ORDER BY subject"))

        if (RegistroDB.getInstance(context).isSecondPeriodStarted)
            pager.setCurrentItem(1, true)

    }

    internal inner class PeriodiPager(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment = Registro()
            val bundle = Bundle()
            bundle.putInt("q", position)
            fragment.arguments = bundle
            return fragment
        }


        override fun getPageTitle(position: Int): CharSequence {
            if (position == 2) return "Generale"
            return String.format(Locale.getDefault(), "%d° Periodo", position + 1)
        }


        override fun getCount(): Int {
            return 3
        }
    }

}
