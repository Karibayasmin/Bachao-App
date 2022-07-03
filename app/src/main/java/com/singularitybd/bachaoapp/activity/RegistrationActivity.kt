package com.singularitybd.bachaoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.emergency.EmergencyNumber
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.databinding.ActivityRegistrationBinding
import com.singularitybd.bachaoapp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_registration.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class RegistrationActivity : AppCompatActivity() {

    var userTypeList : ArrayList<String> = ArrayList()

    var selectedUserType : String = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registration)

        var binding = DataBindingUtil.setContentView<ActivityRegistrationBinding>(this, R.layout.activity_registration)

        binding.lifecycleOwner = this

        setUserTypeDropDown()

        submitRegistration()
    }

    private fun submitRegistration() {
        if (!AppUtils.hasNetworkConnection(this)) {
            AppUtils.showToast(this, getString(R.string.no_internet), false)
            return
        }

        name = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_name.text.toString())
        mobileNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_mobile.text.toString())
        userType = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedUserType)
        isVolunteer = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_is_volunteer.text.toString())
        secretWord = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_secretWord.text.toString())
        email = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_email.text.toString())
        emergencyNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_emergency_number.text.toString())
        previousExperience = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_previous_experience.text.toString())
        occupation = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_occupation.text.toString())
        latitudeLongitude = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_latLon.text.toString())
        address = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_address.text.toString())
        bloodGroup = RequestBody.create("text/plain".toMediaTypeOrNull(), editText_bloodGroup.text.toString())

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