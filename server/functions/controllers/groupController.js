const {Firestore} = require('../helpers/firebaseApp');
const HttpResponse = require('../helpers/HttpResponse');
const groupRef = Firestore.collection('groups');

module.exports = {
    createNewGroup: async (req, res) => {
        if (!req.body.displayName) res.send(HttpResponse.badRequestError({
            message: {
                displayName: "DisplayName cannot be blank."
            }
        }));

        groupRef.add({
            id: groupRef.doc().id,
            displayName: req.body.displayName,
            imageURL: req.body.imageURL === undefined ? "" : req.body.imageURL,
            description: req.body.description === undefined ? "" : req.body.description,
            mainColor: req.body.mainColor === undefined ? "" : req.body.mainColor,
            members: [req.user.uid],
            status: true
        }).then(docRef => res.send(HttpResponse.ok({id: docRef.id})))
            .catch(error => res.send(HttpResponse.badRequestError(error)))
    },
    getGroupById: async (req, res) => {
        const groupId = req.params.groupId;
        groupRef.doc(groupId).get()
            .then(group => res.send(HttpResponse.ok(group.data())))
            .catch(error => res.send(HttpResponse.badRequestError(error)))
    },
    getAllGroups: async (req, res) => {
        const uid = req.user.uid;
        groupRef.where('members', 'array-contains', uid).get()
            .then(groups => {
                let dataGroups = [];
                groups.forEach(group => dataGroups.push(group.data()));
                res.send(HttpResponse.ok(dataGroups))
            })
            .catch(error => res.send(HttpResponse.badRequestError(error)))
    },
};
