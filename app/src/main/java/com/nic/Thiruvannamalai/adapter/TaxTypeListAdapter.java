package com.nic.Thiruvannamalai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.databinding.TaxItemViewBinding;
import com.nic.Thiruvannamalai.model.PublicTax;

import java.util.ArrayList;

public class TaxTypeListAdapter extends RecyclerView.Adapter<TaxTypeListAdapter.MyViewHolder> {
    private ArrayList<PublicTax> taxTypeList;
    private ArrayList<Integer> taxImageList;
    private Context context;
    private LayoutInflater layoutInflater;

    public TaxTypeListAdapter(Context context, ArrayList<PublicTax> taxTypeList,ArrayList<Integer> taxImageList) {
        this.context = context;
        this.taxTypeList = taxTypeList;
        this.taxImageList = taxImageList;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TaxItemViewBinding taxItemViewBinding;

        MyViewHolder(TaxItemViewBinding Binding) {
            super(Binding.getRoot());
            taxItemViewBinding = Binding;
        }
    }

    @Override
    public TaxTypeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        TaxItemViewBinding taxItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.tax_item_view, viewGroup, false);
        return new MyViewHolder(taxItemViewBinding);
    }

    @Override
    public void onBindViewHolder(TaxTypeListAdapter.MyViewHolder holder, final int position) {

        holder.taxItemViewBinding.taxName.setText(taxTypeList.get(position).getTaxtypedesc_en());
        holder.taxItemViewBinding.taxImg.setImageResource(taxImageList.get(position));

    }

    @Override
    public int getItemCount() {
        return taxTypeList == null ? 0 : taxTypeList.size();
    }
}
