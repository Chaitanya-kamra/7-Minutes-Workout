package com.example.a7minworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minworkout.databinding.ActivityExerciseBinding
import com.example.a7minworkout.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts :TextToSpeech? = null

    private var binding:ActivityExerciseBinding? = null

    private var restTimer :CountDownTimer? = null
    private var restProgress = 0
    private val restTimeDuration :Long = 10000

    private var exerciseTimer :CountDownTimer? = null
    private var exerciseProgress = 0
    private val exerciseTimeDuration :Long = 30000

    private var exerciseList :ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = 0

    private var player:MediaPlayer? = null
    var adapter : ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)



      setSupportActionBar(binding?.toolBar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolBar?.setNavigationOnClickListener {
            backDialogPressed()
        }

        tts = TextToSpeech(this,this)
        exerciseList = Constants.getExerciseList()

         setRestView()
        setUpExerciseStatusRv()
    }

    private fun backDialogPressed() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    override fun onBackPressed() {
        backDialogPressed()

    }

    private fun setUpExerciseStatusRv(){

        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = adapter
    }
    private fun speakOut(text:String){
        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")

    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.UK)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","not Supported")
            }
        }else{
            Log.e("TTS","init failed")
        }
    }
    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object:  CountDownTimer(restTimeDuration,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = (restTimeDuration/1000).toInt() - restProgress
                binding?.tvTimer?.text = ((restTimeDuration/1000).toInt() - restProgress).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(true)
                adapter!!.notifyDataSetChanged()
                setExerciseView()
            }

        }.start()
    }

    private fun setRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.example.a7minworkout/"+ R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e : Exception){
            e.printStackTrace()
        }

        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.text = "Get Ready For " + exerciseList!![currentExercisePosition].getName()


        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        speakOut("Relax")

        binding?.ivImage?.setImageResource(R.drawable.ic_rest)
        setRestProgressBar()

    }
    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress
        exerciseTimer = object:  CountDownTimer(exerciseTimeDuration,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = (exerciseTimeDuration/1000).toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = ((exerciseTimeDuration/1000).toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition< exerciseList!!.size-1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    adapter!!.notifyDataSetChanged()
                    currentExercisePosition++
                    setRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }


            }

        }.start()
    }

    private fun setExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.text = exerciseList!![currentExercisePosition].getName()
        binding?.flExerciseView?.visibility = View.VISIBLE
        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        setExerciseProgressBar()

    }

    override fun onDestroy() {
        super.onDestroy()


        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        if (player!= null){
            player!!.stop()
        }
        binding = null
    }
}