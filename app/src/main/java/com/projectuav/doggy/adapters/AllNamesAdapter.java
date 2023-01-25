package com.projectuav.doggy.adapters;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.projectuav.doggy.R;
import com.squareup.picasso.Picasso;

import java.util.Random;


public class AllNamesAdapter extends RecyclerView.Adapter<AllNamesAdapter.AllNamesViewHolder> {
    private AllNamesAdapter.OnItemClickListener listener;
    private String[] mDataset;
    String compare = "0";

    @Override
    public void onBindViewHolder(AllNamesViewHolder holder, int position) {
        String[] completeDataX = mDataset[position].split("-_-_-_-_-");

        if (completeDataX[1].equals("1")){
            holder.profilename.setText(completeDataX[0]);
            holder.profilename.setTextColor(Color.parseColor("#161A2C"));
            holder.cardColor.setBackgroundResource(R.drawable.button_shape_gold);
        }
        else{
            holder.profilename.setText(completeDataX[0]);
            holder.profilename.setTextColor(Color.parseColor("#6F87E6"));
            holder.cardColor.setBackgroundResource(R.drawable.button_shape_dark);
        }

    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public AllNamesAdapter(String[] mylavdaset) {
        mDataset = mylavdaset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AllNamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_names_card, parent, false);

        return new AllNamesViewHolder(itemView);

    }
    class AllNamesViewHolder extends RecyclerView.ViewHolder {
        TextView profilename;
        ConstraintLayout cardColor;


        public AllNamesViewHolder(View itemView) {
            super(itemView);
            profilename = itemView.findViewById(R.id.profile_user_name_following);
            cardColor = itemView.findViewById(R.id.cardColor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onListClick(position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {

        void onListClick(int position);
    }
    public void setOnListClickListener(AllNamesAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}