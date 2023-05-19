package com.melhit.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.melhit.app.databinding.FragmentStartBinding

class StartFragment : Fragment() {


    private lateinit var binding: FragmentStartBinding

      override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
          binding = FragmentStartBinding.inflate(inflater,container,false)
          binding.textView.setOnClickListener {
                val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                navController.navigate(R.id.levelsFragment)
          }
          var music = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("music",false)
          var sound = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("sound",false)
          binding.switch1.isChecked = music
          binding.switch3.isChecked = sound
          binding.switch3.setOnCheckedChangeListener { buttonView, isChecked ->
              sound = ! sound
              requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putBoolean("sound",sound).apply()

          }
          binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
              music = ! music
              requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putBoolean("music",music).apply()
          }
          return binding.root
    }


}