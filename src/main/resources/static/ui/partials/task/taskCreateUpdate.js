app.controller('taskCreateUpdateCtrl', ['TaskService', 'PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance', 'title', 'action', 'task',
    function (TaskService, PersonService, $scope, $rootScope, $timeout, $uibModalInstance, title, action, task) {

        $scope.title = title;

        $scope.action = action;

        $scope.task = task;

        $scope.buffer = {};

        $scope.toPersonList = [];

        $scope.fetchPersonData = function () {
            $rootScope.showNotify("المهام", "جاري تحميل حسابات الموظفين، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
            PersonService.findPersonUnderMe().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("المهام", "تم التحميل بنجاح، يمكنك متابعة عملك الآن", "success", "fa-black-tie");
            })
        };

        if (action === 'create') {
            $timeout(function () {
                $scope.fetchPersonData();
            }, 1500);
        }

        $scope.submit = function () {
            $rootScope.showNotify("المهام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-black-tie");
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
                        if ($scope.form) {
                            $scope.form.$setPristine();
                        }
                        $rootScope.showNotify("المهام", "تم اضافة المهمة بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
                    });
                    break;
                case 'update' :
                    TaskService.update($scope.task).then(function (data) {
                        $scope.task = data;
                        $rootScope.showNotify("المهام", "تم تعديل بيانات المهمة بنجاح، يمكنك القيام بعملية آخرى الآن", "success", "fa-black-tie");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);