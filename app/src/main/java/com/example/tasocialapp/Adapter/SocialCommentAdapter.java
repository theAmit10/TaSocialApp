package com.example.tasocialapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasocialapp.Model.CommentModel;
import com.example.tasocialapp.Model.UserModel;
import com.example.tasocialapp.R;
import com.example.tasocialapp.databinding.SocialCommentSampleBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class SocialCommentAdapter extends RecyclerView.Adapter<SocialCommentAdapter.viewHolder> {


    Context context;
    ArrayList<CommentModel> list;

    public SocialCommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.social_comment_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel commentModel = list.get(position);
//        holder.binding.commentS.setText(commentModel.getCommentBody());

        // setting the commenting time
        String text = TimeAgo.using(commentModel.getCommentedAt());
        holder.binding.timeCS.setText(text);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(commentModel.getCommentedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(userModel.getProfilePhoto())
                                .placeholder(R.drawable.ic_no_image_found)
                                .into(holder.binding.profile2);
                        holder.binding.commentS.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" +" "  + commentModel.getCommentBody()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        SocialCommentSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SocialCommentSampleBinding.bind(itemView);
        }
    }
}
