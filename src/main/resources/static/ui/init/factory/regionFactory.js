app.factory("RegionService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/region/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/region/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (region) {
                return $http.post("/api/region/create", region).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/region/delete/" + id);
            },
            update: function (region) {
                return $http.put("/api/region/update", region).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/region/count").then(function (response) {
                    return response.data;
                });
            },
            findByName: function (name) {
                return $http.get("/api/region/findByName/" + name).then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/region/fetchTableData").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);