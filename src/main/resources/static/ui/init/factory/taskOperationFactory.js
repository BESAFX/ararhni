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
            clearCounters: function (taskId, personId) {
                return $http.get("/api/taskOperation/clearCounters/" + taskId + "/" + personId).then(function (response) {
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
            },
            findByTaskAndType: function (task, type) {
                return $http.get("/api/taskOperation/findByTaskAndType/" + task.id + "/" + type).then(function (response) {
                    return response.data;
                });
            },
            findIncomingOperationsForMe: function (timeType) {
                return $http.get("/api/taskOperation/findIncomingOperationsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            },
            findOutgoingOperationsForMe: function (timeType) {
                return $http.get("/api/taskOperation/findOutgoingOperationsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);