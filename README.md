# Forgery Detection Application

## Overview
This application detects forgery in images, videos, and signatures using advanced algorithms and OpenCV.

## Features
- Image forgery detection
- Video forgery detection
- Signature verification

## Installation
1. Clone the repository.
2. Navigate to the project directory.
3. Run `mvn install` to build the project.

## Usage
- Dev (H2): `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Prod-like (PostgreSQL): `mvn spring-boot:run`
- To run the packaged jar: `java -jar target/forgery-detection.jar`.
- Follow the API documentation for endpoints.

## API Endpoints
- **Image Detection**: `POST /api/detection/image`
- **Video Detection**: `POST /api/detection/video`
- **Signature Verification**: `POST /api/detection/signature`

## Exception Handling
- If the uploaded file exceeds the maximum size (100MB), a `400 Bad Request` response will be returned with a message indicating the file is too large.

## Contributing
Feel free to submit issues or pull requests.

## License
This project is licensed under the MIT License.
