app.controller('taskDetailsCtrl',
    ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
        function ($scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

            $scope.task = task;

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };

        }]);