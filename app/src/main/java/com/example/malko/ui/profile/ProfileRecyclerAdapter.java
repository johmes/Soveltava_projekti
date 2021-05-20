package com.example.malko.ui.profile;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malko.MainRecyclerAdapter;
import com.example.malko.Product;
import com.example.malko.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;
    private OnProductLongClickListener listener;


    public ProfileRecyclerAdapter (Context ct, List<Product> product){
        this.context = ct;
        this.productList = product;
    }

    @NonNull
    @Override
    public ProfileRecyclerAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ProfileRecyclerAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRecyclerAdapter.ProductViewHolder holder, int position) {
        Product product = this.productList.get(position);
        String location = product.getLocation() + " " + product.getDistanceTo() + " km";

        holder.textHeader.setText(product.getProductTitle());
        holder.textLocation.setText(location);
        holder.textAmount.setText((product.getAmount() + " kpl"));
        holder.textDate.setText(product.getDate());
        holder.textDescription.setText(product.getDescription());
        holder.textCategory.setText(product.getCategory());

        boolean isExpandable = productList.get(position).isExpandable();
        holder.constraintLayoutExpandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return this.productList.size();
    }

    public List<Product> getAll() {return productList;}

    class ProductViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayoutExpandable;
        ConstraintLayout constraintLayout;

        TextView textHeader;
        TextView textLocation;
        TextView textAmount;
        TextView textDate;
        TextView textDescription;
        TextView textCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textHeader = itemView.findViewById(R.id.recyclerview_row_header);
            textLocation = itemView.findViewById(R.id.recyclerview_row_location);
            textAmount = itemView.findViewById(R.id.recyclerview_row_amount);
            textDate = itemView.findViewById(R.id.recyclerview_row_date);
            textDescription = itemView.findViewById(R.id.recyclerview_row_description);
            textCategory = itemView.findViewById(R.id.recyclerview_row_category);

            // click on listener
            constraintLayout = itemView.findViewById(R.id.recyclerview_row);
            constraintLayoutExpandable = itemView.findViewById(R.id.recyclerview_row_expandable);

            constraintLayout.setOnClickListener(v -> {

                Product product = productList.get(getAdapterPosition());
                product.setExpandable(!product.isExpandable());
                notifyItemChanged(getAdapterPosition());

            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onProductLongClick(productList.get(position));
                }
                return false;
            });

        }
    }
    public interface OnProductLongClickListener {
        void onProductLongClick(Product product);
    }


    public void setOnProductLongClickListener(OnProductLongClickListener listener) {
        this.listener = listener;
    }
}
