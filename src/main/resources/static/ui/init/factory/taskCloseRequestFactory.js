app.factory("TaskCloseRequestService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/taskCloseRequest/findAll").then(function (response) {
                    return response.data;
                })
            },
            create: function (taskCloseRequest) {
                return $http.post("/api/taskCloseRequest/create", taskCloseRequest).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/taskCloseRequest/delete/" +  id);
            },
            count: function () {
                return $http.get("/api/taskCloseRequest/count").then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/taskCloseRequest/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);