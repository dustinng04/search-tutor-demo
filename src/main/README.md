# Search Tutor Demo - Preply-like Interface

A modern web application for finding tutors with a beautiful, step-by-step interface inspired by Preply.

## Features

- **Step-by-step tutor search**: Guided process with subject selection (required), level selection (optional), and availability selection (optional)
- **Modern UI**: Built with Tailwind CSS for a responsive, beautiful interface
- **Interactive experience**: Smooth transitions and hover effects
- **Demo mode**: Includes sample tutors for testing without Elasticsearch
- **Real search integration**: Connects to your existing Elasticsearch backend

## Available Options

### Subjects (Required)
- Mathematics
- Physics  
- English Literature

### Levels (Optional)
- High School
- Undergraduate

### Availability (Optional)
- Days: Monday through Sunday
- Time slots: 8:00-22:00 in 2-hour intervals

## How to Run

1. **Start the Spring Boot application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access the application**:
   Open your browser and navigate to: `http://localhost:8080`

3. **Use the interface**:
   - Select a subject (required to proceed)
   - Choose a level or skip
   - Select availability time slots or skip
   - View search results

## API Endpoints

- **Main search**: `GET /api/search` - Real search using Elasticsearch
- **Demo search**: `GET /api/demo/search` - Sample data for testing
- **Home page**: `GET /` - Serves the main interface

## Demo Data

The application includes 5 sample tutors with different subjects, levels, and availability times to demonstrate the functionality without requiring a full Elasticsearch setup.

## Technology Stack

- **Backend**: Spring Boot, Java
- **Frontend**: HTML5, CSS3 (Tailwind), Vanilla JavaScript
- **Search**: Elasticsearch (optional for demo mode)
- **Icons**: Font Awesome
- **Styling**: Tailwind CSS via CDN

## File Structure

```
src/main/
├── java/com/example/dev/
│   ├── Controller/
│   │   ├── MyController.java      # Main search API
│   │   └── DemoController.java    # Demo data API
│   ├── config/
│   │   └── WebConfig.java         # CORS and static resources
│   └── ...
└── resources/
    └── static/
        ├── index.html             # Main UI
        └── app.js                 # Frontend logic
```

The interface automatically tries the demo endpoint first and falls back to the real search API, making it easy to test and develop.
