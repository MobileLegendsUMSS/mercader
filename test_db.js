const { MongoClient } = require('mongodb');
const uri = "mongodb+srv://202300124_db_user:sLb0Awdb8HZ4KSdn@mercader.oilstjh.mongodb.net/mercader_db?appName=Mercader";

async function main() {
  const client = new MongoClient(uri);
  await client.connect();
  const db = client.db("mercader_db");
  const games = await db.collection("juego").find({}, { projection: { portada: 1, titulo: 1, visitas: 1, createdAt: 1 } }).sort({createdAt: -1}).limit(5).toArray();
  console.log(games);
  await client.close();
}
main().catch(console.error);
