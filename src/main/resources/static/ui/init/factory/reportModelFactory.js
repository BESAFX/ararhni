app.factory("ReportModelService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/reportModel/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/reportModel/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (reportModel) {
                return $http.post("/api/reportModel/create", reportModel).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/reportModel/delete/" + id);
            },
            update: function (reportModel) {
                return $http.put("/api/reportModel/update", reportModel).then(function (response) {
                    return response.data;
                });
            },
            findByScreen: function (id) {
                return $http.get("/api/reportModel/findByScreen/" + id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);