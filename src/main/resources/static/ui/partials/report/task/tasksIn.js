app.controller('tasksInCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'tasks',
    function ($scope, $rootScope, $timeout, $uibModalInstance, tasks) {

        $scope.tasks = tasks;
        $scope.buffer = {};
        $scope.buffer.tasksList = tasks;

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.tasksList.length; i++) {
                listId[i] = $scope.buffer.tasksList[i].id;
            }
            window.open('/report/Tasks?'
                + "tasksList=" + listId);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);