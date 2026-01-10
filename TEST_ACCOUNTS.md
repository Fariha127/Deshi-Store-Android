# Test Accounts for Deshi Store Android App

## Admin Account
- **Email:** `admin@findingbd.com`
- **Password:** `admin123`
- **Access:** Admin Dashboard with full management capabilities

---

## Company Vendor Accounts

### 1. Akij Food & Beverage Ltd.
- **Email:** `contact@akijfood.com`
- **Password:** `password123`
- **Company:** Akij Food & Beverage Ltd.
- **Contact Person:** Mohammed Akij
- **Designation:** Business Manager
- **Phone:** +8801711111111
- **Registration:** REG-2024-001
- **BSTI Number:** BSTI-10001
- **TIN:** TIN-123456001
- **Address:** Akij House, 198 Bir Uttam Mir Shawkat Sarak, Dhaka

### 2. Anfords Bangladesh Ltd.
- **Email:** `contact@anfords.com`
- **Password:** `password123`
- **Company:** Anfords Bangladesh Ltd.
- **Contact Person:** Rashid Ahmed
- **Designation:** Sales Director
- **Phone:** +8801722222222
- **Registration:** REG-2024-002
- **BSTI Number:** BSTI-10002
- **TIN:** TIN-123456002
- **Address:** House 45, Road 10, Sector 4, Uttara, Dhaka

### 3. Square Toiletries Ltd.
- **Email:** `contact@squaretoiletries.com`
- **Password:** `password123`
- **Company:** Square Toiletries Ltd.
- **Contact Person:** Tahmina Begum
- **Designation:** Marketing Manager
- **Phone:** +8801733333333
- **Registration:** REG-2024-003
- **BSTI Number:** BSTI-10003
- **TIN:** TIN-123456003
- **Address:** Square Centre, 48 Mohakhali C/A, Dhaka

### 4. Sajeeb Group
- **Email:** `contact@sajeeb.com`
- **Password:** `password123`
- **Company:** Sajeeb Group
- **Contact Person:** Sajeeb Wazed
- **Designation:** Managing Director
- **Phone:** +8801744444444
- **Registration:** REG-2024-004
- **BSTI Number:** BSTI-10004
- **TIN:** TIN-123456004
- **Address:** Gulshan Avenue, Dhaka

### 5. PRAN-RFL Group
- **Email:** `contact@pranrfl.com`
- **Password:** `password123`
- **Company:** PRAN-RFL Group
- **Contact Person:** Ahsan Khan
- **Designation:** Regional Manager
- **Phone:** +8801755555555
- **Registration:** REG-2024-005
- **BSTI Number:** BSTI-10005
- **TIN:** TIN-123456005
- **Address:** PRAN Centre, Narsingdi

### 6. Square Food & Beverage Limited
- **Email:** `contact@squarefood.com`
- **Password:** `password123`
- **Company:** Square Food & Beverage Limited
- **Contact Person:** Kamal Hossain
- **Designation:** Brand Manager
- **Phone:** +8801766666666
- **Registration:** REG-2024-006
- **BSTI Number:** BSTI-10006
- **TIN:** TIN-123456006
- **Address:** Square Centre, 48 Mohakhali C/A, Dhaka

### 7. Bashundhara Paper Mills PLC
- **Email:** `contact@bashundhara.com`
- **Password:** `password123`
- **Company:** Bashundhara Paper Mills PLC
- **Contact Person:** Ahmed Bashir
- **Designation:** Operations Manager
- **Phone:** +8801777777777
- **Registration:** REG-2024-007
- **BSTI Number:** BSTI-10007
- **TIN:** TIN-123456007
- **Address:** Bashundhara City, Panthapath, Dhaka

### 8. PRAN Dairy Ltd.
- **Email:** `contact@prandairy.com`
- **Password:** `password123`
- **Company:** PRAN Dairy Ltd.
- **Contact Person:** Salma Akter
- **Designation:** Product Manager
- **Phone:** +8801788888888
- **Registration:** REG-2024-008
- **BSTI Number:** BSTI-10008
- **TIN:** TIN-123456008
- **Address:** PRAN Centre, Narsingdi

---

## How to Initialize Test Accounts

### Method 1: Using the Hidden Feature
1. Open the app and go to the Login screen
2. **Long press** the "Back to Home" button
3. Wait for the toast messages confirming account creation
4. All 8 vendor accounts will be created in Firebase

### Method 2: Manual Creation
Users can also register manually through:
- **Company Vendors:** Click "Sign Up as Company Vendor" on login screen
- **Retail Vendors:** Click "Sign Up as Retail Vendor" on login screen

---

## Login Instructions

1. Open the app
2. Navigate to Login screen
3. Select user type from dropdown:
   - **Admin** → Use admin credentials
   - **Company Vendor** → Use any of the 8 company vendor credentials above
   - **Retail Vendor** → Register manually
   - **User** → Register manually
4. Enter email and password
5. Click Login

## Access Levels

- **Admin:** Full dashboard access with user management, vendor management, and product approvals
- **Company Vendors:** Vendor dashboard with product management and order tracking
- **Retail Vendors:** Vendor dashboard with product management and order tracking
- **Users:** Home screen with shopping capabilities

---

## Product Creation Feature

When vendors add products, the **manufacturer name** is **automatically set** to the vendor's company name:
- Products from `contact@akijfood.com` will have manufacturer: "Akij Food & Beverage Ltd."
- Products from `contact@pranrfl.com` will have manufacturer: "PRAN-RFL Group"
- And so on...

The manufacturer field in the Add Product screen is read-only and auto-populated.

---

## Notes

- All vendor accounts use the same password: `password123`
- Admin credentials are hardcoded: `admin@findingbd.com` / `admin123`
- Vendor accounts are stored in Firebase Realtime Database under `users/{userId}`
- Each vendor account requires Firebase Authentication to be initialized first
- The manufacturer field is automatically populated from the vendor's company name
