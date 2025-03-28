#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting local deployment process..."

# Build the application
echo "📦 Building the application..."
./gradlew clean build

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t mhristev/orgservice:latest .

# Login to Docker Hub (you'll need to enter your credentials)
echo "🔑 Logging in to Docker Hub..."
docker login

# Push to Docker Hub
echo "⬆️ Pushing to Docker Hub..."
docker push mhristev/orgservice:latest

echo "✅ Deployment completed successfully!" 