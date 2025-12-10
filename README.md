[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-21%2B-orange.svg)](#)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](#)

**Contacts Picker** is a modern, lightweight, and highly customizable **Android Contacts Picker** with built-in search, alphabet fast-scroll, big letter popup, and smooth section headers — just like WhatsApp, Google Contacts, and Telegram.

Zero dependencies • Pure Kotlin • Material Design ready

---

## Preview



---

## Features

- **Real-time Search** – Instant filtering by name or phone  
- **Alphabet Fast Scroller** – Side index bar with smooth scroll  
- **Big Letter Popup** – Large floating letter on touch (like native Contacts app)  
- **Section Headers** – A, B, C... sticky headers  
- **Circular Avatars** – Powered by Glide (with placeholder support)  
- **Fully Customizable** – Colors, sizes, backgrounds, icons via XML  
- **Auto Load Contacts** – Just drop in, works out of the box  
- **No External Dependencies** – Lightweight & fast  
- **Runtime Reload** – Call `reloadContacts()` anytime  
- **Permission Handling Ready** – Easy integration  

---

## Installation

**Step 1.** Add JitPack to your root `build.gradle` (or `settings.gradle`):

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency:

```gradle
dependencies {
    implementation 'com.github.YourUsername:AlphaPick:1.0.0'
}
```

> Replace `YourUsername` with your actual GitHub username

---

## Usage

### 1. Add to your layout

```xml
<com.ext.quick_contacts_picker.ContactsPickerView
    android:id="@+id/contactsPickerView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"
    app:autoLoadContacts="true"
    app:showSearchBar="true"
    app:showAlphabetScroller="true"
    app:searchHint="Search contacts..."
    app:contactNameTextColor="#212121"
    app:contactNameTextSize="16sp"
    app:contactPhoneTextColor="#757575"
    app:contactPhoneTextSize="14sp"
    app:emptyMessage="No contacts found"
    app:itemBackground="@drawable/contact_item_ripple"
    app:searchBarBackground="@drawable/search_background"
    app:sectionHeaderBackground="@color/section_header_bg"
    app:sectionHeaderTextColor="#616161" />
```

### 2. In your Activity / Fragment

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contactsPicker = findViewById<ContactsPickerView>(R.id.contactsPickerView)

        contactsPicker.setOnContactClickListener { contact ->
            Toast.makeText(
                this,
                "Selected: ${contact.name}\n${contact.phoneNumber}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Manually reload (e.g., after permission granted)
        // contactsPicker.reloadContacts()
    }
}
```

---

## XML Attributes

| Attribute                        | Type           | Default        | Description                              |
|----------------------------------|----------------|----------------|------------------------------------------|
| `showSearchBar`                  | boolean        | true           | Show/hide search bar                     |
| `showAlphabetScroller`           | boolean        | true           | Show/hide side alphabet bar              |
| `searchHint`                     | string         | "Search..."    | Hint text in search field                |
| `emptyMessage`                   | string         | "No contacts"  | Text when list is empty                  |
| `autoLoadContacts`               | boolean        | true           | Load contacts on view creation           |
| `contactNameTextColor`           | color          | black          | Contact name color                       |
| `contactNameTextSize`            | dimension      | 16sp           | Name text size                           |
| `contactPhoneTextColor`          | color          | gray           | Phone number color                       |
| `contactPhoneTextSize`           | dimension      | 14sp           | Phone text size                          |
| `searchBarBackground`            | reference/color| -              | Search bar background                    |
| `searchTextColor`                | color          | black          | Search input text color                  |
| `searchTextSize`                 | dimension      | 16sp           | Search text size                         |
| `sectionHeaderBackground`        | reference/color| -              | A/B/C header background                  |
| `sectionHeaderTextColor`         | color          | gray           | Section header text color                |
| `sectionHeaderTextSize`          | dimension      | 14sp           | Header text size                         |
| `itemBackground`                 | reference      | none           | Background/ripple for each contact item  |

---

## Required Permission

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
```

Handle runtime permission on Android 6.0+ (API 23+)

---

## Methods

```kotlin
// Set custom contact list (instead of auto-load)
contactsPickerView.setContacts(listOf<ContactModel>())

// Manually reload contacts (after permission)
contactsPickerView.reloadContacts()

// Set click listener
contactsPickerView.setOnContactClickListener { contact -> ... }
```

---

## 📄 License
 
```
MIT License
 
Copyright (c) 2025 Excelsior Technologies
 
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
 
---
