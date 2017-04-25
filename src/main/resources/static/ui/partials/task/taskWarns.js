app.controller('taskWarnsCtrl', ['ModalProvider', 'TaskWarnService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, TaskWarnService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.openCreateWarnModel = function () {
            ModalProvider.openTaskWarnCreateModel($scope.task);
        };

        $scope.findTaskWarns = function () {
            TaskWarnService.findByTask($scope.task).then(function (data) {
                $scope.task.taskWarns = data;
            })
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);