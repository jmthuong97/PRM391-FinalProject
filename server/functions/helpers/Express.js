const express = require('express');
const bodyParser = require('body-parser');
const {FirebaseAdmin} = require('../helpers/firebaseApp');
const HttpResponse = require('../helpers/HttpResponse');

module.exports = class Express {
    constructor(path, routes) {
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
        app.use(path, routes);

        this.app = app;
    }
};
