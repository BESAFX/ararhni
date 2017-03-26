app.controller('departmentCreateUpdateCtrl', ['DepartmentService', 'BranchService', 'PersonService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'department',
    function (DepartmentService, BranchService, PersonService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, department) {

        $scope.fetchBranchData = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $rootScope.showNotify("الاقسام", "تم تحميل بيانات الفروع بنجاح", "success", "fa-sitemap");
            });
        };

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("الاقسام", "تم تحميل بيانات المستخدمين بنجاح", "success", "fa-sitemap");
            });
        };

        $timeout(function () {
            $rootScope.showNotify("الاقسام", "جاري تحميل بيانات الفروع والمستخدمين، فضلاَ انتظر قليلاً", "warning", "fa-sitemap");
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
            $rootScope.showNotify("الاقسام", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-sitemap");
            switch ($scope.action) {
                case 'create' :
                    DepartmentService.create($scope.department).then(function (data) {
                        $scope.department = {};
                        $scope.form.$setPristine();
                        $rootScope.showNotify("الاقسام", "تم القيام بالعملية بنجاح، يمكنك اضافة قسم آخر الآن", "success", "fa-sitemap");
                    });
                    break;
                case 'update' :
                    DepartmentService.update($scope.department).then(function (data) {
                        $scope.department = data;
                        $rootScope.showNotify("الاقسام", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن", "success", "fa-sitemap");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);