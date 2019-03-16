const admin = require('firebase-admin');
const serviceAccount = require("../configuration/serviceAccountKey.json");

const FirebaseAdmin = !admin.apps.length ?
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        databaseURL: "https://chat-application-8c618.firebaseio.com"
    }) : admin.app();

const Database = FirebaseAdmin.database();

module.exports = {
    FirebaseAdmin,
    Database
};
