package com.example.instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.instagram.model.HomeActivity;
import com.example.instagram.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ComposeActivity extends AppCompatActivity {

    //public static final String imagePath = "";

    private EditText mDescriptionInput;
    private Button mCreateButton;
    private Button mRefreshButton;
    private Button mLogoutButton;
    private Button mCameraButton;
    private ImageView mPreviewImage;

    private BottomNavigationView bottomNavigationView;

    File photoFile;

    public String photoFileName = "photo.jpg";
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView= findViewById(R.id.bottom_navigation);

        mDescriptionInput = findViewById(R.id.etDescription);
        mCreateButton = findViewById(R.id.btnCreate);
        mRefreshButton = findViewById(R.id.btnRefresh);
        mLogoutButton = findViewById(R.id.btnLogout);
        mCameraButton = findViewById(R.id.btnCamera);
        mPreviewImage = findViewById(R.id.ivPreview);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = mDescriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                if (photoFile != null) {
                    final File file = new File(photoFile.getAbsolutePath());
                    final ParseFile parseFile = new ParseFile(file);


                    createPost(description, parseFile, user);
                }else{
                    Toast.makeText(ComposeActivity.this, "No photo to submit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTopPosts();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                final Intent intent = new Intent(ComposeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent = null;
                if(menuItem.getItemId()==R.id.action_home){
                    intent= new Intent(ComposeActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId()==R.id.action_profile){
                    intent = new Intent(ComposeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_compose);
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ImageView ivPreview = findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(takenImage);

                File photoFile = getPhotoFileUri(photoFileName);

                ParseFile parseFile = new ParseFile(photoFile);
            } else { // Result was a failure
                //Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPost(final String description, final ParseFile imageFile, final ParseUser user) {
        imageFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    final Post newPost = new Post();
                    newPost.setDescription(description);
                    newPost.setImage(imageFile);
                    newPost.setUser(user);

                    newPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("HomeActivity", "Create post success!");
                                mDescriptionInput.setText("");
                                mPreviewImage.setImageResource(0);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    e.printStackTrace();
                    Toast.makeText(ComposeActivity.this, "Post not saved",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Log.d("HomeActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = (Uri) FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


}
