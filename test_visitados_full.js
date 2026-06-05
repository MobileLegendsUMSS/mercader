const { MongoClient } = require('mongodb');
const bcrypt = require('bcryptjs');
const https = require('https');

const uri = "mongodb+srv://202300124_db_user:sLb0Awdb8HZ4KSdn@mercader.oilstjh.mongodb.net/mercader_db?appName=Mercader";

async function main() {
  const client = new MongoClient(uri);
  await client.connect();
  const db = client.db("mercader_db");
  
  // Insert test user
  const passwordHash = await bcrypt.hash("test1234", 10);
  const testUser = {
    nombre: "testuser_temp",
    correo: "testuser@temp.com",
    contrasenna: passwordHash,
    activo: true
  };
  
  // Clean up if exists
  await db.collection("usuario").deleteOne({ nombre: "testuser_temp" });
  await db.collection("usuario").insertOne(testUser);
  
  // Now login
  const postData = JSON.stringify({ nombre: "testuser_temp", contrasenna: "test1234" });
  const req = https.request({
    hostname: 'mercader-server.onrender.com', port: 443, path: '/api/auth/login', method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Content-Length': postData.length }
  }, res => {
    let body = '';
    res.on('data', d => body += d);
    res.on('end', () => {
      const data = JSON.parse(body);
      const token = data.data.token;
      
      const getData = JSON.stringify({ allGames: false, order: "descendente", amount: 10 });
      const req2 = https.request({
        hostname: 'mercader-server.onrender.com', port: 443, path: '/api/juegos/sistema/visitados', method: 'GET',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token, 'Content-Length': getData.length }
      }, res2 => {
        let body2 = '';
        res2.on('data', d => body2 += d);
        res2.on('end', async () => {
          console.log(body2);
          // Clean up
          await db.collection("usuario").deleteOne({ nombre: "testuser_temp" });
          await client.close();
        });
      });
      req2.write(getData);
      req2.end();
    });
  });
  req.write(postData);
  req.end();
}
main().catch(console.error);
