app.controller('taskDeductionCreateCtrl', ['TaskDeductionService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (TaskDeductionService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.task = task;

        $scope.buffer = {};

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            $scope.taskDeduction.type = 'Manual';
            $scope.taskDeduction.task = task;
            $scope.taskDeduction.toPerson = $scope.buffer.taskTo.person;
            TaskDeductionService.create($scope.taskDeduction).then(function (data) {
                $scope.taskDeduction = {};
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showNotify("المهام", "تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);