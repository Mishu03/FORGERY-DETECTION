import os
import numpy as np
from PIL import Image
import tensorflow as tf
from tensorflow.keras.utils import Sequence
from tensorflow.keras.preprocessing.image import ImageDataGenerator

# -----------------------
# CONFIG
# -----------------------
IMG_SIZE = (128, 128)
BATCH_SIZE = 16
EPOCHS = 20
MODEL_SAVE_PATH = "forgery_seg_model_final.h5"
CHECKPOINT_PATH = "checkpoint/forgery_seg_ckpt.h5"
EPOCH_TRACK_FILE = "checkpoint/last_epoch.txt"  # ✅ track last completed epoch

AU_DIR = "CASIA2/Au"
TP_DIR = "CASIA2/Tp"
MASK_DIR = "CASIA2/CASIA 2 Groundtruth"

os.makedirs("checkpoint", exist_ok=True)

# -----------------------
# IMAGE UTILITIES
# -----------------------
def find_mask(mask_dir, base_name):
    mask_files = os.listdir(mask_dir)
    return next((mf for mf in mask_files if base_name in os.path.splitext(mf)[0]), None)

def load_image_pair(tp_dir, mask_dir, filename):
    img_path = os.path.join(tp_dir, filename)
    base_name = os.path.splitext(filename)[0]
    mask_match = find_mask(mask_dir, base_name)
    if not mask_match:
        return None, None
    mask_path = os.path.join(mask_dir, mask_match)
    try:
        img = Image.open(img_path).convert("RGB").resize(IMG_SIZE)
        mask = Image.open(mask_path).convert("L").resize(IMG_SIZE)
        img = np.array(img) / 255.0
        mask = np.expand_dims(np.array(mask) / 255.0, axis=-1)
        return img, mask
    except Exception as e:
        print(f"⚠️ Error loading {filename}: {e}")
        return None, None

# -----------------------
# DATA GENERATOR
# -----------------------
class ForgeryDataGenerator(Sequence):
    def __init__(self, tp_dir, mask_dir, au_dir, batch_size, img_size, augment=False):
        self.tp_files = os.listdir(tp_dir)
        self.au_files = os.listdir(au_dir)
        self.tp_dir = tp_dir
        self.mask_dir = mask_dir
        self.au_dir = au_dir
        self.batch_size = batch_size
        self.img_size = img_size
        self.augment = augment

        if augment:
            self.image_gen = ImageDataGenerator(horizontal_flip=True, rotation_range=10)
            self.mask_gen = ImageDataGenerator(horizontal_flip=True, rotation_range=10)

    def __len__(self):
        total = len(self.tp_files) + len(self.au_files)
        return total // self.batch_size

    def __getitem__(self, idx):
        imgs, masks = [], []
        start = idx * self.batch_size
        end = start + self.batch_size
        combined_files = self.tp_files + self.au_files
        batch_files = combined_files[start:end]

        for f in batch_files:
            if f in self.tp_files:
                img, mask = load_image_pair(self.tp_dir, self.mask_dir, f)
            else:
                img_path = os.path.join(self.au_dir, f)
                try:
                    img = Image.open(img_path).convert("RGB").resize(self.img_size)
                    img = np.array(img) / 255.0
                    mask = np.zeros((*self.img_size, 1))
                except Exception as e:
                    print(f"⚠️ Error loading {f}: {e}")
                    continue

            if img is not None:
                imgs.append(img)
                masks.append(mask)

        X = np.array(imgs, dtype=np.float32)
        y = np.array(masks, dtype=np.float32)

        if self.augment:
            seed = np.random.randint(10000)
            X = next(self.image_gen.flow(X, batch_size=self.batch_size, seed=seed))
            y = next(self.mask_gen.flow(y, batch_size=self.batch_size, seed=seed))
        return X, y

