package com.codepath.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final FragmentManager fragmentManager= getSupportFragmentManager();

    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Post> posts;
    PostAdapter postAdapter;
    RecyclerView rvPosts;
    BottomNavigationView navBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new com.codepath.instagram.HomeFragment()).commit();

        navBar= findViewById(R.id.navBar);




        postAdapter = new PostAdapter(posts);

        JSONObject pic = ParseUser.getCurrentUser().getJSONObject("ProfilePic");



        navBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.miHome:
                                fragment = new com.codepath.instagram.HomeFragment();
                                break;
                            case R.id.miAddPost:
                                fragment = new com.codepath.instagram.PostFragment();
                                break;
                            case R.id.miProfile:
                                fragment = new com.codepath.instagram.ProfileFragment();
                                break;
                            default:
                                fragment = new com.codepath.instagram.HomeFragment();
                                break;
                        }

                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                        return true;
                    }
                });

        navBar.setSelectedItemId(R.id.miHome);
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
        rvPosts = (RecyclerView) view.findViewById(R.id.rvUserPosts);
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
                    Toast.makeText(MainActivity.this , "Failed to query posts", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    }