package com.nic.Thiruvannamalai.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

public class SavedListViewAdapter extends RecyclerView.Adapter<SavedListViewAdapter.MyViewHolder> implements Filterable {
    private ArrayList<PublicTax> savedList;
    private ArrayList<PublicTax> filtersavedList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SavedListViewAdapter(ArrayList<PublicTax> savedList, Context context) {
        this.savedList = savedList;
        this.filtersavedList = savedList;
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

        holder.savedListItemViewBinding.nameId.setText(filtersavedList.get(position).getName());
        holder.savedListItemViewBinding.age.setText(filtersavedList.get(position).getAge());
        holder.savedListItemViewBinding.location.setText(filtersavedList.get(position).getLocation());
        holder.savedListItemViewBinding.mobile.setText(filtersavedList.get(position).getMobile_no());
        holder.savedListItemViewBinding.slNo.setText(filtersavedList.get(position).getSl_no());

        holder.savedListItemViewBinding.viewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDetailsScreen)context).viewImage(filtersavedList.get(position).getTvm_deepa_festival_id());
            }
        });
        holder.savedListItemViewBinding.viewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewDetailsScreen)context).viewPdf(filtersavedList.get(position).getTvm_deepa_festival_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filtersavedList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtersavedList = savedList;
                } else {
                    ArrayList<PublicTax> filteredList = new ArrayList<>();
                    for (PublicTax row : savedList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (
                                row.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                        row.getAge().toLowerCase().contains(charString.toUpperCase())||
                                        row.getMobile_no().toLowerCase().contains(charString.toUpperCase())||
                                        row.getAadhar_no().toLowerCase().contains(charString.toUpperCase())
                        ) {
                            filteredList.add(row);
                        }
                    }

                    filtersavedList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtersavedList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtersavedList = (ArrayList<PublicTax>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SavedListItemViewBinding savedListItemViewBinding;

        MyViewHolder(SavedListItemViewBinding Binding) {
            super(Binding.getRoot());
            savedListItemViewBinding = Binding;
        }
    }


}
