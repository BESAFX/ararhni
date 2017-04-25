app.controller('taskDeductionsCtrl', ['ModalProvider', 'TaskDeductionService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, TaskDeductionService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.openCreateDeductionModel = function () {
            ModalProvider.openTaskDeductionCreateModel($scope.task);
        };

        $scope.findTaskDeductions = function () {
            TaskDeductionService.findByTask($scope.task).then(function (data) {
                $scope.task.taskDeductions = data;
            })
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);