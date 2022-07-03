package com.singularitybd.bachaoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.emergency.EmergencyNumber
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.databinding.ActivityRegistrationBinding
import com.singularitybd.bachaoapp.model.EventSpeechRecognise
import com.singularitybd.bachaoapp.model.GpsEvent
import com.singularitybd.bachaoapp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registration.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RegistrationActivity : BaseActivity() {

    var userTypeList : ArrayList<String> = ArrayList()
    var volunteerTypeList : ArrayList<String> = ArrayList()

    var selectedUserType : String = ""
    var selectedVolunteerType : String = ""

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

        initView(binding)
    }

    private fun initView(binding: ActivityRegistrationBinding) {
        setUserTypeDropDown()
        setVolunteerTypeDropDown()

        submitRegistration()
    }

    private fun setVolunteerTypeDropDown() {
        volunteerTypeList.add("Select user type")
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
                selectedVolunteerType = parent.getItemAtPosition(position).toString()
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