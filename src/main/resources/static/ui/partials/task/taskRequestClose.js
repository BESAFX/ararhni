app.controller('taskRequestCloseCtrl', ['TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.submit = function () {
            $rootScope.showToast( "جاري القيام بالعملية، فضلاً انتظر قليلاً");
            var taskCloseRequests = [];
            taskCloseRequests.push($scope.taskCloseRequest);
            $scope.task.taskCloseRequests = taskCloseRequests;
            TaskService.requestClose($scope.task).then(function () {
                $scope.taskCloseRequest = {};
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showToast("تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);