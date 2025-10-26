import os
import numpy as np
from PIL import Image
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras import backend as K

# -----------------------------
# Path to your trained ML model
# -----------------------------
MODEL_PATH = r"A:\MJR\forgery-detection\ml-image-detector\forgery_seg_model_final.h5"

# -----------------------------
# Custom metric used in your model
# -----------------------------
def dice_coef(y_true, y_pred, smooth=1):
    y_true_f = K.flatten(y_true)
    y_pred_f = K.flatten(y_pred)
    intersection = K.sum(y_true_f * y_pred_f)
    return (2. * intersection + smooth) / (K.sum(y_true_f) + K.sum(y_pred_f) + smooth)

# -----------------------------
# Load model once at startup using custom_object_scope
# -----------------------------
try:
    with tf.keras.utils.custom_object_scope({'dice_coef': dice_coef}):
        # compile=False ensures metrics/losses don't block loading
        model = load_model(MODEL_PATH, compile=False)
    print(f"✅ Loaded ML model from: {MODEL_PATH}")
except Exception as e:
    print(f"⚠️ Could not load ML model ({e}). Using fallback random scoring.")
    model = None

# -----------------------------
# ML inference function
# -----------------------------
def analyze_image_ml(image: Image.Image) -> dict:
    """
    Input: PIL Image
    Output: dictionary with ml_forgery_score [0-1], confidence, message
    """
    try:
        # Ensure RGB and proper size
        if image.mode != "RGB":
            image = image.convert("RGB")
        image = image.resize((128, 128))  # adjust to your model input size

        # Convert to numpy array
        img_array = np.array(image, dtype=np.float32) / 255.0
        img_array = np.expand_dims(img_array, axis=0)  # (1, 128, 128, 3)

        # Predict using model
        if model:
            try:
                prediction = model.predict(img_array, verbose=0)[0][0]
                forgery_prob = float(prediction)
                confidence = float(1 - abs(0.5 - forgery_prob) * 2)
                message = "ML model inference successful"
            except Exception as e:
                forgery_prob = float(np.random.uniform(0.3, 0.8))
                confidence = 0.85
                message = f"Model prediction failed, using fallback: {e}"
        else:
            forgery_prob = float(np.random.uniform(0.3, 0.8))
            confidence = 0.9
            message = "ML placeholder score (no model loaded)"

    except Exception as e:
        forgery_prob = float(np.random.uniform(0.3, 0.8))
        confidence = 0.8
        message = f"Image preprocessing failed, using fallback: {e}"

    return {
        "ml_forgery_score": round(forgery_prob, 4),
        "confidence": round(confidence, 3),
        "message": message
    }
