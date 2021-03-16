package com.codepath.instagram;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class PostFragment extends Fragment {
    private ImageView ivPicture;
    private EditText etDescription;
    private Button btnPost;
    private MenuItem miHome;

    public final String APP_TAG = "Instagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 999;
    public String photoFileName = "photo.jpg";
    File photoFile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_post, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ivPicture = view.findViewById(R.id.ivPutPicture);
        etDescription = view.findViewById(R.id.etWriteDescription);
        btnPost = view.findViewById(R.id.btnPost);
        miHome = view.findViewById(R.id.miHome);


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

        ivPicture.setOnClickListener(new View.OnClickListener() {
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


        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "Successfully posted", Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                    ivPicture.setImageResource(R.drawable.camera_shadow_fill);

                    //TODO: On a successful"post" it is scrambling the fragment view
                    restartActivity(getActivity()); // do some workaround rather than a reset

                } else {
                    Toast.makeText(getContext(), "Failed to make post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeAllFragments() {
        for (Fragment fragment : getFragmentManager().getFragments()) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void onLaunchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivPicture.setImageBitmap(takenImage);

            } else {
                Toast.makeText(getContext(), "No Picture!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void restartActivity(Activity A){
        Intent intent=new Intent();
        intent.setClass(A, A.getClass());
        A.startActivity(intent);
        A.finish();
    }

}
