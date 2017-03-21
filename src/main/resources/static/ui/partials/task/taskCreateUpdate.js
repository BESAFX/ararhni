app.controller('taskCreateUpdateCtrl', ['TaskService', 'PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance', 'title', 'action', 'task',
    function (TaskService, PersonService, $scope, $rootScope, $timeout, $uibModalInstance, title, action, task) {

        $scope.title = title;

        $scope.action = action;

        $scope.task = task;

        $scope.buffer = {};

        $scope.toPersonList = [];

        $scope.fetchPersonData = function () {
            $rootScope.showToast("جاري تحميل حسابات الموظفين، فضلاً انتظر قليلاً");
            PersonService.findPersonUnderMe().then(function (data) {
                $scope.persons = data;
                $rootScope.showToast("تم التحميل بنجاح، يمكنك متابعة عملك الآن");
            })
        };

        if (action === 'create') {
            $timeout(function () {
                $scope.fetchPersonData();
            }, 1500);
        }

        $scope.submit = function () {
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    var taskTos = [];
                    for (var i = 0; i < $scope.toPersonList.length; i++) {
                        var taskTo = {};
                        taskTo.person = $scope.toPersonList[i];
                        taskTos.push(taskTo);
                    }
                    $scope.task.taskTos = taskTos;
                    TaskService.create($scope.task).then(function (data) {
                        $scope.task = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن");
                    });
                    break;
                case 'update' :
                    TaskService.update($scope.task).then(function (data) {
                        $scope.task = data;
                        $rootScope.showToast("تم إنجاز العمل بنجاح، يمكنك القيام بعملية آخرى الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);