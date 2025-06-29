package com.example.diagnostic333

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DtcsFragment : Fragment() {

    private lateinit var database: AppDatabase
    private lateinit var dtcsMsgDao: DtcsMsgDao
    private lateinit var dtcsAdapter: DtcsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dtcsAdapter = DtcsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dtcs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.dtcsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = dtcsAdapter

        database = AppDatabase.getDatabase(requireContext())
        dtcsMsgDao = database.dtcsMsgDao()

//        // Insert sample data only if database is empty
//        lifecycleScope.launch {
//            val existingDtcs = dtcsMsgDao.getAll().first()
//            if (existingDtcs.isEmpty()) {
//                dtcsMsgDao.insertAll(
//                    listOf(
//                        DtcsMsg("P0300", "Random/Multiple Cylinder Misfire Detected"),
//                        DtcsMsg("P0420", "Catalyst System Efficiency Below Threshold"),
//                        DtcsMsg("P0171", "System Too Lean (Bank 1)")
//                    )
//                )
//            }
//        }

        // Observe data
        lifecycleScope.launch {
            dtcsMsgDao.getAll().collectLatest { dtcsList ->
                dtcsAdapter.submitList(dtcsList)
            }
        }
    }
}