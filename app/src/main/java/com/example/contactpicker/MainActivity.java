package com.example.contactpicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button btnPickContact;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPickContact = findViewById(R.id.btnPickContact);
        btnPickContact.setOnClickListener(v -> requestContactsPermission());

    }

    private void requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
        } else {
            pickContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.length > 0) {
            pickContact();
        }
    }

    private void pickContact() {
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activityResultLauncher.launch(pickContact);
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri contactUri = data.getData();

                                String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

                                Cursor cursor = null;
                                if (contactUri != null) {
                                    cursor = this.getContentResolver()
                                            .query(contactUri, queryFields, null, null, null);
                                }
                                try {
                                    if (cursor != null && cursor.getCount() == 0) return;

                                    if (cursor != null) {
                                        cursor.moveToFirst();
                                    }

                                    String name = null;
                                    if (cursor != null) {
                                        name = cursor.getString(0);
                                    }
                                    Toast.makeText(MainActivity.this, "Name: " + name, Toast.LENGTH_SHORT).show();
                                } finally {
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                }
                            }
                        }
                    });

}