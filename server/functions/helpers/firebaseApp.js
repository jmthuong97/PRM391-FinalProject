const admin = require('firebase-admin');
require('firebase/firestore');
require('firebase/firebase-functions');
const serviceAccount = require("../configuration/serviceAccountKey.json");

const FirebaseAdmin = !admin.apps.length ?
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        databaseURL: "https://chat-application-8c618.firebaseio.com"
    }) : admin.app();

const Firestore = FirebaseAdmin.firestore();

module.exports = {
    FirebaseAdmin,
    Firestore
};