package com.somercelik.firebaseinstagram.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.somercelik.firebaseinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder> {
    private ArrayList<String> userEmailList;
    private ArrayList<String> userCommentList;
    private ArrayList<String> userImageList;

    public FeedRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> userCommentList, ArrayList<String> userImageList) {
        this.userEmailList = userEmailList;
        this.userCommentList = userCommentList;
        this.userImageList = userImageList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.userEmailTextView.setText(userEmailList.get(position));
        holder.commentTextView.setText(userCommentList.get(position));
        Picasso.get().load(userImageList.get(position)).into(holder.postImageView);
    }

    @Override
    public int getItemCount() {
        return userEmailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        TextView userEmailTextView, commentTextView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.recyclerView_row_postImageView);
            userEmailTextView = itemView.findViewById(R.id.recyclerView_row_userNameTextView);
            commentTextView = itemView.findViewById(R.id.recyclerView_row_commentTextView);
        }
    }


}
