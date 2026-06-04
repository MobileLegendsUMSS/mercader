const { MongoClient } = require('mongodb');
const bcrypt = require('bcryptjs');
const uri = "mongodb+srv://202300124_db_user:sLb0Awdb8HZ4KSdn@mercader.oilstjh.mongodb.net/mercader_db?appName=Mercader";

async function main() {
    const client = new MongoClient(uri);
    try {
        await client.connect();
        const db = client.db("mercader_db");
        
        const roles = await db.collection("rol").find({}).toArray();
        const adminRole = roles.find(r => r.nombre_rol === "admin");
        
        if (adminRole) {
            console.log("Admin Role ID:", adminRole._id);
            
            const hash = await bcrypt.hash("admin123", 10);
            
            const result = await db.collection("usuario").updateOne(
                { correo: "admin@mercader.com" },
                { 
                    $set: { 
                        nombres: "Super",
                        apellidos: "Admin",
                        correo: "admin@mercader.com",
                        contrasena: hash,
                        id_rol: adminRole._id,
                        puntos_merca: 9999
                    } 
                },
                { upsert: true }
            );
            console.log("Upserted admin@mercader.com:", result.upsertedId ? "Created" : "Updated");
        }
    } catch (e) {
        console.error("Error:", e.message);
    } finally {
        await client.close();
    }
}
main();
