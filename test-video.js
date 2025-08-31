import fs from "fs";
import path from "path";
import fetch from "node-fetch";
import pkg from "undici";
const { FormData, fileFromPath } = pkg;

const API_BASE = "http://localhost:8080/api/detection";
const OUTPUT_DIR = "test-output";

if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR);

async function postFile(endpoint, form) {
    const res = await fetch(`${API_BASE}${endpoint}`, { method: "POST", body: form });
    const json = await res.json();
    if (!res.ok) {
        console.error("Backend Error Response:", JSON.stringify(json, null, 2));
        throw new Error(`HTTP error! status: ${res.status}`);
    }
    return json;
}

async function testVideo(filePath, expected) {
    const form = new FormData();
    form.append("file", await fileFromPath(filePath));

    const result = await postFile("/video", form);

    console.log("Backend Decision:", result.decision, "| Expected:", expected.decision);
    console.log("Backend Probability:", result.forgeryProbability.toFixed(3),
        "| Expected:", expected.forgeryProbability.toFixed(3));
}

(async () => {
    await testVideo("test-data/video.mp4", { decision: "Suspicious", forgeryProbability: 0.36 });
})();