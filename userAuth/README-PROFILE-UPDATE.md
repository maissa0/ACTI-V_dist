# User Profile Update Functionality

This document provides instructions for testing the user profile update functionality using Postman.

## Overview

The user profile update functionality allows authenticated users to update their profile information, including:
- Email address
- First name
- Last name
- Two-factor authentication status

## API Endpoints

### 1. View User Profile

**Endpoint:** `GET /api/auth/profile`

**Description:** Retrieves the basic profile information of the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "userId": 1,
  "username": "testuser",
  "email": "testuser@example.com",
  "role": "ROLE_USER",
  "company": null
}
```

### 2. View Detailed User Profile

**Endpoint:** `GET /api/auth/profile/detailed`

**Description:** Retrieves detailed profile information of the authenticated user, including role-specific data.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "userId": 1,
  "username": "testuser",
  "email": "testuser@example.com",
  "role": "ROLE_USER",
  "enabled": true,
  "twoFactorEnabled": false,
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

### 3. Update User Profile

**Endpoint:** `PUT /api/auth/profile/update`

**Description:** Updates the profile information of the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Request Body:**
```json
{
  "email": "newemail@example.com",
  "firstName": "New First Name",
  "lastName": "New Last Name",
  "twoFactorEnabled": true
}
```

**Notes:**
- All fields are optional. Only include the fields you want to update.
- If updating the email, the new email must not be in use by another account.
- If enabling two-factor authentication, a secret will be generated if one doesn't exist.

**Response Example:**
```json
{
  "user": {
    "userId": 1,
    "userName": "testuser",
    "email": "newemail@example.com",
    "accountNonLocked": true,
    "accountNonExpired": true,
    "credentialsNonExpired": true,
    "enabled": true,
    "twoFactorEnabled": true,
    "role": {
      "roleId": 1,
      "roleName": "ROLE_USER"
    },
    "createdDate": "2023-05-15T10:30:45",
    "updatedDate": "2023-05-16T14:22:33"
  }
}
```

## Testing with Postman

### Step 1: User Registration
1. Create a new request in Postman
2. Set the method to POST
3. Set the URL to `http://localhost:8080/api/auth/public/signup`
4. Set the Content-Type header to `application/json`
5. Set the request body to:
```json
{
  "username": "profiletestuser",
  "email": "profiletest@example.com",
  "password": "Password123!"
}
```
6. Send the request

### Step 2: User Login
1. Create a new request in Postman
2. Set the method to POST
3. Set the URL to `http://localhost:8080/api/auth/public/signin`
4. Set the Content-Type header to `application/json`
5. Set the request body to:
```json
{
  "username": "profiletestuser",
  "password": "Password123!"
}
```
6. Send the request
7. Save the token from the response

### Step 3: View Current Profile
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/auth/profile`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Note the current profile information

### Step 4: Update Profile
1. Create a new request in Postman
2. Set the method to PUT
3. Set the URL to `http://localhost:8080/api/auth/profile/update`
4. Set the Content-Type header to `application/json`
5. Set the Authorization header to `Bearer <your-token>`
6. Set the request body to:
```json
{
  "firstName": "Updated First",
  "lastName": "Updated Last",
  "twoFactorEnabled": false
}
```
7. Send the request
8. Verify the response contains the updated information

### Step 5: View Updated Profile
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/auth/profile/detailed`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the profile has been updated with the new information

## Error Scenarios

### 1. Email Already in Use
If you try to update the email to one that's already in use:
```json
{
  "email": "existing@example.com"
}
```
You should receive an error response:
```json
{
  "message": "Error updating profile: Email is already in use by another account"
}
```

### 2. Unauthenticated Request
If you try to update the profile without a valid token, you should receive a 401 Unauthorized response.

### 3. Invalid Data
If you provide invalid data (e.g., an invalid email format), you should receive a validation error.

## Postman Collection

You can import the following Postman collection to test the user profile functionality:

1. Create a new collection in Postman
2. Add the following requests:
   - User Registration (POST /api/auth/public/signup)
   - User Login (POST /api/auth/public/signin)
   - View Profile (GET /api/auth/profile)
   - View Detailed Profile (GET /api/auth/profile/detailed)
   - Update Profile (PUT /api/auth/profile/update)
3. Set up environment variables:
   - `base_url`: http://localhost:8080
   - `auth_token`: (populated after login)
4. Use `{{base_url}}` and `{{auth_token}}` in your requests 