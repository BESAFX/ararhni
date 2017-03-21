app.controller('departmentCreateUpdateCtrl', ['DepartmentService', 'BranchService', 'PersonService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'department',
    function (DepartmentService, BranchService, PersonService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, department) {

        $scope.fetchBranchData = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $rootScope.showToast("تم تحميل بيانات الفروع بنجاح");
            });
        };

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showToast("تم تحميل بيانات المستخدمين بنجاح");
            });
        };

        $timeout(function () {
            $rootScope.showToast("جاري تحميل بيانات الفروع والمستخدمين، فضلاَ انتظر قليلاً");
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
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    DepartmentService.create($scope.department).then(function (data) {
                        $scope.department = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة قسم آخر الآن");
                    });
                    break;
                case 'update' :
                    DepartmentService.update($scope.department).then(function (data) {
                        $scope.department = data;
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);