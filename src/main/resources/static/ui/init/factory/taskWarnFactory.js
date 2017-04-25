app.factory("TaskWarnService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/taskWarn/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/taskWarn/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            clearCounters: function (taskId, personId) {
                return $http.get("/api/taskWarn/clearCounters/" + taskId + "/" + personId).then(function (response) {
                    return response.data;
                });
            },
            clearAllCounters: function (taskId) {
                return $http.get("/api/taskWarn/clearAllCounters/" + taskId).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/taskWarn/count").then(function (response) {
                    return response.data;
                });
            },
            findByTask: function (task) {
                return $http.get("/api/taskWarn/findByTask/" + task.id).then(function (response) {
                    return response.data;
                });
            },
            findByTaskAndType: function (task, type) {
                return $http.get("/api/taskWarn/findByTaskAndType/" + task.id + "/" + type).then(function (response) {
                    return response.data;
                });
            },
            findIncomingWarnsForMe: function (timeType) {
                return $http.get("/api/taskWarn/findIncomingWarnsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            },
            findOutgoingWarnsForMe: function (timeType) {
                return $http.get("/api/taskWarn/findOutgoingWarnsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);