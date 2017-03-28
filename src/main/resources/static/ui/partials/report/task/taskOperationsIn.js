app.controller('taskOperationsInCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'tasks',
    function ($scope, $rootScope, $timeout, $uibModalInstance, tasks) {

        $scope.tasks = tasks;
        $scope.buffer = {};
        $scope.buffer.tasksList = [];

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.tasksList.length; i++) {
                listId[i] = $scope.buffer.tasksList[i].id;
            }
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/TaskOperations?'
                    + "tasksList=" + listId + "&"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/TaskOperations?'
                    + "tasksList=" + listId);
            }
        };


        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);