package com.ilham.etumarketbyseller.admin

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ilham.etumarketbyseller.databinding.FragmentGrafikBinding
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GrafikFragment : Fragment() {

    lateinit var binding : FragmentGrafikBinding
    lateinit var pref: SharedPreferences
    lateinit var adminVm : AdminViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGrafikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)

        val token = pref.getString("token", "").toString()

        adminVm.DatapendapatanToko(token)

        adminVm.datapendapatanToko.observe(viewLifecycleOwner, { data ->
            val list: ArrayList<BarEntry> = ArrayList()
            val sellerNames: ArrayList<String> = ArrayList()

            data.forEachIndexed { index, dataToko ->
                list.add(BarEntry(index.toFloat(), dataToko.totalPendapatan.toFloat()))
                sellerNames.add(dataToko.shopName)
            }

            val barDataSet = BarDataSet(list, "Total Pendapatan")
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
            barDataSet.valueTextColor = Color.GREEN

            val barData = BarData(barDataSet)

            binding.barChart.setFitBars(true)
            binding.barChart.data = barData
            binding.barChart.description.text = "Bar Chart"

            // Atur label pada sumbu horizontal
            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(sellerNames)
            binding.barChart.xAxis.granularity = 1f // untuk mencegah label berantakan
            binding.barChart.xAxis.setCenterAxisLabels(true)
            binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            binding.barChart.xAxis.setDrawGridLines(false)
            binding.barChart.xAxis.isGranularityEnabled = true

            binding.barChart.animateY(2000)
        })

//        adminVm.datapendapatanToko.observe(viewLifecycleOwner, { data ->
//            val entries: ArrayList<PieEntry> = ArrayList()
//            val colors: ArrayList<Int> = ArrayList()
//
//            data.forEach { dataToko ->
//                entries.add(PieEntry(dataToko.totalPendapatan.toFloat(), dataToko.sellerName))
//                // Tambahkan warna secara dinamis
//                val color = Color.argb(255, (Math.random() * 256).toInt(), (Math.random() * 256).toInt(), (Math.random() * 256).toInt())
//                colors.add(color)
//            }
//
//            val dataSet = PieDataSet(entries, "Total Pendapatan")
//            dataSet.colors = colors
//            dataSet.valueTextColor = Color.BLACK
//            dataSet.valueTextSize = 12f
//
//            val pieData = PieData(dataSet)
//
//            binding.pieChart.apply {
//                this.data = pieData
//                setEntryLabelTextSize(12f)
//                setEntryLabelColor(Color.BLACK)
//                description.isEnabled = false
//                legend.isEnabled = true
//                setUsePercentValues(false) // Tidak menggunakan persentase
//                setDrawEntryLabels(false)
//                animateY(1400)
//                invalidate()
//            }
//        })


    }
}