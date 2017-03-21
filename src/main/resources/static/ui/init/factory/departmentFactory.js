app.factory("DepartmentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/department/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/department/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (department) {
                return $http.post("/api/department/create", department).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/department/delete/" + id);
            },
            update: function (department) {
                return $http.put("/api/department/update", department).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/department/count").then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/department/fetchTableData").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);