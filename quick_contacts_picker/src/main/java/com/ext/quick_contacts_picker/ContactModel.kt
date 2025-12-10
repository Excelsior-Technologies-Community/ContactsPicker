package com.ext.quick_contacts_picker

import android.os.Parcel
import android.os.Parcelable

/**
 * Data model representing a contact
 */
data class ContactModel(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    var isSelected: Boolean = false
) : Parcelable {

    /**
     * Get the first letter of the name for alphabet scrolling
     */
    fun getFirstLetter(): String {
        return if (name.isNotEmpty()) {
            name.first().uppercaseChar().toString()
        } else {
            "#"
        }
    }

    // Manual Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
        parcel.writeString(photoUri)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactModel> {
        override fun createFromParcel(parcel: Parcel): ContactModel {
            return ContactModel(parcel)
        }

        override fun newArray(size: Int): Array<ContactModel?> {
            return arrayOfNulls(size)
        }
    }
}