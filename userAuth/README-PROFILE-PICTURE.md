# Profile Picture Functionality

This document provides instructions for testing the profile picture functionality using Postman.

## Overview

The profile picture functionality allows authenticated users to:
- Upload a profile picture
- View their profile picture
- Delete their profile picture

## API Endpoints

### 1. Upload Profile Picture

**Endpoint:** `POST /api/profile/picture/upload`

**Description:** Uploads a profile picture for the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Request:**
- Content-Type: multipart/form-data
- Body: 
  - file: The image file to upload (field name must be "file")

**Response Example:**
```json
{
  "message": "Profile picture uploaded successfully"
}
```

### 2. Get Profile Picture

**Endpoint:** `GET /api/profile/picture`

**Description:** Retrieves the profile picture of the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Response:**
- Content-Type: image/* (depends on the image format)
- Body: The image file

### 3. Delete Profile Picture

**Endpoint:** `DELETE /api/profile/picture`

**Description:** Deletes the profile picture of the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "message": "Profile picture deleted successfully"
}
```

### 4. View User Profile with Profile Picture Information

**Endpoint:** `GET /api/auth/profile`

**Description:** Retrieves the user profile information, including the profile picture path.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "user": {
    "userId": 1,
    "userName": "testuser",
    "email": "testuser@example.com",
    "role": {
      "roleId": 1,
      "roleName": "ROLE_USER"
    },
    "profilePicture": "profile-pictures/abc123def456.jpg"
  }
}
```

### 5. View Detailed User Profile with Profile Picture Information

**Endpoint:** `GET /api/auth/profile/detailed`

**Description:** Retrieves detailed user profile information, including the profile picture path and URL.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "userId": 1,
  "username": "testuser",
  "email": "testuser@example.com",
  "firstName": "Test",
  "lastName": "User",
  "role": "ROLE_USER",
  "enabled": true,
  "twoFactorEnabled": false,
  "profilePicture": "profile-pictures/abc123def456.jpg",
  "profilePictureUrl": "/api/profile/picture",
  "roleType": "Standard User",
  "accountStatus": {
    "accountNonLocked": true,
    "accountNonExpired": true,
    "credentialsNonExpired": true,
    "accountExpiryDate": "2025-05-15",
    "credentialsExpiryDate": "2025-05-15"
  }
}
```

## Testing with Postman

### Step 1: User Login
1. Create a new request in Postman
2. Set the method to POST
3. Set the URL to `http://localhost:8080/api/auth/public/signin`
4. Set the Content-Type header to `application/json`
5. Set the request body to:
```json
{
  "username": "your_username",
  "password": "your_password"
}
```
6. Send the request
7. Save the token from the response

### Step 2: Upload Profile Picture
1. Create a new request in Postman
2. Set the method to POST
3. Set the URL to `http://localhost:8080/api/profile/picture/upload`
4. Set the Authorization header to `Bearer <your-token>`
5. Go to the "Body" tab and select "form-data"
6. Add a key named "file" with type "File"
7. Select an image file from your computer
8. Send the request
9. Verify you receive a success message

### Step 3: View User Profile
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/auth/profile`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the response includes the profile picture path

### Step 4: View Profile Picture
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/profile/picture`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the response is the image file

### Step 5: Delete Profile Picture
1. Create a new request in Postman
2. Set the method to DELETE
3. Set the URL to `http://localhost:8080/api/profile/picture`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify you receive a success message

## Testing with a Web Browser

You can also test the profile picture functionality using a web browser:

1. Log in to your application
2. Navigate to your profile page
3. Upload a profile picture
4. Verify the profile picture is displayed
5. Delete the profile picture
6. Verify the profile picture is removed

## Error Scenarios

### 1. Uploading a Non-Image File
If you try to upload a file that is not an image:
```
{
  "message": "Only image files are allowed"
}
```

### 2. Unauthenticated Request
If you try to access any of the endpoints without a valid token, you should receive a 401 Unauthorized response.

### 3. No Profile Picture
If you try to get a profile picture when none exists:
```
404 Not Found
```

## Implementation Notes

- Profile pictures are stored in the `uploads/profile-pictures` directory
- Each file is given a unique name to prevent conflicts
- The original file extension is preserved
- The maximum file size is 200MB (configurable in application.properties)
- Supported image formats: PNG, JPEG, GIF, BMP, WEBP 