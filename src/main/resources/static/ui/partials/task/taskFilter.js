app.controller('taskFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'taskType',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance, title, taskType) {

        $scope.modalTitle = title;
        $scope.buffer.taskType = taskType;

        $scope.submit = function () {
            $uibModalInstance.close($scope.buffer);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);