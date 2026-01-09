# Quick Setup: Firebase Storage (Free Plan)

## ✅ What's Already Done:
- ✅ Glide library added for image loading
- ✅ ProductAdapter configured to load images
- ✅ CategoryAdapter configured to load images
- ✅ Sample placeholder images added to test
- ✅ ImageUploadHelper class created for uploads

## 🚀 Three Options to Add Images (All FREE):

### **Option 1: Use Placeholder URLs (Current Setup)**
Your app now uses placeholder images. Run the app to see them working!

```
Products: https://via.placeholder.com/300x300.png?text=Product+Name
Categories: https://via.placeholder.com/300x200.png?text=Category+Name
```

### **Option 2: Use Real Public URLs (Recommended for Testing)**

Replace placeholder URLs with real image URLs:

**Free Image Sources:**
1. **Imgur** - Upload your images → Get direct link
2. **Unsplash** - Free stock photos
   - Example: `https://images.unsplash.com/photo-xxxxx?w=400`
3. **Google Images** - Right-click → "Open image in new tab" → Copy URL

**How to Update:**
```java
// In HomeActivity.java or other activities
productList.add(new Product(
    "1",
    "Mojo",
    "Soft Drink",
    25.0,
    "250ml",
    "YOUR_REAL_IMAGE_URL_HERE",  // Replace this
    "Manufacturer",
    0,
    false
));
```

### **Option 3: Firebase Storage (For Production)**

#### Step 1: Enable Firebase Storage
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project: **deshi-store-android**
3. Click **Storage** in left sidebar
4. Click **Get Started**
5. Choose **"Start in test mode"**
6. Click **Done**

#### Step 2: Update Storage Rules (Development Mode)
In Firebase Console → Storage → Rules tab:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if true;  // Anyone can read
      allow write: if request.auth != null;  // Only authenticated users can write
    }
  }
}
```

Click **Publish** to save rules.

#### Step 3: Upload Images via Firebase Console
1. Go to Storage → Files tab
2. Create folders: `products/` and `categories/`
3. Click **Upload file** to add images
4. After upload, click the image
5. Copy the **"Download URL"**
6. Use this URL in your Product/Category objects

#### Step 4: Or Upload via App (Optional)
Use `AddProductActivity.java` to upload images from the app:

```java
ImageUploadHelper imageHelper = new ImageUploadHelper();

imageHelper.uploadProductImage(imageUri, context, new ImageUploadHelper.ImageUploadCallback() {
    @Override
    public void onSuccess(String downloadUrl) {
        // Use this URL for your product
        product.setImageUrl(downloadUrl);
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
    }
});
```

## 📊 Free Tier Limits (Spark Plan):
- ✅ **5 GB** storage (≈ 5,000 product images)
- ✅ **1 GB/day** downloads
- ✅ **50,000 downloads/day**

**This is MORE than enough for a small-medium store app!**

## 🎯 Recommended Workflow:

1. **Testing Phase** (Now):
   - Use placeholder images (already set up!)
   - OR use public URLs from Imgur/Unsplash
   
2. **Development Phase**:
   - Upload images to Firebase Storage manually
   - Copy download URLs to your code
   
3. **Production Phase**:
   - Implement image upload feature (use `AddProductActivity`)
   - Store URLs in Firebase Database/Firestore

## 🧪 Test Your Setup:

1. Run your app
2. Navigate to Home or Categories
3. You should see placeholder images loading
4. Images appear = ✅ Everything works!

## 📝 Example: Add Real Images

### Products (HomeActivity.java):
```java
productList.add(new Product(
    "1",
    "Coca Cola",
    "Beverages",
    50.0,
    "bottle",
    "https://i.imgur.com/YourImageId.jpg",  // Upload to Imgur
    "Coca Cola",
    0,
    false
));
```

### Categories (ProductCategoriesActivity.java):
```java
categoryList.add(new Category(
    "1",
    "Beverages",
    "Drinks and juices",
    "https://i.imgur.com/AnotherImageId.jpg"  // Upload to Imgur
));
```

## 💡 Pro Tips:

- **Optimize images** before uploading (resize to 800x800px max)
- **Use JPG** for photos, **PNG** for logos/graphics
- **Compress images** with TinyPNG.com to reduce size
- **Glide caches automatically** - images load faster on subsequent views!

## ❓ Need Help?

Check [FIREBASE_IMAGE_GUIDE.md](./FIREBASE_IMAGE_GUIDE.md) for detailed documentation.

---

**Your app now supports images with Firebase Free Plan! 🎉**
