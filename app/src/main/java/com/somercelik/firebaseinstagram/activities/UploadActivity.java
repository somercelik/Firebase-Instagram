package com.somercelik.firebaseinstagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.somercelik.firebaseinstagram.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    Bitmap selectedImage;
    ImageView imageView;
    EditText commentEditText;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageUri;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.imageView);
        commentEditText = findViewById(R.id.commentEditText);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void selectImageOnClick(View view) {
        //Eğer kullanıcı önceden izin vermemişse
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        } else {                                                    //Eğer kullanıcı önceden izin vermişse galeriyi aç
            openGallery();
        }
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);    //Seçme aksiyonu yapılıp galeri açılacak olan intent
        startActivityForResult(galleryIntent, 2);    //Bir sonuç alacağımız intent 2 istem koduyla başlatıldı
    }

    //Verilen iznin sonucunda ne olacak?
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Başlatılan Activity sonucunda ne olacak?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source imageSource = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    selectedImage = ImageDecoder.decodeBitmap(imageSource);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                }
                imageView.setImageBitmap(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadButtonOnClick(View View) {
        if (imageUri != null) {
            //Başarılı olduğu durumda
            //Dosya ismi için bir uuid gerekiyor
            UUID uuid = UUID.randomUUID();
            String directoryName = "images";
            String imageName = uuid.toString() + ".jpg";
            final String firebaseObjectName = directoryName + "/" + imageName;
            storageReference.child(firebaseObjectName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Download url'yi alacağız
                    StorageReference downloadReference = FirebaseStorage.getInstance().getReference(firebaseObjectName);
                    downloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();        //Upload ettiğimiz resmin url'ini aldık
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();      //Şu an oturumu açık olan kullanıcıyı alıyoruz
                            String userEmail = firebaseUser.getEmail();
                            String comment = commentEditText.getText().toString();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("userEmail", userEmail);
                            postData.put("downloadUrl", downloadUrl);
                            postData.put("comment", comment);
                            postData.put("date", FieldValue.serverTimestamp());
                            //Database'e kayıt
                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intentAfterUpload = new Intent(UploadActivity.this, FeedActivity.class);
                                    intentAfterUpload.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intentAfterUpload);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {   //Başarısız olduğu durumda
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}