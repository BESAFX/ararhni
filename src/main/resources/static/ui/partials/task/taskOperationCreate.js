app.controller('taskOperationCreateCtrl', ['TaskService', 'TaskOperationService', 'TaskOperationAttachService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskService, TaskOperationService, TaskOperationAttachService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.taskOperation = {};

        $scope.taskOperation.task = task;

        $scope.taskOperation.task.tos = task.taskTos.map(function (a) {
            return a.person.name
        });

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadFileAndGetShared'
        });

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var taskOperationAttaches = [];
            for (var i = 0; i < $scope.uploader.queue.length; i++) {
                var taskOperationAttach = {};
                taskOperationAttach.name = $scope.uploader.queue[i].file.name;
                taskOperationAttach.link = $scope.uploader.queue[i].file.url;
                taskOperationAttaches.push(taskOperationAttach);
            }
            $scope.taskOperation.taskOperationAttaches = taskOperationAttaches;
            $scope.taskOperation.type = 1;
            TaskOperationService.create($scope.taskOperation).then(function (data) {
                $scope.taskOperation = {};
                $scope.taskOperation.task = task;
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showNotify("المهام", "تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item, options) {
                return this.queue.length < 10;
            }
        });

        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item, options, deferred) {
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            fileItem.file.url = response;
        };

    }]);