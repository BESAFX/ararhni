app.factory("TaskService",
    ['$http', '$log', '$rootScope', function ($http, $log, $rootScope) {
        return {
            findAll: function () {
                return $http.get("/api/task/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/task/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (task) {
                return $http.post("/api/task/create", task).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/task/delete/" + id);
            },
            update: function (task) {
                return $http.put("/api/task/update", task).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/task/count").then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/task/filter?" + search).then(function (response) {
                    return response.data;
                });
            },
            reportFilteredTasks: function (search, reportProp) {
                $http.post('/report/dynamic/filteredTasks?' + search, reportProp, {responseType: 'arraybuffer'})
                    .success(function (data) {
                        $rootScope.showNotify("نماذج الطباعة", "تم تهيئة التقرير بنجاح، يمكنك حفظ التقرير وإستخراجه الآن", "success", "fa-black-tie");
                        var blob = new Blob([data], {type: 'application/' + reportProp.exportType});
                        saveAs(blob, 'Report.' + reportProp.exportType);
                    });
            },
            reportTask: function (id, reportProp) {
                $http.post('/report/dynamic/task?id=' + id, reportProp, {responseType: 'arraybuffer'})
                    .success(function (data) {
                        $rootScope.showNotify("نماذج الطباعة", "تم تهيئة التقرير بنجاح، يمكنك حفظ التقرير وإستخراجه الآن", "success", "fa-black-tie");
                        var blob = new Blob([data], {type: 'application/' + reportProp.exportType});
                        saveAs(blob, 'Report.' + reportProp.exportType);
                    });
            },
            requestClose: function (task) {
                return $http.post("/api/task/requestClose", task).then(function (response) {
                    return response.data;
                });
            },
            setProgress: function (task, progress) {
                return $http.post("/api/task/setProgress/" + progress, task).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);