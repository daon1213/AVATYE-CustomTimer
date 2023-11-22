package com.daon.customtimer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.daon.customtimer.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timerView: TimerView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var cancelButton: Button
    private lateinit var editText: EditText
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var progress = 0 // progress 변수 추가
    private var savedProgress = 0 // 저장된 진행률 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerView = binding.timerView
        startButton = binding.startButton
        stopButton = binding.stopButton
        cancelButton = binding.cancelButton
        editText = binding.editText

        startButton.setOnClickListener {
            val inputText = editText.text.toString()
            if (inputText.isBlank()) {
                // EditText에 값이 입력되지 않았을 때 다이얼로그를 표시합니다.
                showInputDialog("시간을 입력해주세요")
            } else if (!isTimerRunning) {
                val totalTimeInSeconds = convertInputToSeconds(inputText)
                if (totalTimeInSeconds > 0) {
                    // 저장된 진행 상태가 있으면 해당 상태로 타이머를 시작합니다.
                    startTimer(totalTimeInSeconds, savedProgress)
                }
            }
        }

        stopButton.setOnClickListener {
            if (isTimerRunning) {
                countDownTimer.cancel()
                isTimerRunning = false
                savedProgress = progress // 현재 진행 상태를 저장합니다.
            }
        }

        cancelButton.setOnClickListener {
            if (isTimerRunning) {
                countDownTimer.cancel() // 타이머를 종료합니다.
                savedProgress = 0
                isTimerRunning = false
                timerView.updateProgressAnimated(0) // 원을 초기화합니다.
            } else {
                val inputText = editText.text.toString()
                if (inputText.isBlank()) {
                    // EditText에 값이 입력되지 않았을 때 다이얼로그를 표시합니다.
                    showInputDialog("시간을 입력해주세요")
                }
            }
        }
    }

    private fun convertInputToSeconds(input: String): Long {
        val parts = input.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toLongOrNull()
            val seconds = parts[1].toLongOrNull()
            if (minutes != null && seconds != null) {
                return (minutes * 60 + seconds)
            }
        }
        return -1 // 잘못된 입력
    }

    private fun startTimer(totalTimeInSeconds: Long, startProgress: Int = 0) {
        val totalMilliseconds = totalTimeInSeconds * 1000
        val progressPerSecond = 100 / totalTimeInSeconds.toDouble()

        countDownTimer = object : CountDownTimer(totalMilliseconds, 1000) {
            var isFinished = false

            override fun onTick(millisUntilFinished: Long) {
                if (!isFinished) {
                    val elapsedMilliseconds =
                        totalMilliseconds - millisUntilFinished
                    val calculatedProgress =
                        (elapsedMilliseconds * progressPerSecond / 1000).toInt()
                    progress = startProgress + calculatedProgress
                    Log.d("Timer", "onTick - Progress: $progress")
                    isTimerRunning = true
                    timerView.updateProgressAnimated(progress)

                    // 예외상황을 위한 처리
                    if (progress >= 100) {
                        onFinish()
                        isFinished = true
                    }
                }
            }

            override fun onFinish() {
                Log.d("Timer", "Finish")
                isTimerRunning = false
                timerView.updateProgressAnimated(100)
                showTimerDialog("타이머가 종료되었습니다.")
                GlobalScope.launch(Dispatchers.Main) {
                    delay(800L)
                    timerView.updateProgressAnimated(0)
                    savedProgress = 0
                }
            }
        }

        countDownTimer.start()
    }

    private fun showTimerDialog(timermessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(timermessage)
        builder.setPositiveButton("확인") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showInputDialog(inputmessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(inputmessage)
        builder.setPositiveButton("확인") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}