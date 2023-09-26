package com.aryan.dinesathi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aryan.dinesathi.R

// TODO: Rename parameter arguments, choose names that match
class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the toolbar for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }
}