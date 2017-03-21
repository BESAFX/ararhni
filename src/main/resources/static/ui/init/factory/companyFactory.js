app.factory("CompanyService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/company/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/company/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (company) {
                return $http.post("/api/company/create", company).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/company/delete/" + id);
            },
            update: function (company) {
                return $http.put("/api/company/update", company).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/company/count").then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/company/fetchTableData").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);