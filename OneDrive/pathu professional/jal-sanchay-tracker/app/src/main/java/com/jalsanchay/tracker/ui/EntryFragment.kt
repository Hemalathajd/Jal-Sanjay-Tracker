package com.jalsanchay.tracker.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jalsanchay.tracker.R
import com.jalsanchay.tracker.databinding.FragmentEntryBinding
import java.util.*

class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 DEBUG: Check button click
        binding.btnSaveEntry.setOnClickListener {

            Log.d("ENTRY_DEBUG", "Button Clicked")

            val rainfallText = binding.etRainfall.text.toString().trim()

            if (rainfallText.isEmpty()) {
                Toast.makeText(context, "Enter rainfall value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rainfall = rainfallText.toDoubleOrNull()

            if (rainfall == null) {
                Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser

            if (user == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                Log.e("ENTRY_DEBUG", "User NULL")
                return@setOnClickListener
            }

            val waterCollected = rainfall * 10

            val entryData = hashMapOf(
                "userId" to user.uid,
                "rainfall" to rainfall,
                "waterCollected" to waterCollected,
                "date" to Date()
            )

            Log.d("ENTRY_DEBUG", "Saving to Firestore...")

            db.collection("rainfallEntries")
                .add(entryData)
                .addOnSuccessListener {

                    Log.d("ENTRY_DEBUG", "Saved Successfully")

                    Toast.makeText(context, "Entry Saved!", Toast.LENGTH_SHORT).show()

                    // Safe navigation
                    val navController = findNavController()
                    if (navController.currentDestination?.id == R.id.entryFragment) {
                        navController.navigate(R.id.action_entry_to_dashboard)
                    }
                }
                .addOnFailureListener { e ->

                    Log.e("ENTRY_DEBUG", "Error: ${e.message}")

                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}