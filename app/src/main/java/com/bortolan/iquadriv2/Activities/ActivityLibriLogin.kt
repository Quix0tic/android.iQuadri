package com.bortolan.iquadriv2.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bortolan.iquadriv2.API.Libri.LibriAPI
import com.bortolan.iquadriv2.BuildConfig
import com.bortolan.iquadriv2.Interfaces.Libri.UserResponse
import com.bortolan.iquadriv2.R
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.crashlytics.android.answers.SignUpEvent
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.transitionseverywhere.AutoTransition
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.sephiroth.android.library.tooltip.Tooltip
import kotlinx.android.synthetic.main.activity_libri_login.*

class ActivityLibriLogin : AppCompatActivity() {
    var register: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libri_login)

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            register = isChecked
            TransitionManager.beginDelayedTransition(view_group, AutoTransition().setInterpolator(DecelerateInterpolator(2f)))
            TransitionManager.beginDelayedTransition(headline.rootView as ViewGroup, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN).setInterpolator(DecelerateInterpolator(2f)))
            name.visibility = if (isChecked) View.VISIBLE else View.GONE
            confirm.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked)
                name.requestFocus()
            else
                phone.requestFocus()
        }

        login.setOnClickListener {
            MaterialDialog.Builder(this)
                    .title("Continuare?")
                    .content(Html.fromHtml(String.format("%1s, usufruirai del servizio promosso dall'applicazione <b>Scambio Libri</b>, disponibile nel Play Store.", if (register) "Registrandoti" else "Accedendo")))
                    .negativeText("No")
                    .positiveText("Sì")
                    .onPositive { _, _ ->
                        if (register) {
                            signup()
                        } else {
                            login()
                        }
                    }.show()
        }
    }

    fun login() {
        enableAll(false)
        LibriAPI(this).mService.postLogin(phone.text.toString(), password.text.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t: UserResponse? ->
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("libri_api_logged", true).putString("city", t?.user?.city ?: "Vicenza").apply()
                    enableAll(true)
                    if (!BuildConfig.DEBUG) Answers.getInstance()?.logLogin(LoginEvent().putSuccess(true))
                    finish()
                }, { t ->
                    if (t is HttpException) run {
                        if (t.code() == 401)
                            Toast.makeText(this, "Login fallito", Toast.LENGTH_SHORT).show()
                    }
                    t.printStackTrace()
                    enableAll(true)
                    if (!BuildConfig.DEBUG) Answers.getInstance()?.logLogin(LoginEvent().putSuccess(true).putCustomAttribute("error", t.localizedMessage))
                })
    }

    fun signup(): Boolean {
        enableAll(false)
        if (isName() && isPhone() && isPassword()) {
            LibriAPI(this).mService.postSignup(phone.text.toString(), password.text.toString(), "Vicenza", name.text.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("libri_api_logged", true).putString("city", "Vicenza").apply()
                        if (!BuildConfig.DEBUG) Answers.getInstance()?.logSignUp(SignUpEvent().putSuccess(true))
                        enableAll(true)
                        finish()
                    }, { t ->
                        if (t is HttpException) run {
                            if (t.code() == 401)
                                Toast.makeText(this, "Registrazione fallita", Toast.LENGTH_SHORT).show()
                        }
                        t.printStackTrace()
                        if (!BuildConfig.DEBUG) Answers.getInstance()?.logSignUp(SignUpEvent().putSuccess(false).putCustomAttribute("error", t.localizedMessage))
                        enableAll(true)
                    })
        } else {
            enableAll(true)
        }
        return true
    }

    fun enableAll(e: Boolean) {
        name?.isEnabled = e
        phone?.isEnabled = e
        password?.isEnabled = e
        confirm?.isEnabled = e
        login?.isEnabled = e
        checkbox?.isEnabled = e
    }

    fun isName(): Boolean {
        if (name.text.toString().isNullOrEmpty()) {
            Tooltip.make(this, Tooltip.Builder().anchor(name, Tooltip.Gravity.BOTTOM).text("Parametro obbligatorio").withArrow(true).withOverlay(true).closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 5000).build()).show()
            name?.requestFocus()
            return false
        }
        return true
    }

    fun isPhone(): Boolean {
        if (phone.text.length != 10) {
            Tooltip.make(this, Tooltip.Builder().anchor(phone, Tooltip.Gravity.BOTTOM).text("10 cifre necessarie").withArrow(true).withOverlay(true).closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 5000).build()).show()
            phone?.requestFocus()
            return false
        }
        return true
    }

    fun isPassword(): Boolean {
        if (password.text.isNullOrBlank()) {
            Tooltip.make(this, Tooltip.Builder().anchor(password, Tooltip.Gravity.BOTTOM).text("La password non può essere vuota").withArrow(true).withOverlay(true).closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 5000).build()).show()
            password.requestFocus()
            return false
        }
        if (password.text.toString() != confirm.text.toString()) {
            Tooltip.make(this, Tooltip.Builder().anchor(confirm, Tooltip.Gravity.BOTTOM).text("Le password non coincidono").withArrow(true).withOverlay(true).closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 5000).build()).show()
            confirm.postDelayed({
                confirm?.text?.clear()
                confirm?.requestFocus()
            }, 1000)
            return false
        }
        return true
    }
}