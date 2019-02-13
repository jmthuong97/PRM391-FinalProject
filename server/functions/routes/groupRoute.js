const express = require("express");
const Router = express.Router();
const groupController = require('../controllers/groupController');

Router.post('/', groupController.createNewGroup);
Router.get('/', groupController.getAllGroups);
Router.get('/:groupId', groupController.getGroupById);

module.exports = Router;
