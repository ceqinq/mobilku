package com.legacies.legacies.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.legacies.legacies.Object.ListProfilItem;
import com.legacies.legacies.R;

import java.util.ArrayList;


public class ListProfilAdapter extends RecyclerView.Adapter<ListProfilAdapter.ListProfilViewHolder>{

    ArrayList<ListProfilItem> arrayList;
    private onItemClickListener mItemClickListener;

    public interface onItemClickListener {
        void onItemClickListener(View view, int position, ListProfilItem item);
    }

    public ListProfilAdapter(ArrayList<ListProfilItem> arrayList) {
        this.arrayList = arrayList;
    }

    public void setOnItemClickListener(onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ListProfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_profil_item, parent, false);
        return new ListProfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProfilViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    class ListProfilViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel, tvValue;

        ListProfilViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvValue = itemView.findViewById(R.id.tvValue);
        }

        void bind() {
            final int position = getAdapterPosition();

            ListProfilItem data = arrayList.get(position);
            tvLabel.setText(data.label);
            tvValue.setText(data.value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickListener(view, getAdapterPosition(), arrayList.get(position));
                    }
                }
            });
        }

    }

}
