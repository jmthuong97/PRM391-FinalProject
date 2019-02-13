const functions = require('firebase-functions');
const {Firestore} = require('../helpers/firebaseApp');
const usersRef = Firestore.collection('users');

module.exports = {
    onCreateUser: functions.auth.user().onCreate(user => {
        let dataUser = user.providerData[0];
        return usersRef.doc(user.uid).set({
            displayName: dataUser.displayName,
            email: dataUser.email,
            phoneNumber: dataUser.phoneNumber === undefined ? "" : dataUser.phoneNumber,
            photoURL: dataUser.photoURL,
            providerId: dataUser.providerId,
            uid: dataUser.uid
        });
    })
};