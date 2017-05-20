app.controller('taskCreateUpdateCtrl', ['TaskService', 'PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance', 'title', 'action', 'task',
    function (TaskService, PersonService, $scope, $rootScope, $timeout, $uibModalInstance, title, action, task) {

        $scope.title = title;

        $scope.action = action;

        $scope.task = task;

        $scope.buffer = {};

        $scope.buffer.structure = {};

        $scope.buffer.toPersonList = [];

        if (action === 'create') {
            $timeout(function () {
                PersonService.findPersonUnderMeSummery().then(function (data) {
                    $scope.persons = data;
                })
            }, 1500);
        }

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    var personsList = [];
                    for (var i = 0; i < $scope.buffer.toPersonList.length; i++) {
                        personsList[i] = $scope.buffer.toPersonList[i].id;
                    }
                    TaskService.create($scope.task, personsList, $scope.buffer.structure).then(function (data) {
                        $scope.task = {};
                        if ($scope.form) {
                            $scope.form.$setPristine();
                        }
                    });
                    break;
                case 'update' :
                    TaskService.update($scope.task).then(function (data) {
                        $scope.task = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);