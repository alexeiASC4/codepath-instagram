package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.model.HomeActivity;
import com.example.instagram.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private PostAdapter mAdapter;
    private RecyclerView mPosts;
    ArrayList<Post> posts;
    public ParseUser filterForUser;
    private SwipeRefreshLayout swipeContainer;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mPosts = findViewById(R.id.rvPosts);
        posts=new ArrayList<>();

        bottomNavigationView= findViewById(R.id.bottom_navigation);

        //create the adapter
        mAdapter = new PostAdapter(this, posts);
        //create the data source

        //set the adapter on the recycler view

        mPosts.setAdapter(mAdapter);
        //set the layout manager on the recycler view
        mPosts.setLayoutManager(new LinearLayoutManager(this));

        queryPosts();

        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                posts.clear();
                mAdapter.clear();
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent = null;
                if(menuItem.getItemId()==R.id.action_home){
                    intent= new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                if(menuItem.getItemId()==R.id.action_compose){
                    intent= new Intent(ProfileActivity.this, ComposeActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_profile);

    }

    private void queryPosts(){
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.orderByDescending("createdAt");
        postQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        if(filterForUser != null) {
            postQuery.whereEqualTo("user", filterForUser);
        }
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e!=null){
                    Log.e("PostActivity", "Error with query");
                    e.printStackTrace();
                    return;
                }

                for (int i =0; i<objects.size(); i ++){
                    Post post = objects.get(i);
                    posts.add(post);
                    mAdapter.notifyItemInserted(posts.size());
                    Log.d("PostActivity", "Post: " + post.getDescription() + "username: " + post.getUser().getUsername());
                }

                swipeContainer.setRefreshing(false);
            }
        });
    }



}
