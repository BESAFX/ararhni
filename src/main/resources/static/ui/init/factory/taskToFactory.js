app.factory("TaskToService",
    ['$http', '$log', '$rootScope', function ($http, $log, $rootScope) {
        return {
            create: function (taskTo) {
                return $http.post("/api/taskTo/create", taskTo).then(function (response) {
                    return response.data;
                });
            },
            update: function (taskTo) {
                return $http.put("/api/taskTo/update", taskTo).then(function (response) {
                    return response.data;
                });
            },
            findByTask: function (taskId) {
                return $http.get("/api/taskTo/findByTask/" + taskId).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);