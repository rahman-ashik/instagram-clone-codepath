package com.codepath.instagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.codepath.instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;


public class PostActivity extends AppCompatActivity {
    private static final String APP_TAG = "HomeActivity";
    private ImageView ivPicture;
    private EditText etDescription;
    private Button btnPost;
    private Button btnUpload;

    public final static int CAMERA_REQUEST_CODE = 99;

    public String photoFileName = "image.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivPicture = findViewById(R.id.ivPicture);
        etDescription = findViewById(R.id.etDescription);
        btnPost = findViewById(R.id.btnPost);
        btnUpload = findViewById(R.id.btnUpload);

        ivPicture.setImageResource(R.drawable.camera_shadow_fill);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etDescription.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                final File file = photoFile;
                if(file == null) {
                    Toast.makeText(getContext(), "Must include a photo!", Toast.LENGTH_SHORT).show();
                } else {
                    final ParseFile parseFile = new ParseFile(file);

                    createPost(description, parseFile, user);
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

    }

    public void createPost(String description, ParseFile image, ParseUser user) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(image);
        newPost.setUser(user);
        newPost.addAll(Post.KEY_LIKED_BY, new ArrayList<ParseUser>());

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "Successful: posted", Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                    ivPicture.setImageResource(R.drawable.camera_shadow_fill);
                } else {
                    Toast.makeText(getContext(), "Failed: not posted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Context getContext() {
        return this;
    }


    public void onLaunchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create a photo directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivPicture.setImageBitmap(takenImage);
                viewHome();
            } else {
                Toast.makeText(getContext(), "No Image?!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void viewHome() {
        final Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

}
