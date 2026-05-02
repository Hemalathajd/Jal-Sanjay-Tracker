package com.jalsanchay.tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jalsanchay.tracker.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // 🔥 Check if user exists
        if (auth.currentUser == null) {

            // 🔥 Anonymous login
            auth.signInAnonymously()
                .addOnSuccessListener {
                    // After login → go to Setup
                    findNavController().navigate(R.id.action_splash_to_setup)
                }
                .addOnFailureListener {
                    // Even if fail → still go (to avoid block)
                    findNavController().navigate(R.id.action_splash_to_setup)
                }

        } else {
            // Already logged in → go to Dashboard
            findNavController().navigate(R.id.action_splash_to_dashboard)
        }
    }
}