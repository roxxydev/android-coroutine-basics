package com.android.coroutinebasics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_parallel_task.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class ParallelTasksFragment: Fragment(R.layout.fragment_parallel_task) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener{
            setNewText("Clicked!")

            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {

            // Using Job
//            val job1 = launch{
//                val time1 = measureTimeMillis {
//                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
//                    val result1 = getResult1FromApi()
//                    setTextOnMainThread("Got $result1")
//                }
//                println("debug: completed job1 in $time1 ms")
//            }
//
//            val job2 = launch{
//                val time2 = measureTimeMillis {
//                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
//                    val result2 = getResult2FromApi()
//                    setTextOnMainThread("Got $result2")
//                }
//                println("debug: completed job2 in $time2 ms")
//            }

            //Using async/await
            val executionTime = measureTimeMillis {

                val result1: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }

                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }
            println("debug: total time elapsed: $executionTime ms")
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

    private suspend fun getResult1FromApi(): String {
        delay(5000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(500)
        return "Result #2"
    }
}