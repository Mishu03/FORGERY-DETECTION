// test-image.js
import fs from 'fs';
import fetch from 'node-fetch';
import FormData from 'form-data';

const IMAGE_PATH = './test-data/image.jpg'; // Replace with your test image path
const API_URL = 'http://localhost:8080/api/detection/image'; // Your backend endpoint

async function testImage() {
    const form = new FormData();
    form.append('file', fs.createReadStream(IMAGE_PATH));

    try {
        const res = await fetch(API_URL, {
            method: 'POST',
            body: form
        });

        const data = await res.json();

        console.log('--- Backend Response ---');
        console.log(`Decision: ${data.decision}`);
        console.log(`Forgery Probability: ${data.forgeryProbability}`);
        console.log(`ELA Score: ${data.elaScore}`);
        console.log(`Noise Score: ${data.noiseAnalysisScore}`);
        console.log(`Metadata Score: ${data.metadataConsistencyScore}`);
        console.log(`Similarity Score: ${data.similarityScore}`);
        console.log(`Processing Time: ${data.processingTimeMs} ms`);
        if (data.elaHeatmapBase64) console.log('ELA Heatmap Base64: Present');
        else console.log('ELA Heatmap Base64: None');

    } catch (err) {
        console.error('Error testing image:', err);
    }
}

testImage();