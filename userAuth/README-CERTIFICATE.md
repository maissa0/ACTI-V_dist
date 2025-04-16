# Certificate Management Functionality

This document provides instructions for testing the certificate management functionality using Postman.

## Overview

The certificate management functionality allows authenticated users to:
- Upload any type of file as a certificate
- View their certificates
- Download certificates
- Update certificate information
- Delete certificates

## API Endpoints

### 1. Upload a Certificate

**Endpoint:** `POST /api/certs/upload`

**Description:** Uploads a file as a certificate for the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Request:**
- Content-Type: multipart/form-data
- Body: 
  - file: The file to upload (field name must be "file")
  - fromWhere: The source of the certificate (e.g., "University", "Training Center", "Company")

**Response Example:**
```json
{
  "fileName": "certificate.pdf",
  "downloadURL": "http://localhost:8080/api/certs/download/abc123def456",
  "fileType": "application/pdf",
  "fileSize": 12345
}
```

### 2. Get All User Certificates

**Endpoint:** `GET /api/certs`

**Description:** Retrieves all certificates for the authenticated user.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
[
  {
    "fileName": "certificate1.pdf",
    "downloadURL": "http://localhost:8080/api/certs/download/abc123def456",
    "fileType": "application/pdf",
    "fileSize": 12345
  },
  {
    "fileName": "certificate2.jpg",
    "downloadURL": "http://localhost:8080/api/certs/download/ghi789jkl012",
    "fileType": "image/jpeg",
    "fileSize": 67890
  }
]
```

### 3. Get Certificate by ID

**Endpoint:** `GET /api/certs/{fileId}`

**Description:** Retrieves a specific certificate by its ID.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "fileName": "certificate.pdf",
  "downloadURL": "http://localhost:8080/api/certs/download/abc123def456",
  "fileType": "application/pdf",
  "fileSize": 12345
}
```

### 4. Download a Certificate

**Endpoint:** `GET /api/certs/download/{fileId}`

**Description:** Downloads a certificate file.

**Authentication:** JWT token required in the Authorization header.

**Response:**
- Content-Type: Depends on the file type
- Body: The file content

### 5. Update Certificate Information

**Endpoint:** `PUT /api/certs/{fileId}`

**Description:** Updates the information of a certificate.

**Authentication:** JWT token required in the Authorization header.

**Request:**
- Content-Type: application/x-www-form-urlencoded
- Body:
  - fromWhere: The new source of the certificate

**Response Example:**
```json
{
  "fileName": "certificate.pdf",
  "downloadURL": "http://localhost:8080/api/certs/download/abc123def456",
  "fileType": "application/pdf",
  "fileSize": 12345
}
```

### 6. Delete a Certificate

**Endpoint:** `DELETE /api/certs/{fileId}`

**Description:** Deletes a certificate.

**Authentication:** JWT token required in the Authorization header.

**Response Example:**
```json
{
  "message": "Certificate deleted successfully"
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

### Step 2: Upload a Certificate
1. Create a new request in Postman
2. Set the method to POST
3. Set the URL to `http://localhost:8080/api/certs/upload`
4. Set the Authorization header to `Bearer <your-token>`
5. Go to the "Body" tab and select "form-data"
6. Add a key named "file" with type "File" and select a file from your computer
7. Add a key named "fromWhere" with type "Text" and enter a value (e.g., "University")
8. Send the request
9. Verify you receive a success response with the certificate details

### Step 3: View All Certificates
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/certs`
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the response includes a list of your certificates

### Step 4: Get a Certificate by ID
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/certs/{fileId}` (replace `{fileId}` with an actual certificate ID)
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the response includes the certificate details

### Step 5: Download a Certificate
1. Create a new request in Postman
2. Set the method to GET
3. Set the URL to `http://localhost:8080/api/certs/download/{fileId}` (replace `{fileId}` with an actual certificate ID)
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify the response is the file content

### Step 6: Update Certificate Information
1. Create a new request in Postman
2. Set the method to PUT
3. Set the URL to `http://localhost:8080/api/certs/{fileId}` (replace `{fileId}` with an actual certificate ID)
4. Set the Authorization header to `Bearer <your-token>`
5. Go to the "Body" tab and select "x-www-form-urlencoded"
6. Add a key named "fromWhere" and enter a new value (e.g., "Training Center")
7. Send the request
8. Verify the response includes the updated certificate details

### Step 7: Delete a Certificate
1. Create a new request in Postman
2. Set the method to DELETE
3. Set the URL to `http://localhost:8080/api/certs/{fileId}` (replace `{fileId}` with an actual certificate ID)
4. Set the Authorization header to `Bearer <your-token>`
5. Send the request
6. Verify you receive a success message

## Testing with cURL (Command Line)

If you prefer using the command line, here are the equivalent cURL commands:

### Authentication
```bash
curl -X POST http://localhost:8080/api/auth/public/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}'
```

### Upload Certificate
```bash
curl -X POST http://localhost:8080/api/certs/upload \
  -H "Authorization: Bearer your_token_here" \
  -F "file=@/path/to/your/certificate.pdf" \
  -F "fromWhere=University"
```

### View All Certificates
```bash
curl -X GET http://localhost:8080/api/certs \
  -H "Authorization: Bearer your_token_here"
```

### Get Certificate by ID
```bash
curl -X GET http://localhost:8080/api/certs/your_certificate_id \
  -H "Authorization: Bearer your_token_here"
```

### Download Certificate
```bash
curl -X GET http://localhost:8080/api/certs/download/your_certificate_id \
  -H "Authorization: Bearer your_token_here" \
  --output downloaded_certificate.pdf
```

### Update Certificate Information
```bash
curl -X PUT http://localhost:8080/api/certs/your_certificate_id \
  -H "Authorization: Bearer your_token_here" \
  -d "fromWhere=Training Center"
```

### Delete Certificate
```bash
curl -X DELETE http://localhost:8080/api/certs/your_certificate_id \
  -H "Authorization: Bearer your_token_here"
```

## Error Scenarios

### 1. Unauthenticated Request
If you try to access any of the endpoints without a valid token, you should receive a 401 Unauthorized response.

### 2. Certificate Not Found
If you try to get, download, update, or delete a certificate that doesn't exist:
```json
{
  "message": "Certificate not found with Id: your_certificate_id"
}
```

### 3. Permission Denied
If you try to update or delete a certificate that doesn't belong to you:
```json
{
  "message": "You don't have permission to update this certificate"
}
```

## Implementation Notes

- Certificates are stored in the database as binary data (BLOB)
- Each certificate has a unique ID generated using UUID
- The system logs all certificate operations (creation, update, deletion) for audit purposes
- The system supports any file type, not just image files
- The maximum file size is 200MB (configurable in application.properties) 