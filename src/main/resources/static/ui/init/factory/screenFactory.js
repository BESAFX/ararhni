app.factory("ScreenService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/screen/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/screen/findOne/" + id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);