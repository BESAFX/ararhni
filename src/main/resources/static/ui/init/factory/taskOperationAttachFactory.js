app.factory("TaskOperationAttachService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/taskOperationAttach/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/taskOperationAttach/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (taskOperationAttach) {
                return $http.post("/api/taskOperationAttach/create", taskOperationAttach).then(function (response) {
                    return response.data;
                });
            },
            remove: function (taskOperationAttach) {
                return $http.delete("/api/taskOperationAttach/delete/" + taskOperationAttach.id);
            },
            update: function (taskOperationAttach) {
                return $http.put("/api/taskOperationAttach/update", taskOperationAttach).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);