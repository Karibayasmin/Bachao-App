package com.singularitybd.bachaoapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.databinding.ActivityLoginBinding
import com.singularitybd.bachaoapp.firebase.FirebaseHandler
import com.singularitybd.bachaoapp.model.LoginResponse
import com.singularitybd.bachaoapp.model.ProfileData
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import com.singularitybd.bachaoapp.utils.AppUtils
import com.singularitybd.bachaoapp.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.editText_mobile
import kotlinx.android.synthetic.main.activity_login.editText_password
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class LoginActivity : BaseActivity() {

    lateinit var loginViewModel : LoginViewModel

    lateinit private var password : RequestBody
    lateinit private var mobileNumber : RequestBody
    lateinit private var deviceToken : RequestBody

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        var binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)

        binding.lifecycleOwner = this
        binding.viemodel = loginViewModel

        button_login.setOnClickListener {
            submitLogin()

        }

        textView_registration.setOnClickListener {
            var intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValid(): Boolean{
        if(editText_mobile.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_mobile_number), false)
            return false
        }

        if(editText_password.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_password), false)
            return false
        }
            return true
    }

    private fun submitLogin() {
        if (!AppUtils.hasNetworkConnection(this)) {
            AppUtils.showToast(this, getString(R.string.no_internet), false)
            return
        }

        if(isValid()){
            progressDialog.show()

            mobileNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_mobile.text.toString())
            password = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_password.text.toString())

            loginViewModel.submitLogin(this, mobileNumber, password)?.observe(this, object : Observer<LoginResponse>{
                override fun onChanged(data: LoginResponse) {
                    if(data.responseCode == 200){

                        PreferenceUtil.saveAccessToken(this@LoginActivity, data?.accessToken ?: "")
                        PreferenceUtil.saveIsLoggedIn(this@LoginActivity, true)
                        PreferenceUtil.saveUserData(this@LoginActivity, data?.profileData ?: ProfileData())

                        if (AppUtils.hasNetworkConnection(this@LoginActivity)) {
                            FirebaseHandler.enableFirebase()
                            FirebaseHandler.registerFcmTokenServer(this@LoginActivity)
                        }

                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{
                        AppUtils.showToast(this@LoginActivity, data.appMessage ?: getString(R.string.common_error), false)
                    }
                }

            })
        }
    }
}