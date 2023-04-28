package com.example.mokuramqtt.ui.monitoring

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.R

class PairAdapter(private val context: Context, private val listPairedDevice: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<PairAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_paireddevices,parent,false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder:ListViewHolder, position: Int) {
        val device = listPairedDevice[position]

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                Constants.REQUEST_ENABLE_BLUETOOTH
            )
        }
        holder.tvDeviceName.text = device.name.toString()
        holder.tvMacAddress.text = device.address.toString()

        holder.itemView.setOnClickListener{
            onItemClickCallback.onItemClicked(listPairedDevice[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listPairedDevice.size
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDeviceName: TextView = itemView.findViewById(R.id.tv_device)
        var tvMacAddress: TextView = itemView.findViewById(R.id.tv_mac)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: BluetoothDevice)
    }
}


