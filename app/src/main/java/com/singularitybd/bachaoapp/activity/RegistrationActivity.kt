package com.singularitybd.bachaoapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.databinding.ActivityRegistrationBinding
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.GpsEvent
import com.singularitybd.bachaoapp.utils.AppUtils
import com.singularitybd.bachaoapp.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_registration.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RegistrationActivity : BaseActivity() {

    lateinit var registrationViewModel : RegistrationViewModel

    var userTypeList : ArrayList<String> = ArrayList()
    var volunteerTypeList : ArrayList<String> = ArrayList()

    var selectedUserType : String = ""
    var selectedVolunteerType : String = ""
    var selectedVolunteerTypeString : String = ""

    lateinit private var name : RequestBody
    lateinit private var mobileNumber : RequestBody
    lateinit private var userType : RequestBody
    lateinit private var isVolunteer : RequestBody
    lateinit private var secretWord : RequestBody
    lateinit private var email : RequestBody
    lateinit private var emergencyNumber : RequestBody
    lateinit private var previousExperience : RequestBody
    lateinit private var occupation : RequestBody
    lateinit private var latitudeLongitude : RequestBody
    lateinit private var address : RequestBody
    lateinit private var bloodGroup : RequestBody
    lateinit private var password : RequestBody

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registration)

        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        var binding = DataBindingUtil.setContentView<ActivityRegistrationBinding>(this, R.layout.activity_registration)

        binding.lifecycleOwner = this
        binding.viewModel = registrationViewModel

        initView(binding)
    }

    private fun initView(binding: ActivityRegistrationBinding) {
        setUserTypeDropDown()
        setVolunteerTypeDropDown()

        button_registration.setOnClickListener {
            submitRegistration()
        }
    }

    private fun setVolunteerTypeDropDown() {
        volunteerTypeList.add(getString(R.string.is_volunteer))
        volunteerTypeList.add("yes")
        volunteerTypeList.add("no")
        var spinnerItemAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, volunteerTypeList)

        volunteerTypeSpinner.adapter = spinnerItemAdapter

        volunteerTypeSpinner.setSelection(0)

        volunteerTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if(parent.getItemAtPosition(position).toString().toLowerCase() == "yes"){
                    selectedVolunteerType = "1"
                }else if(parent.getItemAtPosition(position).toString().toLowerCase() == "no"){
                    selectedVolunteerType = "0"
                }

                selectedVolunteerTypeString = parent.getItemAtPosition(position).toString()
                Toast.makeText(parent.getContext(), selectedVolunteerType , Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.e("latLong", "$latLong")
        editText_latLon.setText(latLong)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun gpsEvent(event: GpsEvent) {
        if(event.isGpsUpdated == true){
            editText_latLon.setText(event.latLon)
            editText_address.setText(event.address)
        }
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    private fun submitRegistration() {
        if (!AppUtils.hasNetworkConnection(this)) {
            AppUtils.showToast(this, getString(R.string.no_internet), false)
            return
        }

        if(isValid()){
            progressDialog.show()

            name = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_name.text.toString())
            mobileNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_mobile.text.toString())
            userType = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedUserType)
            isVolunteer = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedVolunteerType)
            secretWord = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_secretWord.text.toString())
            email = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_email.text.toString())
            emergencyNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_emergency_number.text.toString())
            previousExperience = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_previous_experience.text.toString())
            occupation = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_occupation.text.toString())
            latitudeLongitude = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_latLon.text.toString())
            address = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_address.text.toString())
            bloodGroup = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_bloodGroup.text.toString())
            password = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_password.text.toString())

            registrationViewModel?.submitRegistration(this, name, mobileNumber, userType, isVolunteer, secretWord, email, emergencyNumber, previousExperience, occupation, latitudeLongitude, address, bloodGroup, password)?.observe(this, object : Observer<CommonResponse>{
                override fun onChanged(data: CommonResponse) {
                    progressDialog.dismiss()

                    if(data.responseCode == 200){
                        AppUtils.showToast(this@RegistrationActivity, data.appMessage ?: getString(R.string.common_success), false)
                        onBackPressed()
                    }else{
                        AppUtils.showToast(this@RegistrationActivity, data.appMessage ?: getString(R.string.common_error), false)
                    }
                }

            })

        }

    }

    private fun isValid(): Boolean{
    Log.e("selectedVolunteerType", "$selectedVolunteerType")
        if(editText_name.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_name), false)
            return false
        }

        if(editText_mobile.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_mobile_number), false)
            return false
        }

        if(selectedUserType.toLowerCase() == getString(R.string.select_user_type).toLowerCase()){
            AppUtils.showToast(this, getString(R.string.please_select_user_type), false)
            return false
        }
        if(editText_secretWord.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_secret_word), false)
            return false
        }

        if(selectedVolunteerTypeString.toLowerCase() == getString(R.string.is_volunteer).toLowerCase()){
            AppUtils.showToast(this, getString(R.string.select_is_volunteer), false)
            return false
        }

        if(editText_email.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_email), false)
            return false
        }

        if(editText_emergency_number.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_emergency_mobile_number), false)
            return false
        }

        if(editText_emergency_number.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_emergency_mobile_number), false)
            return false
        }

        if(editText_previous_experience.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_previous_experience), false)
            return false
        }

        if(editText_occupation.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_occupation), false)
            return false
        }

        if(editText_latLon.text?.trim()?.isEmpty() == true){
            if (!AppUtils.hasNetworkConnection(this)) {
                AppUtils.showToast(this, getString(R.string.check_internet_for_sync), false)
                editText_latLon.setText(getString(R.string.check_internet_for_sync))
            }else{
                editText_latLon.setText(getString(R.string.syncing_lat_lon))
            }
            return false
        }

        if(editText_address.text?.trim()?.isEmpty() == true){
            if (!AppUtils.hasNetworkConnection(this)) {
                AppUtils.showToast(this, getString(R.string.check_internet_for_sync), false)
                editText_address.setText(getString(R.string.check_internet_for_sync))
            }else{
                editText_address.setText(getString(R.string.syncing_lat_lon))
            }
            return false
        }

        if(editText_bloodGroup.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_blood_group), false)
            return false
        }

        if(editText_password.text?.trim()?.isEmpty() == true){
            AppUtils.showToast(this, getString(R.string.enter_password), false)
            return false
        }

        return true
    }

    private fun setUserTypeDropDown() {
        userTypeList.add("Select user type")
        userTypeList.add("shopkeeper")
        userTypeList.add("general")
        var spinnerItemAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, userTypeList)

        userTypeSpinner.adapter = spinnerItemAdapter

        userTypeSpinner.setSelection(0)

        userTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectedUserType = parent.getItemAtPosition(position).toString()
                Toast.makeText(parent.getContext(), selectedUserType , Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}