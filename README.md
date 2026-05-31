Novel Reader App

A desktop application for managing and reading PDF novels. The application automatically imports PDF files, extracts metadata, generates book covers, tracks reading progress, and stores the library in a PostgreSQL database.

Features

- Import PDF novels
- Automatic cover generation from the first PDF page
- Metadata extraction using PDFBox
- PostgreSQL persistence
- Reading progress tracking
- Favorite books system
- Layered architecture (Service → Repository → Database)
- Configuration management using properties files

Architecture

The project follows a layered architecture:

UI
 ↓
Services
 ↓
Repository Interface
 ↓
Postgres Repository
 ↓
Database Manager
 ↓
PostgreSQL

Main Components

Services

- "BookImportService"
  
  - Imports PDF files
  - Extracts metadata
  - Generates cover images
  - Persists imported books

- "LibraryService"
  
  - Retrieves library data
  - Updates reading progress
  - Manages favorite books

Repository Layer

- "NovelRepository" (interface)
- "PostgresNovelRepository" (implementation)

This separation allows changing the persistence implementation without affecting business logic.

Database Layer

- "DataBaseManager"
  - Database initialization
  - Connection management

Technologies Used

- Java
- JavaFX
- PostgreSQL
- Maven
- Apache PDFBox
- JDBC

Database Schema

CREATE TABLE novels (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    file_path TEXT UNIQUE NOT NULL,
    cover_path TEXT,
    current_page INTEGER DEFAULT 0,
    total_pages INTEGER,
    is_favorite BOOLEAN DEFAULT FALSE
);

Project Structure

src/main/java
│
├── config
├── database
├── model
├── repository
├── service
└── ui

Setup

1. Clone the repository

git clone <repository-url>
cd novel-reader-app

2. Configure the database

Create:

src/main/resources/application.properties

Example:

db.url=jdbc:postgresql://localhost:5432/novel_reader
db.user=postgres
db.password=your_password

3. Create the PostgreSQL database

CREATE DATABASE novel_reader;

4. Run the application

mvn clean install
mvn javafx:run

Future Improvements

- EPUB support
- Search and filtering
- Reading statistics
- Book categories and tags
- Custom themes
- Unit testing
- Logging system
- REST API integration

Author

Developed as a Software Engineering learning project focused on:

- Java application development
- Layered architecture
- Repository pattern
- Database integration
- Clean code principles