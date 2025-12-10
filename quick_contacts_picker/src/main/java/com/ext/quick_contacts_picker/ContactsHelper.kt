package com.ext.quick_contacts_picker

import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper class to fetch contacts from device
 */
object ContactsHelper {

    /**
     * Fetch all contacts from device
     * Requires READ_CONTACTS permission
     */
    suspend fun fetchContacts(context: Context): List<ContactModel> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactModel>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val id = it.getString(idIndex) ?: continue
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex) ?: ""
                val photoUri = it.getString(photoIndex)

                contacts.add(
                    ContactModel(
                        id = id,
                        name = name,
                        phoneNumber = number,
                        photoUri = photoUri
                    )
                )
            }
        }

        // Remove duplicates based on contact ID
        contacts.distinctBy { it.id }
    }

    /**
     * Search contacts by query
     */
    suspend fun searchContacts(context: Context, query: String): List<ContactModel> =
        withContext(Dispatchers.IO) {
            val allContacts = fetchContacts(context)
            allContacts.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        contact.phoneNumber.contains(query, ignoreCase = true)
            }
        }
}