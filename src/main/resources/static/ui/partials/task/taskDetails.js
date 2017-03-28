app.controller('taskDetailsCtrl', ['ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.openCreateOperationModel = function () {
            ModalProvider.openTaskOperationCreateModel($scope.task);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);