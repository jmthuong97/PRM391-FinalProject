const Express = require('./helpers/Express');
const functions = require('firebase-functions');

const authTrigger = require('./triggers/AuthTrigger');
const userRoute = require('./routes/userRoute');
const groupRoute = require('./routes/groupRoute');

exports.user = functions.https.onRequest(new Express('/', userRoute).app);
exports.group = functions.https.onRequest(new Express('/', groupRoute).app);

// ==> triggers <==
exports.createUser = authTrigger.onCreateUser;

