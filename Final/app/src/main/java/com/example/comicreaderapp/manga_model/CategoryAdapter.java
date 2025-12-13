package com.example.comicreaderapp.manga_model;

import android.util.Log; // <-- Add this import statement
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicreaderapp.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
    private final List<CategoryItem> data;
    private final OnClick listener;

    public interface OnClick { void onClick(CategoryItem c); }

    public CategoryAdapter(List<CategoryItem> data, OnClick l) {
        this.data = data;
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CategoryItem c = data.get(position);
        String label = (c.name != null && !c.name.isEmpty()) ? c.name : (c.id != null ? c.id : "");
        // This line will now work correctly
        Log.d("CategoryAdapter", "binding pos=" + position + " label='" + label + "'");
        holder.chip.setText(label);
        holder.chip.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        Chip chip;
        VH(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }
    }
}
