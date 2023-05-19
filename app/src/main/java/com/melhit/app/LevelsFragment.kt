package com.melhit.app

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.melhit.app.databinding.FragmentLevelsBinding


class LevelsFragment : Fragment() {

    private lateinit var binding: FragmentLevelsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLevelsBinding.inflate(inflater,container,false)
        binding.textView2.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        val list = mutableListOf(binding.button,binding.button6,binding.button7,binding.button8,binding.button9,binding.button10,binding.button11,binding.button12,binding.button4,binding.button13)
        for(i in list.indices) {
            list[i].setOnClickListener {
                val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                navController.navigate(R.id.gameFragment, Bundle().apply { putInt("level",i) })
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        var level = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("level",0)
        val list = mutableListOf(binding.button,binding.button6,binding.button7,binding.button8,binding.button9,binding.button10,binding.button11,binding.button12,binding.button4,binding.button13)
        for(i in list.indices) {
            list[i].backgroundTintList = ColorStateList.valueOf(resources.getColor(if(level>=i) R.color.yel else R.color.dark))
            list[i].isEnabled = level>=i
        }
    }
}