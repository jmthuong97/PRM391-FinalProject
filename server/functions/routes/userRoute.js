const express = require("express");
const Router = express.Router();
const userController = require('../controllers/userController');

Router.get('/:groupId', userController.getAllUsersByGroupId);

module.exports = Router;
