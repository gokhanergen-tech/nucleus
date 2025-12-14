# LWJGL Game Engine Project

A lightweight 3D game engine built in Java using **LWJGL 3 (Lightweight Java Game Library)**.  
Supports advanced lighting, terrain textures (BlendMap), 3D models (OBJ), and flexible input handling.

---

## Features

### Lighting
- **Directional Light** – simulates sunlight
- **Spot Light** – focused cone lights
- **Point Light** – omni-directional light sources
- Real-time dynamic lighting and color control

### Terrain & Textures
- **BlendMap support** for multiple terrain textures
- Smooth transitions for realistic landscapes
- Heightmap-based terrain generation

### 3D Models
- Load **OBJ models** with textures
- Multiple models supported simultaneously
- Easily integrate custom models

### Input & Controls
- Keyboard: WASD / Arrow keys
- Mouse: Camera rotation
- Adjustable camera speed and sensitivity

### Audio & GUI (Extra Features)
- Basic 3D sound support
- Simple HUD / GUI rendering system

---

## Getting Started

![Açıklayıcı Alternatif Metin](./screenshots/example.png)

### Prerequisites
- Java 11+ (project is configured for Java 11)
- Maven

### Compile and Run
```bash
mvn clean compile exec:java
