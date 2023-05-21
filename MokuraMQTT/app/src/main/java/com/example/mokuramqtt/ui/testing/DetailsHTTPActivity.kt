package com.example.mokuramqtt.ui.testing


import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityDetailsHttpBinding
import com.example.mokuramqtt.viewmodel.HTTPViewModel


class DetailsHTTPActivity : AppCompatActivity() {
    private lateinit var httpViewModel: HTTPViewModel
    private lateinit var binding: ActivityDetailsHttpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsHttpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()
        setupAction()

    }

    private fun setupAction() {
        val tableLayout = binding.tlData
        // Retrieve your data, assuming you have a list of objects with ID, timestamp sent, timestamp received

        httpViewModel.allData.observe(this) { data ->
            Log.d("HTTP_DB", data.toString())
            for (dataItem in data) {
                val row = TableRow(this)

                val idTextView = TextView(this)
                idTextView.setTextColor(resources.getColor(R.color.white))
                idTextView.text = dataItem.idHTTP.toString()
                idTextView.setPadding(30.dpToPx(), 0, 30.dpToPx(), 0) // Add left and right padding
                row.addView(idTextView)

                val packetSizeTextView = TextView(this)
                packetSizeTextView.setTextColor(resources.getColor(R.color.white))
                packetSizeTextView.text = dataItem.packetSize
                packetSizeTextView.setPadding(20.dpToPx(), 0, 20.dpToPx(), 0) // Add left and right padding
                row.addView(packetSizeTextView)


                val sentTextView = TextView(this)
                sentTextView.setTextColor(resources.getColor(R.color.white))
                sentTextView.text = dataItem.sentTimeStamp
                sentTextView.setPadding(30.dpToPx(), 0, 20.dpToPx(), 0) // Add left and right padding
                row.addView(sentTextView)

                val receivedTextView = TextView(this)
                receivedTextView.setTextColor(resources.getColor(R.color.white))
                receivedTextView.text = dataItem.receivedTimeStamp
                receivedTextView.setPadding(20.dpToPx(), 0, 20.dpToPx(), 0) // Add left and right padding
                row.addView(receivedTextView)

                val differenceTextView = TextView(this)
                differenceTextView.setTextColor(resources.getColor(R.color.white))
                differenceTextView.text = dataItem.timeDifference
                differenceTextView.setPadding(20.dpToPx(), 0, 20.dpToPx(), 0) // Add left and right padding
                row.addView(differenceTextView)

                val transmissionTextView = TextView(this)
                transmissionTextView.setTextColor(resources.getColor(R.color.white))
                transmissionTextView.text = dataItem.timeTransmission
                transmissionTextView.setPadding(20.dpToPx(), 0, 20.dpToPx(), 0) // Add left and right padding
                row.addView(transmissionTextView)

                tableLayout.addView(row)
            }
        }
    }

    // Extension function to convert dp to pixels
    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }


    private fun setupViewModel() {
        httpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[HTTPViewModel::class.java]

    }
}