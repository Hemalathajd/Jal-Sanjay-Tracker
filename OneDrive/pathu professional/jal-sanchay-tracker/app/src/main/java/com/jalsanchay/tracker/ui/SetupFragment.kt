package com.jalsanchay.tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jalsanchay.tracker.R
import com.jalsanchay.tracker.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinish.setOnClickListener {

            val roofText = binding.etRoofArea.text.toString().trim()
            val tankText = binding.etTankCapacity.text.toString().trim()

            // ✅ Validation
            if (roofText.isEmpty() || tankText.isEmpty()) {
                Toast.makeText(context, "Please enter all values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val roofArea = roofText.toDouble()
            val tankCapacity = tankText.toDouble()

            val user = auth.currentUser

            if (user == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Data to save
            val userData = hashMapOf(
                "userId" to user.uid,
                "roofArea" to roofArea,
                "tankCapacity" to tankCapacity
            )

            // 🔥 Save to Firestore
            db.collection("users")
                .document(user.uid)
                .set(userData)
                .addOnSuccessListener {

                    Toast.makeText(context, "Setup Saved Successfully!", Toast.LENGTH_SHORT).show()

                    // ✅ Navigate to Dashboard
                    findNavController().navigate(R.id.action_setup_to_dashboard)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}