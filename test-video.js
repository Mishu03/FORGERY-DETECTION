import fs from "fs";
import path from "path";
import fetch from "node-fetch";
import FormData from "form-data";

const API_BASE = "http://localhost:8080/api/detection";
const OUTPUT_DIR = "test-output";

// Ensure output folder exists
if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR);

// Utility to post file
async function postFile(endpoint, form) {
    const res = await fetch(`${API_BASE}${endpoint}`, {
        method: "POST",
        body: form,
        headers: form.getHeaders()
    });
    const result = await res.json();
    if (!res.ok) {
        console.error("Backend Error Response:", JSON.stringify(result, null, 2));
        throw new Error(`HTTP error! status: ${res.status}`);
    }
    return result;
}

// Compare result
function compareResult(backendResult, expected) {
    console.log("Backend Decision:", backendResult.decision, "| Expected:", expected.decision);
    console.log("Backend Probability:", backendResult.forgeryProbability?.toFixed(3) ?? 0,
                "| Expected:", expected.forgeryProbability?.toFixed(3) ?? 0);

    const decisionMatch = backendResult.decision === expected.decision;
    const probabilityDiff = Math.abs((backendResult.forgeryProbability || 0) - (expected.forgeryProbability || 0));

    if (decisionMatch && probabilityDiff < 0.05) {
        console.log("✅ Video test matches expectations.\n");
    } else {
        console.log("⚠️ Video test differs from expectations.\n");
    }
}

// Video test function
async function testVideo(filePath, expected) {
    if (!fs.existsSync(filePath)) {
        console.error("VIDEO file not found:", filePath);
        return;
    }

    const form = new FormData();
    form.append("file", fs.createReadStream(filePath));

    try {
        const result = await postFile("/video", form);
        compareResult(result, expected);
    } catch (err) {
        console.error("Error during VIDEO test:", err.message);
    }
}

// Run video test
(async () => {
    const expectedVideo = { decision: "Likely Forged", forgeryProbability: 0.85 };
    await testVideo("test-data/video.mp4", expectedVideo);
})();