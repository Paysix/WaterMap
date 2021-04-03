package com.example.android.fetchdata.recycleView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.fetchdata.R;
import com.example.android.fetchdata.dataBase.WPFEntity;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private List<WPFEntity> allWPF;

    public RecycleAdapter(List<WPFEntity> allWPF) {
        this.allWPF = allWPF;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView area;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name_of_list);
            area = (TextView) itemView.findViewById(R.id.area_of_list);
        }
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        holder.area.setText(allWPF.get(position).getArea());
        holder.name.setText(allWPF.get(position).getName() + "淨水廠");
    }

    @Override
    public int getItemCount() {
        return allWPF.size();
    }
}
