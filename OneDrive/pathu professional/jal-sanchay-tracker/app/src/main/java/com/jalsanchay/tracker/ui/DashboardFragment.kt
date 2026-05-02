package com.jalsanchay.tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.jalsanchay.tracker.R
import com.jalsanchay.tracker.databinding.FragmentDashboardBinding
import com.jalsanchay.tracker.models.RainfallEntry
import com.jalsanchay.tracker.models.UserProfile

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var userProfile: UserProfile? = null

    private var profileListener: ListenerRegistration? = null
    private var entriesListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        // ✅ Add Entry Button
        binding.btnAddEntry.setOnClickListener {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.dashboardFragment) {
                navController.navigate(R.id.action_dashboardFragment_to_entryFragment)
            }
        }

        // 🔥 NEW: Analytics Button
        binding.btnAnalytics.setOnClickListener {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.dashboardFragment) {
                navController.navigate(R.id.action_dashboard_to_analytics)
            }
        }
    }

    private fun loadData() {
        val userId = auth.currentUser?.uid ?: return

        profileListener = db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, e ->

                if (_binding == null) return@addSnapshotListener
                if (e != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    userProfile = snapshot.toObject(UserProfile::class.java)
                }
            }

        entriesListener = db.collection("rainfallEntries")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->

                if (_binding == null) return@addSnapshotListener
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    val entries = snapshot.toObjects(RainfallEntry::class.java)
                    calculateStats(entries)
                }
            }
    }

    private fun calculateStats(entries: List<RainfallEntry>) {

        if (_binding == null) return

        val totalWater = entries.sumOf { it.waterCollected }

        binding.totalWaterText.text = String.format("%,.0f", totalWater)

        val impactDays = (totalWater / 135).toInt()
        binding.impactText.text = "Equivalent to $impactDays days of usage"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        profileListener?.remove()
        entriesListener?.remove()

        _binding = null
    }
}