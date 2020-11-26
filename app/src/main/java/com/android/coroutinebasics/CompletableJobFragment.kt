package com.android.coroutinebasics

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_completable_job.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CompletableJobFragment: Fragment(R.layout.fragment_completable_job) {

    private val TAG: String = "AppDebug"

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 5000;
    private lateinit var job: CompletableJob

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job_button.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }

    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. Cancelling...")
            resetJob()
        }
        else {
            job_button.setText("Cancel job #1")
            CoroutineScope(IO + job).launch {
                println("coroutine $this is activate with job $job")

                for (i in PROGRESS_START.. PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }

                updateJobCompleteText("Job is complete")

                delay(1500)
                resetJob();
            }
        }
    }

    private fun updateJobCompleteText(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.setText(text)
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    fun initJob() {
        GlobalScope.launch(Main) {
            job_button.setText("Start Job #1")
        }

        updateJobCompleteText("")

        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if(msg.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                println("$job was cancelled. Reason: $msg")
                showToast(msg)
            }
        }

        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        }
    }
}