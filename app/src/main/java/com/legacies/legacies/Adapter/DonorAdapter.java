package com.legacies.legacies.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.legacies.legacies.Object.DonorItem;
import com.legacies.legacies.R;

import java.util.ArrayList;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    ArrayList<DonorItem> arrayList;

    public DonorAdapter(ArrayList<DonorItem> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_donor,parent,false);
        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class DonorViewHolder extends RecyclerView.ViewHolder {

        TextView tvTempat, tvTanggal, tvJumlah;

        DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTempat = itemView.findViewById(R.id.tvTempat);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
        }

        void bind(int pos) {
            DonorItem item = arrayList.get(arrayList.size()-(pos+1));
            tvTempat.setText("Tempat: " + item.tempat);
            tvJumlah.setText("Jumlah: " + item.jumlah + "ml");
            tvTanggal.setText(item.tanggal);
        }
    }

}

