# Repo Overview

- **Name**: forgery-detection
- **Stack**: Java 11, Spring Boot 2.7.x, Maven; OpenCV (via org.openpnp), Weka; Thymeleaf. Node scripts for testing HTTP endpoints.
- **Modules**: Single module Spring Boot app

## Key Paths
- Backend Java: `src/main/java/com/mydomain/forgery_detection`
- Resources: `src/main/resources`
- Tests (Java): `src/test/java`
- Test assets: `test-data`
- Node client scripts: `test-image.js`, `test-video.js`, `test-signature.js`

## Important Classes
- `DetectionController` (REST endpoints `/api/detection/*`)
- `ImageDetectionService`, `VideoDetectionService`, `SignatureVerificationService`
- Analyzers: `ElaAnalyzer`, `NoiseAnalyzer`, `MetadataAnalyzer`, `StructuralSimilarityAnalyzer`, `StrokeConsistencyAnalyzer`, `PressurePatternAnalyzer`
- Config: `OpenCVLoader` (loads native OpenCV)
- DTOs: `DetectionResult` + specific result types

## Build/Run
- Build: `mvn install`
- Run: `mvn spring-boot:run`
- OpenCV: set `OPENCV_DLL_PATH` to `opencv_java*.dll` for Windows (fallback if System.loadLibrary fails)

## Notable Settings
- `application.properties` uses PostgreSQL by default; dev profile will add H2.
- Multipart limits: 100MB.

## Known Fixes Applied
- Fixed Weka dependency in `pom.xml`.
- Removed hardcoded OpenCV `-Djava.library.path`.
- Robust OpenCV loader implemented.
- README endpoints aligned to `/api/detection/*`.

## Pending/Optional
- Dev profile added with H2 (application-dev.properties) and H2 dependency in pom.
- Consider upgrading Spring Boot and Java in future.