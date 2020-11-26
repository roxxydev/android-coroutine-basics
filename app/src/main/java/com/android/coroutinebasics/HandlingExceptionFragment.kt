package com.android.coroutinebasics

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_handling_exception.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class HandlingExceptionFragment: Fragment(R.layout.fragment_handling_exception) {

    private val TAG: String = "AppDebug"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            main()
        }
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Exception thrown in one of the children: $exception")
    }

    fun main(){
        val parentJob = CoroutineScope(IO).launch(handler) {

            // --------- JOB A ---------
            val jobA = launch {
                val resultA = getResult(1)
                println("resultA: ${resultA}")
            }
            jobA.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    println("Error getting resultA: ${throwable}")
                }
            }

            // --------- JOB B ---------
            val jobB = launch {
                val resultB = getResult(2)
                println("resultB: ${resultB}")
            }
            jobB.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    println("Error getting resultB: ${throwable}")
                }
            }

            // --------- JOB C ---------
            val jobC = launch {
                val resultC = getResult(3)
                println("resultC: ${resultC}")
            }
            jobC.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    println("Error getting resultC: ${throwable}")
                }
            }
        }

        /*
        val parentJob = CoroutineScope(IO).launch(handler) {

            // Using supervisorScope lets the exception in child coroutine be handled independently
            // and will also not cancel the parent coroutine.

            supervisorScope {
                // --------- JOB A ---------
                val jobA = launch {
                    val resultA = getResult(1)
                    println("resultA: ${resultA}")
                }

                // --------- JOB B ---------
                val jobB = launch {
                    val resultB = getResult(2)
                    println("resultB: ${resultB}")
                }
                jobB.invokeOnCompletion { throwable ->
                    if(throwable != null){
                        println("Error getting resultB: ${throwable}")
                    }
                }
            }
        }
        */

        parentJob.invokeOnCompletion { throwable ->
            if(throwable != null){
                println("Parent job failed: ${throwable}")
            }
            else{
                println("Parent job SUCCESS")
            }
        }
    }

    suspend fun getResult(number: Int): Int{
        return withContext(Main){
            delay(number*500L)
            if(number == 2){

                // Won't cancel the parent Coroutine
                // cancel(CancellationException("Error getting result for number: ${number}"))

                // Will cancel the Job but other Jobs in parent Coroutine continues.
                // Exception will be handled by CoroutineExceptionHandler of the parent Coroutine
                throw CancellationException("Error getting result for number: ${number}") // treated like "cancel()"

                // Will cause the app to crash as the Exception is unhandled by the Job and parent Coroutine
                // throw Exception("Error getting result for number: ${number}")
            }
            number*2
        }
    }


    private fun println(message: String){
        Log.d(TAG, message)
    }
}