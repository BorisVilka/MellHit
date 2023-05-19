package com.melhit.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.melhit.app.databinding.FragmentGameBinding
import kotlin.math.max
import kotlin.math.min


class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putInt("tmp",requireArguments().getInt("level")).apply()
        binding = FragmentGameBinding.inflate(inflater,container,false)
        binding.game.level = requireArguments().getInt("level")
        binding.textView5.text = "Level: ${requireArguments().getInt("level")+1}"
        binding.button3.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        binding.imageView5.setOnClickListener {
            binding.pause.visibility = View.GONE
            binding.game.togglePause()
        }
        binding.imageView6.setOnClickListener {
            binding.game.togglePause()
            binding.pause.visibility = View.VISIBLE
        }
        binding.game.setEndListener(object : GameView.Companion.EndListener {
            override fun end() {
                if(binding.game.isWin) {
                    var level = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("level",0)
                    if( max(requireArguments().getInt("level")+1,level)<9) requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("level",
                        max(requireArguments().getInt("level")+1,level)
                        ).apply()
                    requireActivity().runOnUiThread {
                        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                        navController.popBackStack()
                        navController.navigate(R.id.winFragment,Bundle().apply {
                            putInt("level",requireArguments().getInt("level"))
                        })
                    }
                } else {
                    requireActivity().runOnUiThread {
                        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                        navController.popBackStack()
                        navController.navigate(R.id.endFragment,Bundle().apply {
                            putInt("level",requireArguments().getInt("level"))
                        })
                    }
                }
            }

            override fun score(score: Int) {

            }

        })
        return binding.root
    }


}