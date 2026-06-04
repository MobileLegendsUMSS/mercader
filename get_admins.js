const { MongoClient } = require('mongodb');
const uri = "mongodb+srv://202300124_db_user:sLb0Awdb8HZ4KSdn@mercader.oilstjh.mongodb.net/mercader_db?appName=Mercader";

async function main() {
    const client = new MongoClient(uri);
    try {
        await client.connect();
        const db = client.db("mercader_db");
        
        const roles = await db.collection("rol").find({}).toArray();
        const adminRole = roles.find(r => r.nombre_rol === "admin");
        if (adminRole) {
            const admins = await db.collection("usuario").find({ id_rol: adminRole._id }).toArray();
            console.log("\n--- ADMINS FOUND ---");
            admins.forEach(a => console.log("Email:", a.correo));
        }

        const superAdminRole = roles.find(r => r.nombre_rol === "superadmin");
        if (superAdminRole) {
            const superAdmins = await db.collection("usuario").find({ id_rol: superAdminRole._id }).toArray();
            console.log("\n--- SUPER ADMINS FOUND ---");
            superAdmins.forEach(a => console.log("Email:", a.correo));
        }
    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}
main();
