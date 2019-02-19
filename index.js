const express = require('express'),
    bodyParser = require('body-parser');
const {FirebaseAdmin} = require('./helpers/firebaseApp');
const HttpResponse = require('./helpers/HttpResponse');

const app = express();

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json({extended: false}));

const authenticate = async (req, res, next) => {
    if (!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ')) {
        res.send(HttpResponse.unauthorizedError({error: {message: "Unauthorized"}}));
        return;
    }
    const idToken = req.headers.authorization.split('Bearer ')[1];
    try {
        req.user = await FirebaseAdmin.auth().verifyIdToken(idToken);
        next();
    } catch (error) {
        res.send(HttpResponse.unauthorizedError(error));
    }
};

app.use(authenticate);
app.disable('etag');

const braintreeRoute = require('./routes/braintreeRoute');

app.get('/', (req, res) => {
    res.json('Welcome to Chat Application API !');
});

app.use('/paypal', braintreeRoute);

// Run server
const port = process.env.PORT || 8000;
app.listen(port, (err) => {
    if (err) console.log(err);
    console.log("App is start at port " + port);
});
