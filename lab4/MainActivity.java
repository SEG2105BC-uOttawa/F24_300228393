package com.example.lab4profilemanager;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> avatarLauncher;
    private ImageView logoImageView;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //
        logoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetAvatarButton(v);
            }
        });
        //
        avatarLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    int avatarId = data.getIntExtra("avatarId", 0);
                    if (avatarId != 0) {
                        logoImageView.setImageResource(avatarId);
                    }
                }
            }
        });
        //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //
    public void OnOpenInGoogleMaps(View view) {
        EditText teamAddress = (EditText) findViewById(R.id.teamAddressField);
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + teamAddress.getText());
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }
    //
    public void onSetAvatarButton(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        avatarLauncher.launch(intent);
    }
    //
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Getting the Avatar Image we show to our users
        ImageView avatarImage = (ImageView) findViewById(R.id.logoImage);
        // Figuring out the correct image
        String drawableName = "avatar1";
        switch (data.getIntExtra("selectedImageId", R.id.logoImage01)) {
            case R.id.logoImage01:
                drawableName = "ic_logo_00";
                break;
            case R.id.logoImage02:
                drawableName = "ic_logo_01";
                break;
            case R.id.logoImage03:
                drawableName = "ic_logo_02";
                break;
            case R.id.logoImage04:
                drawableName = "ic_logo_03";
                break;
            case R.id.logoImage05:
                drawableName = "ic_logo_04";
                break;
            case R.id.logoImage00:
                drawableName = "ic_logo_05";
                break;
            default:
                drawableName = "ic_logo_00";
                break;
        }
        int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        avatarImage.setImageResource(resID);
    }
    //
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.lab3pm.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        }
    }
    //
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}