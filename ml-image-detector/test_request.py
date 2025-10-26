import requests

# URL of the running Flask ML microservice
url = "http://127.0.0.1:5000/analyze"  # use 192.168.0.100 if needed

# Path to your test image
image_path = r"A:\MJR\forgery-detection\ml-image-detector\sample_images\test_image.jpeg"

# Open image in binary mode
with open(image_path, "rb") as f:
    files = {"file": f}
    try:
        response = requests.post(url, files=files)
        response.raise_for_status()  # raise error if status code != 200
        result = response.json()
        print("ML Forgery Analysis Result:")
        print(result)
    except requests.exceptions.RequestException as e:
        print("Request failed:", e)
    except ValueError as ve:
        print("Invalid JSON response:", ve)