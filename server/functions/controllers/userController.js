const {Firestore} = require('../helpers/firebaseApp');
const HttpResponse = require('../helpers/HttpResponse');
const userRef = Firestore.collection('users');
const groupRef = Firestore.collection('groups');

module.exports = {
    getAllUsersByGroupId: async (req, res) => {
        groupRef.doc(req.params.groupId).get()
            .then(group => {
                if (group.exists) {
                    let members = group.get("members");
                    let createPromise = [];
                    members.map(userId => createPromise.push(userRef.doc(userId).get()));
                    Promise.all(createPromise)
                        .then(users => {
                            let dataUsers = users.map(user => {
                                return user.data();
                            });
                            res.send(HttpResponse.ok(dataUsers));
                        })
                        .catch(error => res.send(HttpResponse.badRequestError(error)));
                } else res.send(HttpResponse.badRequestError({
                    message: {message: "Group not found."}
                }))
            }).catch(error => res.send(HttpResponse.badRequestError(error)))
    }
};
