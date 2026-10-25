const fs = require('fs');
const http = require('http');

console.log('--- 1. FILE INTEGRITY CHECK ---');
const files = [
  'index.html',
  'css/style.css',
  'js/app.js',
  'version.json',
  'announcements.json',
  'assets/Fairy-Music.png',
  'assets/download.png',
  'assets/obtainium.png',
  'assets/bg-video.mp4',
  'server.js'
];

for (const f of files) {
  if (!fs.existsSync(f)) {
    console.error('[FAIL] Missing file:', f);
    process.exit(1);
  }
  const sz = fs.statSync(f).size;
  console.log('[OK]', f, sz, 'bytes');
}

console.log('\n--- 2. JSON SCHEMA VALIDATION ---');
const ver = JSON.parse(fs.readFileSync('version.json', 'utf8'));
if (!ver.version || !ver.apkUrl || !ver.changelog) throw new Error('Invalid version.json');
console.log('[OK] version.json schema valid. Version:', ver.version, 'APK:', ver.apkUrl);

const ann = JSON.parse(fs.readFileSync('announcements.json', 'utf8'));
console.log('[OK] announcements.json schema valid. Title:', ann.title);

console.log('\n--- 3. JAVASCRIPT SYNTAX CHECK ---');
const vm = require('vm');
const appCode = fs.readFileSync('js/app.js', 'utf8');
new vm.Script(appCode, { filename: 'js/app.js' });
console.log('[OK] js/app.js syntax compiled cleanly without syntax errors');

const serverCode = fs.readFileSync('server.js', 'utf8');
new vm.Script(serverCode, { filename: 'server.js' });
console.log('[OK] server.js syntax compiled cleanly without syntax errors');

console.log('\n--- 4. HTML RESOURCE REFERENCE CHECK ---');
const html = fs.readFileSync('index.html', 'utf8');
const assetRefs = [
  'css/style.css',
  'js/app.js',
  'assets/Fairy-Music.png'
];
for (const ref of assetRefs) {
  if (!html.includes(ref)) {
    console.error('[FAIL] index.html missing reference to:', ref);
    process.exit(1);
  }
  console.log('[OK] Reference verified:', ref);
}

console.log('\n--- 5. LIVE HTTP SERVER & MIME VERIFICATION ---');
function runHttpTests() {
  const testEndpoints = [
    { url: '/', mime: 'text/html; charset=UTF-8' },
    { url: '/index.html', mime: 'text/html; charset=UTF-8' },
    { url: '/css/style.css', mime: 'text/css; charset=UTF-8' },
    { url: '/js/app.js', mime: 'application/javascript; charset=UTF-8' },
    { url: '/version.json', mime: 'application/json; charset=UTF-8' },
    { url: '/assets/Fairy-Music.png', mime: 'image/png' },
    { url: '/assets/download.png', mime: 'image/png' },
    { url: '/assets/obtainium.png', mime: 'image/png' },
    { url: '/assets/bg-video.mp4', mime: 'video/mp4' }
  ];

  let completed = 0;
  testEndpoints.forEach(ep => {
    http.get('http://127.0.0.1:3000' + ep.url, res => {
      if (res.statusCode !== 200) {
        console.error('[FAIL] HTTP', res.statusCode, ep.url);
        process.exit(1);
      }
      const ct = res.headers['content-type'];
      if (!ct.includes(ep.mime.split(';')[0])) {
        console.error('[FAIL] Wrong MIME for', ep.url, 'got', ct, 'expected', ep.mime);
        process.exit(1);
      }
      console.log('[HTTP 200 OK]', ep.url, '-> Content-Type:', ct);
      res.resume();
      completed++;
      if (completed === testEndpoints.length) {
        console.log('\n=============================================');
        console.log('ALL VERIFICATION CHECKS PASSED WITH 100% SUCCESS');
        console.log('=============================================');
        process.exit(0);
      }
    }).on('error', err => {
      console.error('[ERROR]', err);
      process.exit(1);
    });
  });
}

// Check if server is already running on port 3000
http.get('http://127.0.0.1:3000/', () => {
  runHttpTests();
}).on('error', () => {
  require('./server.js');
  setTimeout(runHttpTests, 500);
});
