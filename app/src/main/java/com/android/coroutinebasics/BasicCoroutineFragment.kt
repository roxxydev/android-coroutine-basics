package com.android.coroutinebasics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_basic_coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class BasicCoroutineFragment : Fragment(R.layout.fragment_basic_coroutine) {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    private val JOB_TIMEOUT = 3000L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener{

            CoroutineScope(IO).launch {

                // fakeApiRequest()
                fakeApiRequest2()
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest() {
        val result1 = getResultFromApi1()
        println("debug: $result1")
        setTextOnMainThread(result1)
    }

    private suspend fun fakeApiRequest2() {
        withContext(IO) {

            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResultFromApi1()
                setTextOnMainThread(result1)

                val result2 = getResultFromApi2()
                setTextOnMainThread(result2)
            }

            if (job === null) {
                val cancelMessage = "Cancelling a job...Job took longer than ${JOB_TIMEOUT}ms"
                println("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
    }

    private suspend fun getResultFromApi1(): String {
        logThread("getResultFromApi1")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResultFromApi2(): String {
        logThread("getResultFromApi2")
        delay(1000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}