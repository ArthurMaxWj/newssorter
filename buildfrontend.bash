#!/bin/bash

set -e

FRONTEND_DIR="frontend/reactapp"
SPRING_STATIC_DIR="src/main/resources/static/app"

# Clean previous frontend build from static
echo "Cleaning old static files..."
rm -rf "$SPRING_STATIC_DIR"/*

# Build React app
echo "Building React app..."
cd "$FRONTEND_DIR"
npm install
npm run build

# Detect build output folder
if [ -d "dist" ]; then
  BUILD_DIR="dist"
elif [ -d "build" ]; then
  BUILD_DIR="build"
else
  echo "Build directory not found!"
  exit 1
fi

# Copy build files to Spring Boot static folder
echo "Copying $BUILD_DIR to $SPRING_STATIC_DIR"
cp -r $BUILD_DIR/* ../../$SPRING_STATIC_DIR/
echo "Copying ../../$SPRING_STATIC_DIR/assets/** to ../../$SPRING_STATIC_DIR/../assets"
cp -r ../../$SPRING_STATIC_DIR/assets/** ../../$SPRING_STATIC_DIR/../assets

echo "Build and copy complete."
