import fs from "fs";
import path from "path";
import fetch from "node-fetch";
import FormData from "form-data";

const API_BASE = "http://localhost:8080/api/detection";
const OUTPUT_DIR = "test-output";

// Ensure output folder exists
if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR);

// ---------- Utility to post file and get JSON ----------
async function postFile(endpoint, form) {
    const res = await fetch(`${API_BASE}${endpoint}`, {
        method: "POST",
        body: form,
        headers: form.getHeaders()  // Important for form-data
    });

    const result = await res.json();

    if (!res.ok) {
        console.error("Backend Error Response:", JSON.stringify(result, null, 2));
        throw new Error(`HTTP error! status: ${res.status}`);
    }
    return result;
}

// ---------- Compare backend result to expected frontend result ----------
function compareResult(backendResult, expected) {
    console.log("Backend Decision:", backendResult.decision, "| Expected:", expected.decision);
    console.log("Backend Probability:", backendResult.forgeryProbability.toFixed(3),
                "| Expected:", expected.forgeryProbability.toFixed(3));

    const decisionMatch = backendResult.decision === expected.decision;
    const probabilityDiff = Math.abs(backendResult.forgeryProbability - expected.forgeryProbability);

    if (decisionMatch && probabilityDiff < 0.05) {
        console.log("✅ Result matches frontend expectations.\n");
    } else {
        console.log("⚠️ Result differs from frontend expectations.\n");
    }
}

// ---------- IMAGE TEST ----------
async function testImage(filePath, expected) {
    if (!fs.existsSync(filePath)) {
        console.error("IMAGE file not found:", filePath);
        return;
    }

    const form = new FormData();
    form.append("file", fs.createReadStream(filePath));

    try {
        const result = await postFile("/image", form);

        // Save the ELA heatmap if available
        if (result.elaHeatmapBase64) {
            const heatmapBuffer = Buffer.from(result.elaHeatmapBase64, "base64");
            const heatmapPath = path.join(OUTPUT_DIR, "heatmap.png");
            fs.writeFileSync(heatmapPath, heatmapBuffer);
            console.log("ELA heatmap saved at:", heatmapPath);
        }

        compareResult(result, expected);
    } catch (err) {
        console.error("Error during test:", err.message);
    }
}

// ---------- RUN TEST ----------
(async () => {
    const expectedImage = { decision: "Likely Genuine", forgeryProbability: 0.25 };
    await testImage("test-data/image.jpg", expectedImage);
})();