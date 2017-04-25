app.factory("TaskDeductionService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/taskDeduction/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/taskDeduction/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            clearCounters: function (taskId, personId) {
                return $http.get("/api/taskDeduction/clearCounters/" + taskId + "/" + personId).then(function (response) {
                    return response.data;
                });
            },
            clearAllCounters: function (taskId) {
                return $http.get("/api/taskDeduction/clearAllCounters/" + taskId).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/taskDeduction/count").then(function (response) {
                    return response.data;
                });
            },
            findByTask: function (task) {
                return $http.get("/api/taskDeduction/findByTask/" + task.id).then(function (response) {
                    return response.data;
                });
            },
            findByTaskAndType: function (task, type) {
                return $http.get("/api/taskDeduction/findByTaskAndType/" + task.id + "/" + type).then(function (response) {
                    return response.data;
                });
            },
            findIncomingDeductionsForMe: function (timeType) {
                return $http.get("/api/taskDeduction/findIncomingDeductionsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            },
            findOutgoingDeductionsForMe: function (timeType) {
                return $http.get("/api/taskDeduction/findOutgoingDeductionsForMe/" + timeType).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);