# -----------------------
# MODEL
# -----------------------
def unet_model(input_size=(128,128,3)):
    inputs = tf.keras.Input(input_size)
    c1 = tf.keras.layers.Conv2D(32, 3, activation='relu', padding='same')(inputs)
    c1 = tf.keras.layers.Conv2D(32, 3, activation='relu', padding='same')(c1)
    p1 = tf.keras.layers.MaxPooling2D(2)(c1)

    c2 = tf.keras.layers.Conv2D(64, 3, activation='relu', padding='same')(p1)
    c2 = tf.keras.layers.Conv2D(64, 3, activation='relu', padding='same')(c2)
    p2 = tf.keras.layers.MaxPooling2D(2)(c2)

    c3 = tf.keras.layers.Conv2D(128, 3, activation='relu', padding='same')(p2)
    c3 = tf.keras.layers.Conv2D(128, 3, activation='relu', padding='same')(c3)

    u2 = tf.keras.layers.UpSampling2D(2)(c3)
    u2 = tf.keras.layers.Concatenate()([u2, c2])
    c4 = tf.keras.layers.Conv2D(64, 3, activation='relu', padding='same')(u2)
    c4 = tf.keras.layers.Conv2D(64, 3, activation='relu', padding='same')(c4)

    u1 = tf.keras.layers.UpSampling2D(2)(c4)
    u1 = tf.keras.layers.Concatenate()([u1, c1])
    c5 = tf.keras.layers.Conv2D(32, 3, activation='relu', padding='same')(u1)
    c5 = tf.keras.layers.Conv2D(32, 3, activation='relu', padding='same')(c5)

    outputs = tf.keras.layers.Conv2D(1, 1, activation='sigmoid')(c5)
    return tf.keras.Model(inputs, outputs)

def dice_coef(y_true, y_pred):
    y_true_f = tf.keras.backend.flatten(y_true)
    y_pred_f = tf.keras.backend.flatten(y_pred)
    intersection = tf.keras.backend.sum(y_true_f * y_pred_f)
    return (2. * intersection + 1e-7) / (tf.keras.backend.sum(y_true_f) + tf.keras.backend.sum(y_pred_f) + 1e-7)

# -----------------------
# TRAINING
# -----------------------
train_gen = ForgeryDataGenerator(TP_DIR, MASK_DIR, AU_DIR, BATCH_SIZE, IMG_SIZE, augment=True)
val_gen = ForgeryDataGenerator(TP_DIR, MASK_DIR, AU_DIR, BATCH_SIZE, IMG_SIZE, augment=False)

# 🔹 Resume logic with epoch tracking
initial_epoch = 0
if os.path.exists(CHECKPOINT_PATH):
    print(f"🔁 Resuming training from checkpoint: {CHECKPOINT_PATH}")
    model = tf.keras.models.load_model(CHECKPOINT_PATH, custom_objects={'dice_coef': dice_coef})
    if os.path.exists(EPOCH_TRACK_FILE):
        with open(EPOCH_TRACK_FILE, 'r') as f:
            initial_epoch = int(f.read())
else:
    print("🆕 Starting training from scratch.")
    model = unet_model()
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy', dice_coef])

# 🔹 Callbacks
checkpoint_cb = tf.keras.callbacks.ModelCheckpoint(
    CHECKPOINT_PATH,
    monitor='val_loss',
    save_best_only=True,
    verbose=1,
    save_format="h5"
)

early_stop_cb = tf.keras.callbacks.EarlyStopping(
    monitor='val_loss',
    patience=5,
    restore_best_weights=True
)

class EpochTracker(tf.keras.callbacks.Callback):
    def on_epoch_end(self, epoch, logs=None):
        with open(EPOCH_TRACK_FILE, 'w') as f:
            f.write(str(epoch + 1))  # track last completed epoch

# 🔹 Train
history = model.fit(
    train_gen,
    validation_data=val_gen,
    epochs=EPOCHS,
    initial_epoch=initial_epoch,
    callbacks=[checkpoint_cb, early_stop_cb, EpochTracker()]
)

# 🔹 Final save
model.save(MODEL_SAVE_PATH, save_format="h5")
print(f"✅ Final model saved as {MODEL_SAVE_PATH}")