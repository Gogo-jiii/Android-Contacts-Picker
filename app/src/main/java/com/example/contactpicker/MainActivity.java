package com.example.contactpicker;

import android.Manifest;
import android.annotation.SuppressLint;
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            Cursor cursor1 = null, cursor2;

            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    cursor1 = getContentResolver().query(uri, null, null, null, null);
                }

                if (cursor1 != null && cursor1.moveToFirst()) {
                    String contactId = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String idResults = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultHold = Integer.parseInt(idResults);

                    if (idResultHold == 1) {
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null
                        );

                        if (cursor2 != null) {
                            cursor2.moveToFirst();
                            String contactNumber = cursor2.getString(cursor2.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String name = cursor2.getString(cursor2.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                            Toast.makeText(MainActivity.this, "Name: " + name + "\n" + "Number: " + contactNumber, Toast.LENGTH_SHORT).show();
                            cursor2.close();
                        }
                    } else {
                        Toast.makeText(this, "No contact number found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    });
}