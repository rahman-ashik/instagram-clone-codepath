package com.codepath.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.instagram.model.Post;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        //get data according to position
        final Post post = posts.get(i);
        //populate views according to data
        viewHolder.tvUsername.setText(post.getUser().getUsername());
        viewHolder.tvUser.setText(post.getUser().getUsername());
        viewHolder.tvDescription.setText(post.getDescription());


        Glide.with(context)
                .load(post.getImage().getUrl())
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                .into(viewHolder.ivImage);

        String time= post.getUser().getCreatedAt().toString();
        viewHolder.tvRelativeTimestamp.setText(post.getRelativeTime(time));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }



    //for each row pass ViewHolder class

    //bind values based on position of element

    //create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvUser;
        public TextView tvDescription;
        public TextView tvRelativeTimestamp;
        public ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            ivProfileImage = (ImageView) view.findViewById(R.id.ivUserProfile);
            tvUsername = (TextView) view.findViewById(R.id.tvUser);
            tvUser = (TextView) view.findViewById(R.id.tvPostUser);
            tvDescription = (TextView) view.findViewById(R.id.tvPostDescription);
            tvRelativeTimestamp = (TextView) view.findViewById(R.id.tvDate);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Post post = posts.get(position);
            Toast.makeText(context, post.getDescription(), Toast.LENGTH_SHORT).show();
        }

    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

}
