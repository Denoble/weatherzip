package com.example.weatherzip.ui.screen3

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.weatherzip.R
import com.example.weatherzip.databinding.FragmentScreen3Binding
import com.example.weatherzip.ui.LocationViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [Screen3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Screen3 : Fragment() {
 lateinit var binding:FragmentScreen3Binding
 val viewModel:LocationViewModel by activityViewModels()
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
        radioButtonCheckListener()
        observeRadioMetric()
        return view
    }
    fun radioButtonCheckListener(){
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                 R.id.radio_celcius ->{
                    viewModel.setTempMetric("Celcius")
                }
                R.id.radio_fahrenheit ->{
                    viewModel.setTempMetric("Fahrenheit")
                }else->{

                }
            }

        }
    }
    fun observeRadioMetric(){
        viewModel.tempMetric.observe(viewLifecycleOwner,{
            Log.i("Screen3 ",it )
            when(it.lowercase()){
                "fahrenheit" ->{
                    binding.radioGroup.check(
                        binding.radioGroup.getChildAt(1).id)
                }
                "celcius" ->{
                    binding.radioGroup.check(
                        binding.radioGroup.getChildAt(0).id)
                }else ->{}
            }
        })
    }
}