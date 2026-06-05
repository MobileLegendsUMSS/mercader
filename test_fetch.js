const http = require('http');
const https = require('https');

const postData = JSON.stringify({ correo: "sebas@test.com", contrasenia: "1234" });

const req = https.request({
  hostname: 'mercader-server.onrender.com',
  port: 443,
  path: '/api/auth/login',
  method: 'POST',
  headers: { 'Content-Type': 'application/json', 'Content-Length': postData.length }
}, res => {
  let body = '';
  res.on('data', d => body += d);
  res.on('end', () => {
    const token = JSON.parse(body).data.token;
    
    // Now call visitados with GET and body
    const getData = JSON.stringify({ allGames: false, order: "descendente", amount: 10 });
    const req2 = https.request({
      hostname: 'mercader-server.onrender.com',
      port: 443,
      path: '/api/juegos/sistema/visitados',
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token,
        'Content-Length': getData.length
      }
    }, res2 => {
      let body2 = '';
      res2.on('data', d => body2 += d);
      res2.on('end', () => console.log(body2.substring(0, 1500)));
    });
    req2.write(getData);
    req2.end();
  });
});
req.write(postData);
req.end();
