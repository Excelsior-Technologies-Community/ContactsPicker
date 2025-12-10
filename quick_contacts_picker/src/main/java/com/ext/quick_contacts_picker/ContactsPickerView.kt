package com.ext.quick_contacts_picker

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Custom view for picking contacts with search, fast scroll, and alphabet scroller
 */
class ContactsPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var searchView: SearchView
    private var recyclerView: RecyclerView
    private var alphabetScroller: AlphabetScrollerView
    private var emptyView: TextView

    private lateinit var adapter: ContactsAdapter
    private var allContacts: List<ContactModel> = emptyList()
    private var filteredContacts: List<ContactModel> = emptyList()

    private var onContactClickListener: ((ContactModel) -> Unit)? = null

    // Customizable attributes
    private var showSearchBar: Boolean = true
    private var showAlphabetScroller: Boolean = true
    private var searchHint: String = "Search contacts..."
    private var emptyMessage: String = "No contacts found"
    private var searchBarBackground: Int = 0
    private var searchTextColor: Int = 0
    private var searchTextSize: Float = 0f
    private var contactNameTextColor: Int = 0
    private var contactNameTextSize: Float = 0f
    private var contactPhoneTextColor: Int = 0
    private var contactPhoneTextSize: Float = 0f
    private var sectionHeaderBackground: Int = 0
    private var sectionHeaderTextColor: Int = 0
    private var sectionHeaderTextSize: Float = 0f
    private var itemBackground: Int = 0
    private var autoLoadContacts: Boolean = true

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_contacts_picker, this, true)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)
        alphabetScroller = findViewById(R.id.alphabetScroller)
        emptyView = findViewById(R.id.emptyView)

        // Read custom attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ContactsPickerView,
            0, 0
        ).apply {
            try {
                showSearchBar = getBoolean(R.styleable.ContactsPickerView_showSearchBar, true)
                showAlphabetScroller =
                    getBoolean(R.styleable.ContactsPickerView_showAlphabetScroller, true)
                searchHint =
                    getString(R.styleable.ContactsPickerView_searchHint) ?: "Search contacts..."
                emptyMessage =
                    getString(R.styleable.ContactsPickerView_emptyMessage) ?: "No contacts found"
                autoLoadContacts = getBoolean(R.styleable.ContactsPickerView_autoLoadContacts, true)

                // Search bar styling
                searchBarBackground =
                    getResourceId(R.styleable.ContactsPickerView_searchBarBackground, 0)
                searchTextColor = getColor(
                    R.styleable.ContactsPickerView_searchTextColor,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                searchTextSize = getDimension(
                    R.styleable.ContactsPickerView_searchTextSize,
                    16f * resources.displayMetrics.scaledDensity
                )

                // Contact item styling
                contactNameTextColor = getColor(
                    R.styleable.ContactsPickerView_contactNameTextColor,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                contactNameTextSize = getDimension(
                    R.styleable.ContactsPickerView_contactNameTextSize,
                    16f * resources.displayMetrics.scaledDensity
                )
                contactPhoneTextColor = getColor(
                    R.styleable.ContactsPickerView_contactPhoneTextColor,
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
                contactPhoneTextSize = getDimension(
                    R.styleable.ContactsPickerView_contactPhoneTextSize,
                    14f * resources.displayMetrics.scaledDensity
                )

                // Section header styling
                sectionHeaderBackground =
                    getResourceId(R.styleable.ContactsPickerView_sectionHeaderBackground, 0)
                sectionHeaderTextColor = getColor(
                    R.styleable.ContactsPickerView_sectionHeaderTextColor,
                    ContextCompat.getColor(context, R.color.section_header_text)
                )
                sectionHeaderTextSize = getDimension(
                    R.styleable.ContactsPickerView_sectionHeaderTextSize,
                    14f * resources.displayMetrics.scaledDensity
                )

                // Item background
                itemBackground = getResourceId(R.styleable.ContactsPickerView_itemBackground, 0)

            } finally {
                recycle()
            }
        }

        setupViews()

        // Auto-load contacts if enabled
        if (autoLoadContacts) {
            loadContactsAutomatically()
        }
    }

    private fun setupViews() {
        // Setup SearchView
        searchView.queryHint = searchHint
        searchView.visibility = if (showSearchBar) View.VISIBLE else View.GONE

        // Apply search bar styling
        if (searchBarBackground != 0) {
            searchView.setBackgroundResource(searchBarBackground)
        }

        val searchText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchText?.apply {
            setTextColor(searchTextColor)
            textSize = searchTextSize / resources.displayMetrics.scaledDensity
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText ?: "")
                return true
            }
        })

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ContactsAdapter(
            contacts = emptyList(),
            onContactClick = { contact ->
                onContactClickListener?.invoke(contact)
            },
            nameTextColor = contactNameTextColor,
            nameTextSize = contactNameTextSize,
            phoneTextColor = contactPhoneTextColor,
            phoneTextSize = contactPhoneTextSize,
            sectionHeaderBackground = sectionHeaderBackground,
            sectionHeaderTextColor = sectionHeaderTextColor,
            sectionHeaderTextSize = sectionHeaderTextSize,
            itemBackground = itemBackground
        )
        recyclerView.adapter = adapter

        // Setup Alphabet Scroller
        alphabetScroller.visibility = if (showAlphabetScroller) View.VISIBLE else View.GONE
        alphabetScroller.setOnLetterSelectedListener { letter ->
            scrollToLetter(letter)
        }

        // Listen to scroll events to highlight current letter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateCurrentLetter()
            }
        })

        // Empty view
        emptyView.text = emptyMessage
        emptyView.visibility = View.GONE
    }

    private fun loadContactsAutomatically() {
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            try {
                val contacts = ContactsHelper.fetchContacts(context)
                setContacts(contacts)
            } catch (e: Exception) {
                // Handle error - show empty state
                updateUI()
            }
        }
    }

    /**
     * Set the list of contacts to display
     */
    fun setContacts(contacts: List<ContactModel>) {
        allContacts = contacts.sortedBy { it.name }
        filteredContacts = allContacts
        updateUI()
    }

    /**
     * Filter contacts based on search query
     */
    private fun filterContacts(query: String) {
        filteredContacts = if (query.isEmpty()) {
            allContacts
        } else {
            allContacts.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        contact.phoneNumber.contains(query, ignoreCase = true)
            }
        }
        updateUI()
    }

    /**
     * Update UI with filtered contacts
     */
    private fun updateUI() {
        adapter.updateContacts(filteredContacts)

        if (filteredContacts.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            alphabetScroller.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            alphabetScroller.visibility = if (showAlphabetScroller) View.VISIBLE else View.GONE
        }
    }

    /**
     * Scroll to contacts starting with the given letter
     */
    private fun scrollToLetter(letter: String) {
        val position = filteredContacts.indexOfFirst {
            it.name.startsWith(letter, ignoreCase = true)
        }
        if (position != -1) {
            (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                position,
                0
            )
            alphabetScroller.setHighlightedLetter(letter)
        }
    }

    /**
     * Update highlighted letter based on current scroll position
     */
    private fun updateCurrentLetter() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        val firstVisiblePosition = layoutManager?.findFirstVisibleItemPosition() ?: return

        if (firstVisiblePosition >= 0 && firstVisiblePosition < filteredContacts.size) {
            val currentLetter = filteredContacts[firstVisiblePosition].getFirstLetter()
            alphabetScroller.setHighlightedLetter(currentLetter)
        }
    }

    /**
     * Set listener for contact click
     */
    fun setOnContactClickListener(listener: (ContactModel) -> Unit) {
        onContactClickListener = listener
    }

    /**
     * Manually reload contacts
     */
    fun reloadContacts() {
        loadContactsAutomatically()
    }
}