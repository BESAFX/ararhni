app.controller('taskDetailsCtrl', ['ModalProvider', 'TaskOperationService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, TaskOperationService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.openCreateOperationModel = function () {
            ModalProvider.openTaskOperationCreateModel($scope.task);
        };

        $scope.openReportTaskOperationsModel = function () {
            ModalProvider.openTaskOperationsReportModel([$scope.task]);
        };

        $scope.findTaskOperations = function () {
            TaskOperationService.findByTask($scope.task).then(function (data) {
                $scope.task.taskOperations = data;
            })
        };

        $scope.findTaskWarnings = function () {
            TaskOperationService.findByTaskAndType($scope.task, 2).then(function (data) {
                $scope.task.taskWarnings = data;
            })
        };

        $scope.findTaskDeductions = function () {
            TaskOperationService.findByTaskAndType($scope.task, 3).then(function (data) {
                $scope.task.taskDeductions = data;
            })
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);