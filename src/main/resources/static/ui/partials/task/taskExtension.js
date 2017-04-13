app.controller('taskExtensionCtrl', ['TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;
        $scope.buffer = {};

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-battery");
            TaskService.extendEndDate($scope.task, $scope.buffer.days).then(function (data) {
                $scope.task = data;
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showNotify("المهام", "تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-battery");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);