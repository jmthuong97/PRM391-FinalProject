const express = require('express'),
    bodyParser = require('body-parser');
const {FirebaseAdmin} = require('./helpers/firebaseApp');
const HttpResponse = require('./helpers/HttpResponse');
const braintreeRoute = require('./routes/braintreeRoute');

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
        FirebaseAdmin.auth().verifyIdToken(idToken)
            .then(decodedToken => {
                console.log(decodedToken);
                req.uid = decodedToken.user_id;
                next();
            }).catch(err => res.send(HttpResponse.badRequestError(err)));
    } catch (error) {
        res.send(HttpResponse.unauthorizedError(error));
    }
};

app.use(authenticate);
app.disable('etag');

app.get('/test', (req, res) => {
    res.send(req.uid);
});

app.use('/paypal', braintreeRoute);

// Run server
const port = process.env.PORT || 8000;
app.listen(port, (err) => {
    if (err) console.log(err);
    console.log("App is start at port " + port);
});
