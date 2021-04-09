package com.example.malko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;

    public MainRecyclerAdapter (Context ct, List<Product> product){
        this.context = ct;
        this.productList = product;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = this.productList.get(position);
        String location = product.getLocation() + " " + product.getDistanceTo() + " km";

        holder.textHeader.setText(product.getProductTitle());
        holder.textLocation.setText(location);
        holder.textAmount.setText((product.getAmount() + " kpl"));
    }

    @Override
    public int getItemCount() {
        return this.productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textHeader;
        TextView textLocation;
        TextView textAmount;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.recyclerview_row_header);
            textLocation = itemView.findViewById(R.id.recyclerview_row_location);
            textAmount = itemView.findViewById(R.id.recyclerview_row_amount);
        }
    }
}
