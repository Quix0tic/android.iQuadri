package com.bortolan.iquadriv2.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.bortolan.iquadriv2.Adapters.AdapterLibri
import com.bortolan.iquadriv2.LibriAPI.LibriAPI
import com.bortolan.iquadriv2.R
import com.bortolan.iquadriv2.Utils.Methods.dpToPx
import com.bortolan.iquadriv2.Views.SwipableRecyclerView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_libri.*


class Libri : Fragment(), SearchView.OnQueryTextListener, SwipableRecyclerView.OnSwipeActionListener, TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        input = s.toString()
    }

    override fun onSwipe(position: Int, direction: Int) {
        val phone = adapter?.getAt(position)?.phone
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + phone)
        context?.startActivity(intent)
    }

    var input: String = ""

    override fun onQueryTextSubmit(query: String?): Boolean {
        search_view.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter?.filter?.filter(newText)
        return true
    }

    internal var adapter: AdapterLibri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_libri, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.w("FIREBASE", FirebaseInstanceId.getInstance().token!!)
        place_holder.setOnClickListener {
            input = search_view.query.toString()
            MaterialDialog.Builder(context)
                    .title("Ricevere notifiche?")
                    .input("ISBN", search_view.query, false, { _, _ -> })
                    .inputRange(13, 13)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .content("Riceverai una notifica quando verrà pubblicato l'annuncio di un libro avente l'ISBN inserito")
                    .positiveText("Sì")
                    .negativeText("No")
                    .onPositive { _, _ -> FirebaseMessaging.getInstance().subscribeToTopic(PreferenceManager.getDefaultSharedPreferences(context).getString("city", "vicenza").plus("_").plus(input).toLowerCase());Log.w("TOPIC", PreferenceManager.getDefaultSharedPreferences(context).getString("city", "vicenza").plus("_").plus(input).toLowerCase()) }
                    .show().inputEditText?.addTextChangedListener(this)
        }
        adapter = AdapterLibri(recycler, place_holder)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).size(dpToPx(1f).toInt()).build())
        recycler.listener = this

        search_view.setOnQueryTextListener(this)
        search_view.findViewById(R.id.search_close_btn).setOnClickListener {
            search_view.clearFocus()
            search_card.requestFocus()
            search_view.setQuery("", true)
        }

        download()
    }

    fun download() {
        LibriAPI(context).mService.getAnnouncements(PreferenceManager.getDefaultSharedPreferences(context).getString("city", "vicenza"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter?.clear()
                    adapter?.addAll(it)
                    if (!search_view.query.isNullOrEmpty()) search_view.setQuery(search_view.query, true)
                }, Throwable::printStackTrace)
    }
}