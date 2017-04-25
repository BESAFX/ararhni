app.controller('taskToCreateCtrl', ['PersonService', 'TaskService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (PersonService, TaskService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.buffer = {};
        $scope.task = task;

        $timeout(function () {
            PersonService.findPersonUnderMeSummery().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("المهام", "تم التحميل بنجاح، يمكنك متابعة عملك الآن", "success", "fa-black-tie");
            })
        }, 1500);

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskService.addPerson($scope.task.id, $scope.buffer.person.id, $scope.buffer.message).then(function (data) {
                $scope.task = data;
                if ($scope.form) {
                    $scope.form.$setPristine();
                }
                $uibModalInstance.dismiss('cancel');
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);
    }]);