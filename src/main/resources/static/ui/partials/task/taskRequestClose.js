app.controller('taskRequestCloseCtrl', ['TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task', 'title', 'type',
    function (TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task, title, type) {

        $scope.task = task;

        $scope.title = title;

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            var taskCloseRequests = [];
            taskCloseRequests.push($scope.taskCloseRequest);
            $scope.task.taskCloseRequests = taskCloseRequests;
            $scope.task.taskCloseRequests[0].type = type;
            TaskService.requestClose($scope.task).then(function () {
                $scope.taskCloseRequest = {};
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showNotify("المهام", "تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);