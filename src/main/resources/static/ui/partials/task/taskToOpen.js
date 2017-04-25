app.controller('taskToOpenCtrl', ['PersonService', 'TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (PersonService, TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.buffer = {};
        $scope.task = task;

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskService.openTaskOnPerson($scope.task.id, $scope.buffer.taskTo.person.id, $scope.buffer.message).then(function (data) {
                $uibModalInstance.dismiss('cancel');
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);