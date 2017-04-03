app.controller('clearCountersCtrl', ['TaskOperationService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskOperationService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;
        $scope.buffer = {};

        $scope.submit = function () {
            $scope.buffer.taskTo.task = {"id": $scope.task.id};
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskOperationService.clearCounters($scope.buffer.taskTo.task.id, $scope.buffer.taskTo.person.id).then(function (data) {
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