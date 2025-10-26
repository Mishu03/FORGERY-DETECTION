from flask import Flask, request, jsonify
from model import analyze_image_ml
from PIL import Image, UnidentifiedImageError
import io
import traceback

app = Flask(__name__)

@app.route("/analyze", methods=["POST"])
def analyze():
    if "file" not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files["file"]
    if file.filename.strip() == "":
        return jsonify({"error": "Empty filename provided"}), 400

    try:
        # Read file bytes
        file_bytes = file.read()

        # Validate uploaded file is an image
        image = Image.open(io.BytesIO(file_bytes))
        image.verify()  # basic validation

        # Reopen for actual processing
        image = Image.open(io.BytesIO(file_bytes)).convert("RGB")

        # --- Call ML inference ---
        result = analyze_image_ml(image)
        return jsonify(result), 200

    except UnidentifiedImageError:
        return jsonify({"error": "Uploaded file is not a valid image."}), 400

    except Exception as e:
        traceback.print_exc()
        return jsonify({
            "error": str(e),
            "message": "Internal server error during image analysis"
        }), 500

@app.route("/health", methods=["GET"])
def health_check():
    """Simple endpoint to check if Flask ML service is running"""
    return jsonify({"status": "OK", "message": "ML service is running"}), 200

if __name__ == "__main__":
    print("🚀 Starting Flask ML service on port 5000...")
    app.run(host="0.0.0.0", port=5000, debug=True)
