package com.singularitybd.bachaoapp

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.singularitybd.bachaoapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var countDownTimer:CountDownTimer

    private val REQUEST_AUDIO_PERMISSION_CODE = 1

    private val REQUEST_CODE_SPEECH_INPUT = 2

    private var mFileName: String? = null

    private var mRecorder: MediaRecorder? = null

    private var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        initView(binding)

    }

    private fun initView(binding: ActivityMainBinding) {
        setTimer()

        RequestPermissions()
        //startRecording()

        matchSpeechToText()

        textView_play_recording.setOnClickListener {
            playAudio()

        }

        textView_stop_recording.setOnClickListener {
            stopRecording()
        }
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
        }
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
                textView_status.setText(
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

                matchSpeechToText()
            }

            override fun onFinish() {
                textView_timer.text = "0:00"
                stopRecording()
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
}