app.controller('taskWarnCreateCtrl', ['TaskWarnService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskWarnService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.buffer = {};

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            $scope.taskWarn.type = 'Manual';
            $scope.taskWarn.task = task;
            $scope.taskWarn.toPerson = $scope.buffer.taskTo.person;
            TaskWarnService.create($scope.taskWarn).then(function (data) {
                $scope.taskWarn = {};
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