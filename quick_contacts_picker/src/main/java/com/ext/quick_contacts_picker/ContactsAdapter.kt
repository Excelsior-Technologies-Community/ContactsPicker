package com.ext.quick_contacts_picker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Adapter for displaying contacts in RecyclerView
 */
class ContactsAdapter(
    private var contacts: List<ContactModel>,
    private val onContactClick: (ContactModel) -> Unit,
    private val nameTextColor: Int,
    private val nameTextSize: Float,
    private val phoneTextColor: Int,
    private val phoneTextSize: Float,
    private val sectionHeaderBackground: Int,
    private val sectionHeaderTextColor: Int,
    private val sectionHeaderTextSize: Float,
    private val itemBackground: Int
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val phoneText: TextView = itemView.findViewById(R.id.phoneText)
        val sectionHeader: TextView = itemView.findViewById(R.id.sectionHeader)
        val itemContainer: View = itemView.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        // Show section header if first item or different letter from previous
        if (position == 0 || contact.getFirstLetter() != contacts[position - 1].getFirstLetter()) {
            holder.sectionHeader.visibility = View.VISIBLE
            holder.sectionHeader.text = contact.getFirstLetter()

            // Apply section header styling
            if (sectionHeaderBackground != 0) {
                holder.sectionHeader.setBackgroundResource(sectionHeaderBackground)
            }
            holder.sectionHeader.setTextColor(sectionHeaderTextColor)
            holder.sectionHeader.textSize =
                sectionHeaderTextSize / holder.itemView.resources.displayMetrics.scaledDensity
        } else {
            holder.sectionHeader.visibility = View.GONE
        }

        // Apply text styling
        holder.nameText.text = contact.name
        holder.nameText.setTextColor(nameTextColor)
        holder.nameText.textSize =
            nameTextSize / holder.itemView.resources.displayMetrics.scaledDensity

        holder.phoneText.text = contact.phoneNumber
        holder.phoneText.setTextColor(phoneTextColor)
        holder.phoneText.textSize =
            phoneTextSize / holder.itemView.resources.displayMetrics.scaledDensity

        // Apply item background
        if (itemBackground != 0) {
            holder.itemContainer.setBackgroundResource(itemBackground)
        }

        // Load profile image or show placeholder
        if (contact.photoUri != null) {
            Glide.with(holder.itemView.context)
                .load(contact.photoUri)
                .placeholder(R.drawable.ic_person_placeholder)
                .circleCrop()
                .into(holder.profileImage)
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_person_placeholder)
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    /**
     * Update the contacts list
     */
    fun updateContacts(newContacts: List<ContactModel>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}