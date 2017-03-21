app.factory("TaskOperationService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/taskOperation/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/taskOperation/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (taskOperation) {
                return $http.post("/api/taskOperation/create", taskOperation).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/taskOperation/count").then(function (response) {
                    return response.data;
                });
            },
            findByTask: function (task) {
                return $http.get("/api/taskOperation/findByTask/" + task.id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);