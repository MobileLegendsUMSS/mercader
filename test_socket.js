const https = require('https');

const data = JSON.stringify({
  allGames: false,
  order: "descendente",
  amount: 10
});

const options = {
  hostname: 'mercader-server.onrender.com',
  port: 443,
  path: '/api/juegos/sistema/visitados',
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
    // No auth needed to see if it returns 401 or the actual json if we can skip auth?
    // Wait, it requires token! But without token it returns 401. Let me just test if it works with curl first, if I don't have token I can't.
  }
};

const req = https.request(options, res => {
  let body = '';
  res.on('data', d => {
    body += d;
  });
  res.on('end', () => {
      console.log("Status:", res.statusCode);
      console.log("Body:", body);
  });
});

req.on('error', error => {
  console.error(error);
});

req.write(data);
req.end();
