app.controller('taskRequestCloseCtrl', ['TaskCloseRequestService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task', 'title', 'type',
    function (TaskCloseRequestService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task, title, type) {

        $scope.task = task;

        $scope.taskCloseRequest = {};

        $scope.title = title;

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            $scope.taskCloseRequest.task = task;
            $scope.taskCloseRequest.type = type;
            TaskCloseRequestService.create($scope.taskCloseRequest).then(function () {
                $scope.taskCloseRequest = {};
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $rootScope.showNotify("المهام", "تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);