app.controller('taskToCreateCtrl', ['PersonService', 'TaskToService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'task',
    function (PersonService, TaskToService, $scope, $rootScope, $timeout, $log, $uibModalInstance, task) {

        $scope.taskTo = {};
        $scope.taskTo.task = task;

        $timeout(function () {
            PersonService.findPersonUnderMe().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("المهام", "تم التحميل بنجاح، يمكنك متابعة عملك الآن", "success", "fa-black-tie");
            })
        }, 1500);

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            TaskToService.create($scope.taskTo).then(function (data) {
                $scope.taskTo = {};
                $scope.taskTo.task = task;
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