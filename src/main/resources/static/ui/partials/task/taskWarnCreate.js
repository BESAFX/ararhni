app.controller('taskWarnCreateCtrl', ['TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.buffer = {};

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskService.addWarn($scope.task.id, $scope.buffer.taskTo.person.id, $scope.buffer.content).then(function (data) {
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

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);