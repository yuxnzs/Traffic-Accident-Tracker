package com.example.myapplication

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// 繼承好後才會變 Adapter
// 放入 OneItem 型態資料        mList：接收外部的 data      繼承自 RecyclerView 內的 Adapter<VH>（ViewHolder，規定要定義，所以我們自己定義一個）
class MyRecyclerViewAdapter(val mList: ArrayList<OneItem>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    // 建立 ViewHolder；ItemView: View 為外面傳進來的 View； 繼承 RecyclerView的ViewHolder
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val vhDistrict: TextView = itemView.findViewById(R.id.ivDistrict)
        val vhPrecinct: TextView = itemView.findViewById(R.id.ivPrecinct)
        val vhStatus: TextView = itemView.findViewById(R.id.ivStatus)
        val vhInjured: TextView = itemView.findViewById(R.id.ivInjured)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.oneitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oneItem = mList[position]
        holder.vhDistrict.text = oneItem.iDistrict
        holder.vhPrecinct.text = oneItem.iPrecinct
        holder.vhStatus.text = oneItem.iStatus
        holder.vhInjured.text = oneItem.iInjured
        if (Integer.parseInt(holder.vhInjured.text.toString()) > 0) {
            holder.vhInjured.setTextColor(Color.RED)
        } else {
            holder.vhInjured.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).setTitle(holder.vhDistrict.text)
                .setMessage("管轄分局：${oneItem.iPrecinct}\n" +
                            "事故原因：${oneItem.iStatus}\n" +
                            "受傷人數：${oneItem.iInjured}\n" +
                            "死亡人數：${oneItem.iDeath}\n")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}