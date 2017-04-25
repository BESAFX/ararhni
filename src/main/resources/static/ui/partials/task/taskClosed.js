app.controller('taskClosedCtrl', ['TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;
        $scope.buffer = {};

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskService.closeTaskOnPerson($scope.task.id, $scope.buffer.taskTo.person.id, $scope.buffer.message, $scope.buffer.degree).then(function (data) {
                $scope.task = data;
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $uibModalInstance.close(true);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.close(false);
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);