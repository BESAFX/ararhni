app.controller('departmentCreateUpdateCtrl', ['DepartmentService', 'BranchService', 'PersonService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'department',
    function (DepartmentService, BranchService, PersonService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, department) {

        $scope.fetchBranchData = function () {
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
            });
        };

        $scope.fetchPersonData = function () {
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
            });
        };

        $timeout(function () {
            $scope.fetchBranchData();
            $scope.fetchPersonData();
        }, 1500);

        if (department) {
            $scope.department = department;
        } else {
            $scope.department = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    DepartmentService.create($scope.department).then(function (data) {
                        $scope.department = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    DepartmentService.update($scope.department).then(function (data) {
                        $scope.department = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);