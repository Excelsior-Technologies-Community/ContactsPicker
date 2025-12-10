package com.ext.contactspicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ext.quick_contacts_picker.ContactsPickerView

class MainActivity : AppCompatActivity() {

    private lateinit var contactsPickerView: ContactsPickerView

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactsPickerView = findViewById(R.id.contactsPickerView)

        // Set contact click listener
        contactsPickerView.setOnContactClickListener { contact ->
            Toast.makeText(
                this,
                "Selected: ${contact.name}\n${contact.phoneNumber}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Check and request permission
        if (!checkContactsPermission()) {
            requestContactsPermission()
        } else {
            contactsPickerView.reloadContacts()
        }
    }

    private fun checkContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reload contacts
                contactsPickerView.reloadContacts()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Cannot load contacts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}