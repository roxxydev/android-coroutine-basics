package com.android.coroutinebasics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment(R.layout.fragment_main), View.OnClickListener {

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view);
        view_basic_coroutine_btn.setOnClickListener(this)
        view_completable_job_btn.setOnClickListener(this)
        view_parallel_task_btn.setOnClickListener(this)
        view_handling_exception_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.view_basic_coroutine_btn -> navController!!.navigate(R.id.action_mainFragment_to_basicCoroutineFragment)
            R.id.view_completable_job_btn -> navController!!.navigate(R.id.action_mainFragment_to_completableJobFragment)
            R.id.view_parallel_task_btn -> navController!!.navigate(R.id.action_mainFragment_to_parallelTasksFragment)
            R.id.view_handling_exception_btn -> navController!!.navigate(R.id.action_mainFragment_to_handlingExceptionFragment)
        }
    }
}