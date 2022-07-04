package com.singularitybd.bachaoapp.activity

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.afollestad.materialdialogs.Theme
import com.singularitybd.bachaoapp.MyService
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.databinding.ActivityMainBinding
import com.singularitybd.bachaoapp.model.CommonResponse
import com.singularitybd.bachaoapp.model.EventSpeechRecognise
import com.singularitybd.bachaoapp.model.GpsEvent
import com.singularitybd.bachaoapp.preference.PreferenceUtil
import com.singularitybd.bachaoapp.utils.AppConstants
import com.singularitybd.bachaoapp.utils.AppUtils
import com.singularitybd.bachaoapp.utils.Constants
import com.singularitybd.bachaoapp.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registration.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*


class MainActivity : BaseActivity() {

    private lateinit var countDownTimer:CountDownTimer

    private val REQUEST_AUDIO_PERMISSION_CODE = AppConstants.REQUEST_AUDIO_PERMISSION_CODE

    private val REQUEST_CODE_SPEECH_INPUT = AppConstants.REQUEST_CODE_SPEECH_INPUT

    private var mFileName: String? = null

    private var mRecorder: MediaRecorder? = null

    private var mPlayer: MediaPlayer? = null

    var recognisedTime : Int = 0

    lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )

        binding.lifecycleOwner = this

        initView(binding)

    }

    private fun initView(binding: ActivityMainBinding) {
        recognisedTime = 0
        enableAutoStart()
        if (checkServiceRunning()) {
            Log.e("enter", "enter 1")
            btStartService.setText(getString(R.string.stop_service));
            //tvText.setVisibility(View.VISIBLE);
        }

        btStartService.setOnClickListener { v ->

            if (btStartService.text.toString()
                    .equals(getString(R.string.start_service), ignoreCase = true)
            ) {
                Log.e("enter", "enter 2")

                startService(Intent(this@MainActivity, MyService::class.java))
                btStartService.text = getString(R.string.stop_service)
                //tvText.visibility = View.VISIBLE
            } else {
                Log.e("enter", "enter 2")

                stopService(Intent(this@MainActivity, MyService::class.java))
                btStartService.text = getString(R.string.start_service)
                //tvText.visibility = View.GONE
            }
        }

        RequestPermissions()

        textView_play_recording.setOnClickListener {
            playAudio()

        }

        textView_stop_recording.setOnClickListener {
            stopRecording()
        }

        /*textView_off_audio.setOnClickListener {
            stopAudio()
        }*/

        imageView_seek_help.setOnClickListener {
            //setTimer()
            startRecording()
            submitHelpSeek()
        }
    }

    private fun submitHelpSeek() {
        if (!AppUtils.hasNetworkConnection(this)) {
            AppUtils.showToast(this, getString(R.string.no_internet), false)
            return
        }

        if(isValid()){
            textView_volunteer_status.visibility = View.VISIBLE

            mainActivityViewModel.sentHelpRequest(this, current_lat_lon.text.trim().toString(), PreferenceUtil.getUserId(this)).observe(this, object : Observer<CommonResponse>{
                override fun onChanged(data: CommonResponse) {
                    if(data.responseCode == 200){
                        textView_volunteer_status.text = "Request sent successfully to volunteers"
                    }else{
                        submitHelpSeek()
                    }
                }

            })

        }
    }

    fun isValid() : Boolean{
        if(current_lat_lon.text.toString() == "current Location syncing..."){
            AppUtils.showToast(this, "wait for lat lon", false)
            return false
        }

        return true
    }

    private fun fetchRecognisedWord() {
        if(textView_recognised_word.text.toString().toLowerCase() == PreferenceUtil.getUserSecretWord(this)){
            recognisedTime++

            Log.e("recognisedTime","$recognisedTime")

            textView_recognised_word_times.text = "($recognisedTime)"

            if(recognisedTime == 2){
                //setTimer()
                startRecording()
                submitHelpSeek()

                recognisedTime = 0
                textView_recognised_word.text = "Voice Recognised Successfully"
                textView_recognised_word_times.text = ""
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun speechRecognisedEvent(event: EventSpeechRecognise) {
        if (event.isSpeechRecognised == true) {
            Log.e("eventBus", "entered MainActivity" + event.recognisedWord)
            textView_recognised_word.text = event.recognisedWord

            fetchRecognisedWord()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun gpsEvent(event: GpsEvent) {
        if(event.isGpsUpdated == true){

            current_lat_lon.text = event.latLon
            current_address.text = event.address
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

    private fun enableAutoStart() {
        for (intent in Constants.AUTO_START_INTENTS) {
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                Builder(this).title(R.string.enable_autostart)
                    .content(R.string.ask_permission)
                    .theme(Theme.LIGHT)
                    .positiveText(getString(R.string.allow))
                    .onPositive { dialog, which ->
                        try {
                            for (intent1 in Constants.AUTO_START_INTENTS) if (packageManager.resolveActivity(
                                    intent1!!, PackageManager.MATCH_DEFAULT_ONLY
                                )
                                != null
                            ) {
                                startActivity(intent1)
                                break
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Log.e("catcheMessage", "${e.message}")
                        }
                    }
                    .show()
                break
            }
        }
    }

    fun checkServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (getString(R.string.my_service_name) == service.service.className) {
                    return true
                }
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                textView_recognised_word.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    fun stopRecording() {
        mRecorder?.stop()

        mRecorder?.release()
        mRecorder = null
        textView_status.setText("Recording Stopped")
    }

    private fun playAudio() {
        mPlayer = MediaPlayer()

        try {
            if(mFileName != null){
                mPlayer?.setDataSource(mFileName)

                mPlayer?.prepare()

                mPlayer?.start()
                textView_status.text = getString(R.string.play_recording)
            }

        }catch (e : IOException){
            Log.e("TAG", "prepare() failed ${e.message}")
        }
    }

    private fun stopAudio() {
        mPlayer = MediaPlayer()

        try {
            if(mFileName != null){
                mPlayer?.setDataSource(mFileName)

                mPlayer?.prepare()

                mPlayer?.stop()
                textView_status.text = "Audio Play off"
            }

        }catch (e : IOException){
            Log.e("TAG", "prepare() failed ${e.message}")
        }
    }

    fun setTimer() {

        countDownTimer = object : CountDownTimer(60000, 1000) {

            override fun onTick(untilFinis: Long) {
                var difference = untilFinis
                val seconds: Long = 1000
                val minutes = seconds * 60

                val passedMinutes = difference / minutes
                difference %= minutes

                val passedSeconds = difference / seconds

                textView_timer.text = "$passedMinutes : $passedSeconds"

                //matchSpeechToText()
            }

            override fun onFinish() {
                textView_timer.text = "0:00"
                stopRecording()
                //textView_off_audio.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val permissionToRecord = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] === PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun startRecording() {
        if (checkPermissions()) {
            mFileName = getExternalCacheDir()?.getAbsolutePath() + "/audiorecordtest.3gp"

            mRecorder = MediaRecorder()

            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            mRecorder?.setOutputFile(mFileName)
            try {
                mRecorder?.prepare()
                mRecorder?.start()
                textView_status.text = "Recording Started"
            } catch (e: IOException) {
                Log.e("TAG", "enter here prepare() failed ${e.message}")
            }

        } else {

            RequestPermissions()
        }
    }

    private fun RequestPermissions() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }

    fun checkPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun matchSpeechToText() {
        // on below line we are calling speech recognizer intent.
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        // on below line we are passing language model
        // and model free form in our intent
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        // on below line we are passing our
        // language as a default language.
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        // on below line we are specifying a prompt
        // message as speak to text on below line.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        // on below line we are specifying a try catch block.
        // in this block we are calling a start activity
        // for result method and passing our result code.
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            // on below line we are displaying error message in toast
            Toast
                .makeText(
                    this@MainActivity, " " + e.message,
                    Toast.LENGTH_SHORT
                )
                .show()

            /*textView_recognised_word.text = event.recognisedWord

            fetchRecognisedWord()*/
        }
    }

}