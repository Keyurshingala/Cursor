package com.example.cursor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cursor.databinding.RvContactBinding;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    Context context;
    List<Contact> list;
    ContactClickEvent contactClickEvent;

    public ContactAdapter(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<Contact> list) {
        this.list = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(RvContactBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RvContactBinding bind = holder.binding;

        Contact contact = list.get(position);

        bind.tvName.setText(contact.getName());
        bind.tvNum.setText(contact.getContact());

        bind.tvInvite.setOnClickListener(v -> contactClickEvent.onContactClick(contact, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RvContactBinding binding;

        public MyViewHolder(@NonNull RvContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setOnContactClickListener(ContactClickEvent contactClickEvent) {
        this.contactClickEvent = contactClickEvent;
    }

    public interface ContactClickEvent {
        void onContactClick(Contact contact, int pos);
    }

}
