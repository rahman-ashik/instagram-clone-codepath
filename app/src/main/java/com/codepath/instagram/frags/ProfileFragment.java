package com.codepath.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends com.codepath.instagram.HomeFragment {

    private Button btnLogout;
    private TextView tvUser;
    private ImageView ivProfile;
    private RecyclerView rvPosts;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        tvUser = (TextView) view.findViewById(R.id.tvCurrentUser);
        ivProfile = (ImageView) view.findViewById(R.id.ivCurrentProfile);
        tvUser.setText(ParseUser.getCurrentUser().getUsername());
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutAction();
            }
        });
        loadTopPosts();
        rvPosts = (RecyclerView) view.findViewById(R.id.rvUserPosts);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
        rvPosts.setAdapter(postAdapter);

    }

    public void onLogoutAction() {
        ParseUser.logOut();
        final Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void loadTopPosts() {

        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

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
                    Toast.makeText(getContext(), "Failed to query posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
