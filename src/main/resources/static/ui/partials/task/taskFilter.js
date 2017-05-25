app.controller('taskFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'taskType', 'closeType',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance, title, taskType, closeType) {

        $scope.modalTitle = title;
        $scope.buffer.taskType = taskType;
        $scope.buffer.closeType = closeType;

        $scope.submit = function () {
            $uibModalInstance.close($scope.buffer);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);