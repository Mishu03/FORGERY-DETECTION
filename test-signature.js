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
        console.log("✅ Signature test matches expectations.\n");
    } else {
        console.log("⚠️ Signature test differs from expectations.\n");
    }
}

// Signature test function
async function testSignature(referencePath, testPath, expected) {
    if (!fs.existsSync(referencePath)) {
        console.error("REFERENCE signature file not found:", referencePath);
        return;
    }
    if (!fs.existsSync(testPath)) {
        console.error("TEST signature file not found:", testPath);
        return;
    }

    const form = new FormData();
    form.append("referenceFile", fs.createReadStream(referencePath));
    form.append("testFile", fs.createReadStream(testPath));

    try {
        const result = await postFile("/signature", form);
        compareResult(result, expected);
    } catch (err) {
        console.error("Error during SIGNATURE test:", err.message);
    }
}

// Run signature test
(async () => {
    const expectedSignature = { decision: "Likely Genuine", forgeryProbability: 0.2 };
    await testSignature(
        "test-data/sign_ref.jpeg",
        "test-data/sign_test.png",
        expectedSignature
    );
})();