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
            create: function (task, personsList, single) {
                return $http.post("/api/task/create?personsList=" + personsList + "&single=" + single, task).then(function (response) {
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
            increaseEndDate: function (taskId, days, message) {
                return $http.get("/api/task/increaseEndDate?taskId=" + taskId + "&days=" + days + "&message=" + message).then(function (response) {
                    return response.data;
                });
            },
            decreaseEndDate: function (taskId, days, message) {
                return $http.get("/api/task/decreaseEndDate?taskId=" + taskId + "&days=" + days + "&message=" + message).then(function (response) {
                    return response.data;
                });
            },
            declineRequest: function (requestId) {
                return $http.get("/api/task/declineRequest?requestId=" + requestId).then(function (response) {
                    return response.data;
                });
            },
            acceptRequest: function (requestId) {
                return $http.get("/api/task/acceptRequest?requestId=" + requestId).then(function (response) {
                    return response.data;
                });
            },
            closeTaskOnPerson: function (taskId, personId, message, degree) {
                return $http.get("/api/task/closeTaskOnPerson?taskId=" + taskId + "&personId=" + personId + "&message=" + message + "&degree=" + degree).then(function (response) {
                    return response.data;
                });
            },
            closeTaskCompletely: function (taskId) {
                return $http.get("/api/task/closeTaskCompletely?taskId=" + taskId).then(function (response) {
                    return response.data;
                });
            },
            addPerson: function (taskId, personId, message) {
                return $http.get("/api/task/addPerson?taskId=" + taskId + "&personId=" + personId + "&message=" + message).then(function (response) {
                    return response.data;
                });
            },
            removePerson: function (taskId, personId, message) {
                return $http.get("/api/task/removePerson?taskId=" + taskId + "&personId=" + personId + "&message=" + message).then(function (response) {
                    return response.data;
                });
            },
            addWarn: function (taskId, personId, message) {
                return $http.get("/api/task/addWarn?taskId=" + taskId + "&personId=" + personId + "&message=" + message).then(function (response) {
                    return response.data;
                });
            },
            addDeduction: function (taskId, personId, message, deduction) {
                return $http.get("/api/task/addDeduction?taskId=" + taskId + "&personId=" + personId + "&message=" + message + "&deduction=" + deduction).then(function (response) {
                    return response.data;
                });
            },
            openTaskOnPerson: function (taskId, personId, message) {
                return $http.get("/api/task/openTaskOnPerson?taskId=" + taskId + "&personId=" + personId + "&message=" + message).then(function (response) {
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
            setProgress: function (task, progress) {
                return $http.post("/api/task/setProgress/" + progress, task).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);