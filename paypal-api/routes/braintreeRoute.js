const express = require("express");
const Router = express.Router();
const braintreeController = require('../controllers/braintreeController');

Router.get('/client-token', braintreeController.getClientToken);
Router.post('/execute-payment', braintreeController.executePayment);

module.exports = Router;
