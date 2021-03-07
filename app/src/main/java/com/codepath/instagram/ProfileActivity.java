package com.codepath.instagram;

import android.content.Intent;
import android.graphics.drawable.DrawableWrapper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Button btnLogout;
    ImageView ivCurrentProfile;
    TextView tvCurrentUser;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    RecyclerView rvPosts;
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogout = findViewById(R.id.btnLogout);
        ivCurrentProfile= findViewById(R.id.ivCurrentProfile);
        tvCurrentUser= findViewById(R.id.tvCurrentUser);
        rvPosts= findViewById(R.id.rvUserPosts);
        floatingActionButton= findViewById(R.id.floatingActionButton);

        tvCurrentUser.setText(ParseUser.getCurrentUser().getUsername());


        postAdapter = new PostAdapter(posts);


        //String mediaUrl = post.getImage().getUrl();
        JSONObject pic = ParseUser.getCurrentUser().getJSONObject("ProfilePic");

//        ivCurrentProfile.setImageResource(xxxxx);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUserOut();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCreatePostActivity();
            }
        });

    }

    private void gotoCreatePostActivity() {
        final Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    private void logUserOut() {
            ParseUser.logOut();
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadTopPosts();
        //find RecyclerView
        rvPosts = (RecyclerView) view.findViewById(R.id.rvPost);
        //init arraylist
        posts = new ArrayList<>();
        //construct adapter
        postAdapter = new PostAdapter(posts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(linearLayoutManager);
        scrollListener = new com.codepath.instagram.EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
        //set the adapter
        rvPosts.setAdapter(postAdapter);
        //set swipe refresh layout


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTopPosts();
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void loadNextData(int page) {
        loadTopPosts();
    }

    public void loadTopPosts() {

        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.addDescendingOrder(Post.KEY_DATE);

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e == null) {
                    postAdapter.clear();
                    for(int i = 0; i < objects.size(); i++) {
                        posts.add(objects.get(i));
                        postAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this , "Failed to query posts", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }




    }