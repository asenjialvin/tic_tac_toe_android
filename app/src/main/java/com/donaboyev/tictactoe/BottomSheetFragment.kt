package com.donaboyev.tictactoe

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.donaboyev.tictactoe.Util.KEY_MODE
import com.donaboyev.tictactoe.Util.NIGHT_MODE
import com.donaboyev.tictactoe.Util.SHARED_PREF_MODE
import com.donaboyev.tictactoe.databinding.BottomSheetOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOptionsBinding? = null
    private val binding: BottomSheetOptionsBinding
        get() = _binding!!
    private var prefs: SharedPreferences? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mListener: ItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOptionsBinding.inflate(inflater, container, false)
        prefs = requireContext().getSharedPreferences(SHARED_PREF_MODE, MODE_PRIVATE)
        when (Mode.fromInteger(prefs!!.getInt(KEY_MODE, 0))) {
            Mode.EASY -> binding.rbEasy.isChecked = true
            Mode.MEDIUM -> binding.rbMedium.isChecked = true
            Mode.HARD -> binding.rbHard.isChecked = true
            Mode.TWO_PLAYERS -> binding.rbTwoPlayers.isChecked = true
        }
        binding.switchDayNight.isChecked = prefs?.getBoolean(NIGHT_MODE, false)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.rbEasy.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(Mode.EASY)
            binding.rbEasy.isChecked = true
        }
        binding.rbMedium.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(Mode.MEDIUM)
            binding.rbMedium.isChecked = true
        }
        binding.rbHard.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(Mode.HARD)
            binding.rbHard.isChecked = true
        }
        binding.rbTwoPlayers.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(Mode.TWO_PLAYERS)
            binding.rbTwoPlayers.isChecked = true
        }
        binding.switchDayNight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs?.edit()?.putBoolean(NIGHT_MODE, true)?.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs?.edit()?.putBoolean(NIGHT_MODE, false)?.apply()
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(mode: Mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}