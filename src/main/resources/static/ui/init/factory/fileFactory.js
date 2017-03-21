app.factory("FileService",
    ['$http', function ($http) {
        return {
            getSharedLink: function (path) {
                return $http.get("/getSharedLink?" + 'path=' + path).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);