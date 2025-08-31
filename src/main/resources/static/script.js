/* =========================
   Forgery Detection Script (Dynamic Mirrors)
========================= */

const API_BASE = "/api/detection";

// Elements
const files = {
    image: document.getElementById("imageFile"),
    video: document.getElementById("videoFile"),
    reference: document.getElementById("referenceSignature"),
    test: document.getElementById("testSignature"),
};

const previews = {
    image: document.getElementById("imagePreview"),
    video: document.getElementById("videoPreview"),
    reference: document.getElementById("referencePreview"),
    test: document.getElementById("testPreview"),
};

const results = {
    image: document.getElementById("imageResults"),
    video: document.getElementById("videoResults"),
    signature: document.getElementById("signatureResults"),
};

const mirrors = {
    image: document.getElementById("imageResultsMirror"),
    video: document.getElementById("videoResultsMirror"),
    signature: document.getElementById("signatureResultsMirror"),
};

// Buttons
const buttons = {
    image: document.getElementById("analyzeImageBtn"),
    video: document.getElementById("analyzeVideoBtn"),
    signature: document.getElementById("verifySignatureBtn"),
};

/* =========================
   File Previews
========================= */
function showPreview(file, previewElement) {
    const reader = new FileReader();
    reader.onload = (e) => {
        let html = "";
        if (file.type.startsWith("image/")) {
            html = `<img src="${e.target.result}" alt="preview" style="max-width:100%;">`;
        } else if (file.type.startsWith("video/")) {
            html = `<video controls src="${e.target.result}" style="max-width:100%; border-radius:12px;"></video>`;
        } else {
            html = `<p>${file.name}</p>`;
        }
        previewElement.innerHTML = html;
        previewElement.classList.remove("hidden");
    };
    reader.readAsDataURL(file);
}

// Auto-preview on file change
Object.keys(files).forEach((key) => {
    files[key].addEventListener("change", () => {
        if (files[key].files[0]) showPreview(files[key].files[0], previews[key]);
    });
});

/* =========================
   Drag & Drop
========================= */
document.querySelectorAll(".dropzone").forEach((dz) => {
    const input = dz.querySelector("input[type=file]");
    const preview = dz.querySelector(".preview") || null;

    dz.addEventListener("click", () => input.click());

    dz.addEventListener("dragover", (e) => {
        e.preventDefault();
        dz.classList.add("dragover");
    });

    dz.addEventListener("dragleave", () => dz.classList.remove("dragover"));

    dz.addEventListener("drop", (e) => {
        e.preventDefault();
        dz.classList.remove("dragover");
        const file = e.dataTransfer.files[0];
        if (file) {
            input.files = e.dataTransfer.files;
            if (preview) showPreview(file, preview);
        }
    });
});

/* =========================
   Analysis Function
========================= */
async function analyzeFile(type) {
    if (type === "signature") {
        if (!files.reference.files[0] || !files.test.files[0]) {
            alert("Please upload both reference and test signatures!");
            return;
        }
        const formData = new FormData();
        formData.append("referenceFile", files.reference.files[0]);
        formData.append("testFile", files.test.files[0]);

        results.signature.innerHTML = `<div class="placeholder"><i class="fas fa-spinner fa-spin"></i><p>Verifying...</p></div>`;

        try {
            const res = await fetch(`${API_BASE}/signature`, { method: "POST", body: formData });
            const data = await res.json();
            displayResult("signature", data);
        } catch (err) {
            console.error(err);
            results.signature.innerHTML = `<p style="color:red;">Error verifying signature.</p>`;
        }
        return;
    }

    const file = files[type].files[0];
    if (!file) {
        alert(`Please upload a ${type} first!`);
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    results[type].innerHTML = `<div class="placeholder"><i class="fas fa-spinner fa-spin"></i><p>Analyzing...</p></div>`;

    try {
        const endpoint = type === "image" ? `${API_BASE}/image` : `${API_BASE}/video`;
        const res = await fetch(endpoint, { method: "POST", body: formData });
        const data = await res.json();
        displayResult(type, data);
    } catch (err) {
        console.error(err);
        results[type].innerHTML = `<p style="color:red;">Error analyzing ${type}.</p>`;
    }
}

/* =========================
   Display Result
========================= */
function displayResult(type, data) {
    let html = '';

    if (type === 'image') {
        html = `
        <div class="card card--soft">
            <h3>Image Analysis Result</h3>
            <p><strong>Status:</strong> ${data.decision || 'Unknown'}</p>
            <p><strong>ELA Score:</strong> ${data.elaScore?.toFixed(2) ?? 0}</p>
            <p><strong>Noise Score:</strong> ${data.noiseAnalysisScore?.toFixed(2) ?? 0}</p>
            <p><strong>Metadata Score:</strong> ${data.metadataConsistencyScore?.toFixed(2) ?? 0}</p>
            <p><strong>Similarity Score:</strong> ${data.similarityScore?.toFixed(2) ?? 0}</p>
            <p><strong>Processing Time:</strong> ${data.processingTimeMs ?? 0} ms</p>
            ${data.elaHeatmapBase64 ? `<p><strong>ELA Heatmap:</strong></p><img src="data:image/png;base64,${data.elaHeatmapBase64}" style="max-width:100%; border-radius:12px;">` : ''}
        </div>`;
    } else if (type === 'video') {
        html = `
        <div class="card card--soft">
            <h3>Video Analysis Result</h3>
            <p><strong>Status:</strong> ${data.decision || 'Unknown'}</p>
            <p><strong>Frame Consistency:</strong> ${data.frameConsistencyScore?.toFixed(2) ?? 0}</p>
            <p><strong>Motion Anomalies:</strong> ${data.motionAnomaliesScore?.toFixed(2) ?? 0}</p>
            <p><strong>Audio-Video Sync:</strong> ${data.audioVideoSyncScore?.toFixed(2) ?? 0}</p>
            <p><strong>Forgery Probability:</strong> ${data.forgeryProbability?.toFixed(2) ?? 0}</p>
            <p><strong>Processing Time:</strong> ${data.processingTimeMs ?? 0} ms</p>
        </div>`;
    } else if (type === 'signature') {
        html = `
        <div class="card card--soft">
            <h3>Signature Verification Result</h3>
            <p><strong>Status:</strong> ${data.decision || 'Unknown'}</p>
            <p><strong>Structural Similarity:</strong> ${data.structuralSimilarity?.toFixed(2) ?? 0}</p>
            <p><strong>Pressure Pattern:</strong> ${data.pressurePatternScore?.toFixed(2) ?? 0}</p>
            <p><strong>Stroke Consistency:</strong> ${data.strokeConsistencyScore?.toFixed(2) ?? 0}</p>
            <p><strong>Forgery Probability:</strong> ${data.forgeryProbability?.toFixed(2) ?? 0}</p>
            <p><strong>Processing Time:</strong> ${data.processingTimeMs ?? 0} ms</p>
        </div>`;
    }

    results[type].innerHTML = html;
    mirrors[type].innerHTML = `<p><em>Latest Result:</em> ${data.message || data.decision || 'Result received'}</p>`;
}

/* =========================
   Button Event Listeners
========================= */
buttons.image.addEventListener("click", () => analyzeFile("image"));
buttons.video.addEventListener("click", () => analyzeFile("video"));
buttons.signature.addEventListener("click", () => analyzeFile("signature"));