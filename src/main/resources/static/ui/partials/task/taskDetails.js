app.controller('taskDetailsCtrl', ['ModalProvider', 'TaskOperationService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, TaskOperationService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.openCreateOperationModel = function () {
            ModalProvider.openTaskOperationCreateModel($scope.task);
        };

        $scope.findTaskOperations = function () {
            TaskOperationService.findByTask($scope.selected).then(function (data) {
                $scope.task.taskOperations = data;
            })
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);