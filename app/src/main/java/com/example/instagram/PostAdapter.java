package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List <Post> mPosts;
    private Context context;


    public PostAdapter(Context context, List<Post> posts){
        mPosts = posts;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflator = LayoutInflater.from(context);
        View instaView = inflator.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(instaView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        //populate the view
        holder.mUsername.setText(post.getUser().getUsername());
        holder.mCaption.setText(post.getDescription());

        holder.mDate.setText(getRelativeTimeAgo(post.getCreatedAt()));

        ParseFile image = post.getImage();
        if (image!=null){
            String imageUrl = image.getUrl();
            Glide.with(context).load(imageUrl).into(holder.mImage);

            holder.mImage.isShown();
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImage;
        public TextView mUsername;
        public TextView mCaption;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.tvUsername);
            mCaption = itemView.findViewById(R.id.tvCaption);
            mImage = itemView.findViewById(R.id.ivPicture);
            mDate = itemView.findViewById(R.id.tvDate);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent i = new Intent(context, DetailActivity.class);
            i.putExtra("post", mPosts.get(position));
            context.startActivity(i);
        }
    }

    public static String getRelativeTimeAgo(Date date) {
        String relativeDate = "";
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        return relativeDate;
    }

}
