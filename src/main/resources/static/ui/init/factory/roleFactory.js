app.factory("RoleService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/role/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/role/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            setUpRoles: function (role) {
                return $http.post("/api/role/setUpRoles", role).then(function (response) {
                    return response.data;
                });
            },
            create: function (role) {
                return $http.post("/api/role/create", role).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/role/delete/" + id);
            },
            update: function (role) {
                return $http.put("/api/role/update", role).then(function (response) {
                    return response.data;
                });
            },
            findByTeam: function (teamId) {
                return $http.get("/api/role/findByTeam/" + teamId).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);