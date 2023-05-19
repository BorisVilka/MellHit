package com.melhit.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.melhit.app.databinding.FragmentWinBinding
import kotlin.math.min


class WinFragment : Fragment() {

    private lateinit var binding: FragmentWinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWinBinding.inflate(inflater,container,false)
        var level = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("level",0)
        if(requireArguments().getInt("level")+1>=9) binding.button2.visibility= View.INVISIBLE
        binding.button2.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
            navController.navigate(R.id.gameFragment,Bundle().apply {
                putInt("level", min(requireArguments().getInt("level")+1,level))
            })
        }
        binding.textView2.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        return binding.root
    }


}