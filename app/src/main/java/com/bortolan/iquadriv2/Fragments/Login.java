package com.bortolan.iquadriv2.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.bortolan.iquadriv2.API.Spaggiari.SpiaggiariApiClient;
import com.bortolan.iquadriv2.R;
import com.bortolan.iquadriv2.Utils.DeviceUuidFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;


public class Login extends Fragment {
    @BindView(R.id.mail)
    TextInputEditText mEditTextMail;
    @BindView(R.id.password)
    TextInputEditText mEditTextPassword;
    @BindView(R.id.login_btn)
    Button mButtonLogin;
    private Context mContext;
    private Boolean enable = false;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        enable = true;

        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, layout);
        long next_try = PreferenceManager.getDefaultSharedPreferences(mContext).getLong("spiaggiari_next_try", 0);
        if (next_try <= System.currentTimeMillis()) {

            mEditTextMail.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_person_black), null, null, null);
            mEditTextPassword.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_password), null, null, null);

            mEditTextMail.setEnabled(true);
            mEditTextPassword.setEnabled(true);
            mButtonLogin.setEnabled(true);

            mButtonLogin.setOnClickListener(v -> Login());
            mEditTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Login();
                    return true;
                }
                return false;
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Server non raggiungibile, riprovare fra " + (int) Math.ceil((next_try - System.currentTimeMillis()) / 60000.0) + " minuti", Toast.LENGTH_SHORT).show();
            mEditTextMail.setEnabled(false);
            mEditTextPassword.setEnabled(false);
            mButtonLogin.setEnabled(false);
        }

        return layout;
    }

    private void Login() {
        String mEmail = mEditTextMail.getText().toString();
        String mPassword = mEditTextPassword.getText().toString();

        mEditTextMail.setEnabled(false);
        mEditTextPassword.setEnabled(false);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setText(R.string.caricamento);

        new SpiaggiariApiClient(mContext).mService.postLogin(mEmail, mPassword, new DeviceUuidFactory(mContext).getDeviceUuid().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    if (enable) {
                        SharedPreferences settings = mContext.getSharedPreferences("registro", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("logged", true);
                        editor.apply();

                        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.content, new RegistroPeriodi()).commit();
                        Toast.makeText(mContext, R.string.login_msg, Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    if (enable) {
                        if (error instanceof HttpException) {
                            Log.e("LOGIN", "HTTPEXCEPTION");
                            if (!((HttpException) error).response().isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), "Server non raggiungibile, riprovare più tardi", Toast.LENGTH_LONG).show();
                                Log.e("LOGIN", "Server non raggiungibile, riprovare più tardi");
                                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putLong("spiaggiari_next_try", System.currentTimeMillis() + 5 * 60000).apply();
                            }
                        } else {
                            error.printStackTrace();
                            mButtonLogin.setText(R.string.login);
                            Toast.makeText(mContext, R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                            mEditTextMail.setEnabled(true);
                            mEditTextPassword.setEnabled(true);
                            mButtonLogin.setEnabled(true);

                            mEditTextPassword.setText("");
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        enable = false;
    }
}