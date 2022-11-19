package com.nic.Thiruvannamalai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nic.Thiruvannamalai.ImageZoom.ImageMatrixTouchHandler;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.activity.ViewDetailsScreen;
import com.nic.Thiruvannamalai.databinding.SavedListItemViewBinding;
import com.nic.Thiruvannamalai.databinding.TaxItemViewBinding;
import com.nic.Thiruvannamalai.model.PublicTax;

import java.util.ArrayList;

public class SavedListViewAdapter extends RecyclerView.Adapter<SavedListViewAdapter.MyViewHolder> {
    private ArrayList<PublicTax> savedList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SavedListViewAdapter(ArrayList<PublicTax> savedList, Context context) {
        this.savedList = savedList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedListViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SavedListItemViewBinding savedListItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.saved_list_item_view, viewGroup, false);
        return new MyViewHolder(savedListItemViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedListViewAdapter.MyViewHolder holder, int position) {

        holder.savedListItemViewBinding.nameId.setText(savedList.get(position).getName());
        holder.savedListItemViewBinding.age.setText(savedList.get(position).getAge());
        holder.savedListItemViewBinding.location.setText(savedList.get(position).getLocation());
        holder.savedListItemViewBinding.mobile.setText(savedList.get(position).getMobile_no());
        holder.savedListItemViewBinding.slNo.setText(savedList.get(position).getSl_no());

        holder.savedListItemViewBinding.viewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDetailsScreen)context).viewImage(savedList.get(position).getTvm_deepa_festival_id());
            }
        });
        holder.savedListItemViewBinding.viewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDetailsScreen)context).viewPdf(savedList.get(position).getTvm_deepa_festival_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SavedListItemViewBinding savedListItemViewBinding;

        MyViewHolder(SavedListItemViewBinding Binding) {
            super(Binding.getRoot());
            savedListItemViewBinding = Binding;
        }
    }


}
