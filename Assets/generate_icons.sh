#!/bin/bash

# Create Android mipmap directories
mkdir -p ../composeApp/src/main/res/mipmap-mdpi
mkdir -p ../composeApp/src/main/res/mipmap-hdpi
mkdir -p ../composeApp/src/main/res/mipmap-xhdpi
mkdir -p ../composeApp/src/main/res/mipmap-xxhdpi
mkdir -p ../composeApp/src/main/res/mipmap-xxxhdpi

# Create adaptive icon directories
mkdir -p ../composeApp/src/main/res/mipmap-anydpi-v26

# Check if required tools are installed
if ! command -v inkscape &> /dev/null
then
    echo "Inkscape is not installed. Please install Inkscape to generate icons."
    echo "On macOS: brew install inkscape"
    echo "On Ubuntu: sudo apt install inkscape"
    exit 1
fi

# Generate adaptive icon foreground (PNG format)
echo "Generating adaptive icon foreground..."
inkscape -w 108 -h 108 -o ../composeApp/src/main/res/drawable/ic_launcher_foreground.png Log.svg

# Generate mipmap icons for different densities
echo "Generating mipmap icons..."

# mdpi (48x48)
inkscape -w 48 -h 48 -o ../composeApp/src/main/res/mipmap-mdpi/ic_launcher.png Log.svg
inkscape -w 48 -h 48 -o ../composeApp/src/main/res/mipmap-mdpi/ic_launcher_round.png Log.svg

# hdpi (72x72)
inkscape -w 72 -h 72 -o ../composeApp/src/main/res/mipmap-hdpi/ic_launcher.png Log.svg
inkscape -w 72 -h 72 -o ../composeApp/src/main/res/mipmap-hdpi/ic_launcher_round.png Log.svg

# xhdpi (96x96)
inkscape -w 96 -h 96 -o ../composeApp/src/main/res/mipmap-xhdpi/ic_launcher.png Log.svg
inkscape -w 96 -h 96 -o ../composeApp/src/main/res/mipmap-xhdpi/ic_launcher_round.png Log.svg

# xxhdpi (144x144)
inkscape -w 144 -h 144 -o ../composeApp/src/main/res/mipmap-xxhdpi/ic_launcher.png Log.svg
inkscape -w 144 -h 144 -o ../composeApp/src/main/res/mipmap-xxhdpi/ic_launcher_round.png Log.svg

# xxxhdpi (192x192)
inkscape -w 192 -h 192 -o ../composeApp/src/main/res/mipmap-xxxhdpi/ic_launcher.png Log.svg
inkscape -w 192 -h 192 -o ../composeApp/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png Log.svg

# Generate splash screen logos
echo "Generating splash screen logos..."

# Splash logo (360x360)
inkscape -w 360 -h 360 -o ../composeApp/src/main/res/drawable/splash_logo.png Log.svg

# Generate adaptive icon background (solid color)
echo "Generating adaptive icon background..."
convert -size 108x108 xc:"#DC1B58" ../composeApp/src/main/res/drawable/ic_background.png

# Create adaptive icon XML
echo "Creating adaptive icon XML..."
cat > ../composeApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

cat > ../composeApp/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

echo "Android mipmap directories and icons created successfully!"