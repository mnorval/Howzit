package com.michaelnorval.howzit;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.michaelnorval.howzit.ui.Home.HomeFragment;

import java.util.List;

public class Contacts_Adapter extends RecyclerView.Adapter<Contacts_Adapter.MyViewHolder> {

    private List<Contact_Structure> contact__List;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.TextView_ContactName);
            genre = (TextView) view.findViewById(R.id.TextView_ContactNumber);
            year = (TextView) view.findViewById(R.id.TextView_ContactExtra);
        }



    }



    public Contacts_Adapter(List<Contact_Structure> contact__List) {
        this.contact__List = contact__List;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_fav_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact_Structure cs__ = contact__List.get(position);
        holder.title.setText(cs__.getContactName());
        holder.genre.setText(cs__.getContactNumber());
        holder.year.setText(cs__.getContactExtra());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked on item", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contact__List.size();
    }
}


