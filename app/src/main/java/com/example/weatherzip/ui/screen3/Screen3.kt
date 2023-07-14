package com.example.weatherzip.ui.screen3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherzip.R
import com.example.weatherzip.databinding.FragmentScreen3Binding


/**
 * A simple [Fragment] subclass.
 * Use the [Screen3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Screen3 : Fragment() {
 lateinit var binding:FragmentScreen3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScreen3Binding.inflate(inflater,container,false)
        val view =binding.root
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Screen3.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Screen3().apply {
                arguments = Bundle().apply {
                }
            }
    }
}