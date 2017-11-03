package com.bortolan.iquadriv2.Fragments


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.bortolan.iquadriv2.API.SpaggiariREST.APIClient
import com.bortolan.iquadriv2.Interfaces.models.LoginRequest
import com.bortolan.iquadriv2.R
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*


class Login : Fragment() {
    private var enable: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        enable = true
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val next_try = PreferenceManager.getDefaultSharedPreferences(context).getLong("spiaggiari_next_try", 0)
        if (next_try <= System.currentTimeMillis()) {

            mail.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context!!, R.drawable.ic_person_black), null, null, null)
            password.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context!!, R.drawable.ic_password), null, null, null)

            mail.isEnabled = true
            password.isEnabled = true
            login_btn.isEnabled = true

            login_btn.setOnClickListener { Login() }
            password.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Login()
                }
                false
            }
        } else {
            Toast.makeText(activity?.applicationContext, "Server non raggiungibile, riprovare fra " + Math.ceil((next_try - System.currentTimeMillis()) / 60000.0).toInt() + " minuti", Toast.LENGTH_SHORT).show()
            mail.isEnabled = false
            password.isEnabled = false
            login_btn.isEnabled = false
        }

    }

    private fun Login() {
        val mEmail = mail.text.toString()
        val mPassword = password.text.toString()

        mail.isEnabled = false
        password.isEnabled = false
        login_btn.isEnabled = false
        login_btn.setText(R.string.caricamento)

        APIClient.create(context!!).doLogin(LoginRequest(mPassword, mEmail))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ login ->
                    if (enable) {
                        Log.d("DATE", APIClient.dateFormat.parse(login.expire).toString())
                        PreferenceManager.getDefaultSharedPreferences(activity).edit()
                                .putBoolean("spaggiari-logged", true)
                                .putString("spaggiari-user", mEmail)
                                .putString("spaggiari-pass", mPassword)
                                .putLong("spaggiari-expireDate", APIClient.dateFormat.parse(login.expire).time)
                                .putString("spaggiari-token", login.token)
                                .putString("spaggiari-id", login.ident.substring(1))
                                .apply()

                        fragmentManager
                                ?.beginTransaction()
                                ?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                ?.replace(R.id.content, RegistroPeriodi())
                                ?.commit()
                    }
                }, { error ->
                    error.printStackTrace()
                    if (enable) {
                        if (error is HttpException) {
                            if (error.code() in 500..600) {
                                Toast.makeText(activity?.applicationContext, "Server non raggiungibile, riprovare più tardi", Toast.LENGTH_LONG).show()
                                Log.e("LOGIN", "Server non raggiungibile, riprovare più tardi")
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("spiaggiari_next_try", System.currentTimeMillis() + 5 * 60000).apply()
                            } else if (error.code() == 422) {
                                Log.e("LOGIN", "Credenziali non valide")
                                login_btn.setText(R.string.login)
                                Toast.makeText(activity?.applicationContext, "Credenziali non valide", Toast.LENGTH_SHORT).show()

                                mail.isEnabled = true
                                password.isEnabled = true
                                login_btn.isEnabled = true

                                password.setText("")
                            } else {
                                error.printStackTrace()
                            }
                        } else {
                            error.printStackTrace()
                            login_btn.setText(R.string.login)
                            Toast.makeText(activity?.applicationContext, R.string.login_msg_failer, Toast.LENGTH_SHORT).show()

                            mail.isEnabled = true
                            password.isEnabled = true
                            login_btn.isEnabled = true

                            password.setText("")
                        }
                    }
                })
    }

    override fun onStop() {
        super.onStop()
        enable = false
    }
}
