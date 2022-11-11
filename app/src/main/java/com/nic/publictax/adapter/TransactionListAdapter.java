package com.nic.publictax.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.publictax.R;
import com.nic.publictax.databinding.TransactionItemViewBinding;
import com.nic.publictax.model.PublicTax;

import java.util.ArrayList;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {
    private ArrayList<PublicTax> transactionList;

    private Context context;
    private LayoutInflater layoutInflater;

    public TransactionListAdapter(Context context, ArrayList<PublicTax> transactionList) {
        this.context = context;
        this.transactionList = transactionList;


    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TransactionItemViewBinding binding;

        MyViewHolder(TransactionItemViewBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }
    }

    @Override
    public TransactionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        TransactionItemViewBinding transactionItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.transaction_item_view, viewGroup, false);
        return new MyViewHolder(transactionItemViewBinding);
    }

    @Override
    public void onBindViewHolder(TransactionListAdapter.MyViewHolder holder, final int position) {

        holder.binding.name.setText(transactionList.get(position).getTransactionName());
        holder.binding.date.setText(transactionList.get(position).getTransactionDate());
        holder.binding.status.setText(transactionList.get(position).getTransactionStatus());
        if(transactionList.get(position).getTransactionStatus().toLowerCase().contains("success")){
            holder.binding.statusImg.setImageDrawable(context.getResources().getDrawable(R.drawable.successful));
        }else if(transactionList.get(position).getTransactionStatus().toLowerCase().contains("pending")){
            holder.binding.statusImg.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
        }else if(transactionList.get(position).getTransactionStatus().toLowerCase().contains("fail")){
            holder.binding.statusImg.setImageDrawable(context.getResources().getDrawable(R.drawable.failed));
        }else if(transactionList.get(position).getTransactionStatus().toLowerCase().contains("cancel")){
            holder.binding.statusImg.setImageDrawable(context.getResources().getDrawable(R.drawable.cancelled));
        }


    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }
}
