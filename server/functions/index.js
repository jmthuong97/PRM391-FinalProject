const Express = require('./helpers/Express');
const functions = require('firebase-functions');

const authTrigger = require('./triggers/AuthTrigger');
const userRoute = require('./routes/userRoute');
const groupRoute = require('./routes/groupRoute');
const braintreeRoute = require('./routes/braintreeRoute');

// exports.user = functions.https.onRequest(new Express('/', userRoute).app);
// exports.group = functions.https.onRequest(new Express('/', groupRoute).app);
exports.paypal = functions.https.onRequest(new Express('/', braintreeRoute).app);

// ==> triggers <==
exports.createUser = authTrigger.onCreateUser;
