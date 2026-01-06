package com.example.ai_fitness_workout_manager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ai_fitness_workout_manager.R

class PlaceholderFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_ICON = "icon"

        fun newInstance(title: String, message: String, iconResId: Int = R.drawable.ic_nav_home): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putInt(ARG_ICON, iconResId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE) ?: getString(R.string.coming_soon)
        val message = arguments?.getString(ARG_MESSAGE) ?: getString(R.string.coming_soon_desc)
        val iconResId = arguments?.getInt(ARG_ICON) ?: R.drawable.ic_nav_home

        view.findViewById<TextView>(R.id.tvPlaceholderTitle).text = title
        view.findViewById<TextView>(R.id.tvPlaceholderMessage).text = message
        view.findViewById<ImageView>(R.id.ivPlaceholderIcon).setImageResource(iconResId)
    }
}
