# Firebase Realtime Database Setup

## Database Rules

To enable querying by `userType`, you need to set up the following rules in your Firebase Realtime Database:

```json
{
  "rules": {
    "users": {
      ".indexOn": ["userType"],
      ".read": true,
      ".write": true
    },
    "products": {
      ".indexOn": ["vendorId", "status", "category"],
      ".read": true,
      ".write": true
    }
  }
}
```

## How to Set Up

1. **Open Firebase Console:** https://console.firebase.google.com
2. **Select your project:** Deshi-Store-Android
3. **Go to Realtime Database** (in left sidebar under "Build")
4. **Click the "Rules" tab**
5. **Replace the existing rules** with the JSON above
6. **Click "Publish"**

## Why This is Needed

The `.indexOn: ["userType"]` tells Firebase to create an index on the `userType` field, which allows the queries in the admin dashboard to work efficiently:

- `orderByChild("userType").equalTo("Company Vendor")`
- `orderByChild("userType").equalTo("Retail Vendor")`
- `orderByChild("userType").equalTo("User")`

Without this index, Firebase will show warnings in the logs and the queries may not return results.

## Verify Data Structure

After initializing accounts, your database should look like this:

```
users/
  ├── {userId1}/
  │   ├── userType: "Company Vendor"
  │   ├── companyName: "Akij Food & Beverage Ltd."
  │   ├── email: "contact@akijfood.com"
  │   ├── fullName: "Mohammed Akij"
  │   ├── phoneNumber: "+8801711111111"
  │   └── ...
  ├── {userId2}/
  │   ├── userType: "Retail Vendor"
  │   ├── shopName: "Dhaka Grocery Store"
  │   ├── email: "vendor1@findingbd.com"
  │   ├── ownerName: "Karim Rahman"
  │   └── ...
  └── ...
```

## Troubleshooting

### If vendors still don't appear after setup:

1. **Check Firebase Console:**
   - Go to Realtime Database → Data tab
   - Look for the "users" node
   - Verify that vendor accounts exist with `userType` field

2. **Check Android Logcat:**
   - Filter by "CompanyVendors" or "RetailVendors"
   - Look for log messages showing query results
   - Check for any Firebase errors

3. **Verify Rules are Published:**
   - Rules must be published to take effect
   - Make sure there are no syntax errors in the rules

4. **Re-initialize Accounts:**
   - In admin dashboard, click "Init Accounts" again
   - Check the toast messages to see if accounts are created or already exist
   - Wait 5 seconds, then click Refresh in each tab

### Common Issues:

- **"Permission denied"** - Database rules are too restrictive
- **"Index not defined"** - Need to add `.indexOn` for `userType`
- **Empty results** - Accounts may not have been created or `userType` field is missing
